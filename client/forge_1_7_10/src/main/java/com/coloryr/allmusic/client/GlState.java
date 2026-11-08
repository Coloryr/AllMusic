package com.coloryr.allmusic.client;

import org.lwjgl.opengl.GL11;

/**
 * 1.7.10 没有 GlStateManager，HUD 绘制前手动保存 OpenGL 状态机，绘制完原样还原。
 * <p>
 * 绘制 HUD 会改动纹理绑定、纹理开关、混合开关与混合模式、深度写入、颜色、
 * 着色模型、Alpha 测试、裁剪盒、模型视图矩阵、顶点数组和像素存储；不还原会
 * 污染原版后续的 HUD / 界面渲染，表现为其他界面变暗、贴图错乱、文字发黑或裁剪残留。
 * <p>
 * 用法：
 * <pre>{@code
 * try (GlState ignored = GlState.push()) {
 *     // 绘制
 * }
 * }</pre>
 */
public final class GlState implements AutoCloseable {
    private boolean restored;

    private GlState() {
    }

    /**
     * 保存当前 GL 状态机
     *
     * @return 用于还原的状态对象
     */
    public static GlState push() {
        // GL_ALL_ATTRIB_BITS 覆盖纹理、混合、深度、颜色、Alpha 测试、裁剪、光照等全部服务端状态
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        // 顶点数组、像素存储等客户端状态由 glPushClientAttrib 保存
        GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

        // 矩阵栈不在 glPushAttrib 的保存范围里，单独保存模型视图矩阵
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        return new GlState();
    }

    /**
     * 还原 GL 状态机
     */
    @Override
    public void close() {
        if (restored) {
            return;
        }
        restored = true;

        // 绘制过程中可能改过矩阵模式，先切回模型视图再出栈
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

        // 还原客户端状态（顶点数组、像素存储等）
        GL11.glPopClientAttrib();

        // 还原 glPushAttrib 保存的全部服务端状态，包括原来的矩阵模式
        GL11.glPopAttrib();
    }
}
