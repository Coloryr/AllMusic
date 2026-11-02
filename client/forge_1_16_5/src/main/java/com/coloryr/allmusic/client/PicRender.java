package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayInputStream;

public class PicRender extends PictureFrameBuffer {
    private final DynamicTexture sourceTexture;
    private final DynamicTexture rotateTexture;

    public PicRender(int size) {
        sourceTexture = new DynamicTexture(size, size, false);
        rotateTexture = new DynamicTexture(size, size, false);
    }

    @Override
    public void dispose() {
        sourceTexture.releaseId();
        rotateTexture.releaseId();
    }

    @Override
    public void update(byte[] source, byte[] rotate) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(source);
            NativeImage image1 = NativeImage.read(stream);
            sourceTexture.setPixels(image1);
            sourceTexture.upload();

            stream = new ByteArrayInputStream(rotate);
            NativeImage image2 = NativeImage.read(stream);
            rotateTexture.setPixels(image2);
            rotateTexture.upload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(boolean rotate, int size, float x, float y, int ang, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(size, size, x, y, dir);

        RenderSystem.bindTexture(rotate ? rotateTexture.getId() : sourceTexture.getId());
        RenderSystem.enableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL30.GL_ALWAYS);

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL30.GL_ALWAYS);

        Matrix4f matrix;

        int a = size / 2;

        matrix = Matrix4f.createTranslateMatrix(point.x + a, point.y + a, 0);

        if (rotate) {
            matrix.multiply(new Quaternion(0, 0, ang, true));
        }

        float x0 = -a;
        float x1 = a;
        float y0 = -a;
        float y1 = a;
        float z = 0;
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferBuilder.vertex(matrix, x0, y1, z).color(1.0f, 1.0f, 1.0f, alpha).uv(u0, v1).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, z).color(1.0f, 1.0f, 1.0f, alpha).uv(u1, v1).endVertex();
        bufferBuilder.vertex(matrix, x1, y0, z).color(1.0f, 1.0f, 1.0f, alpha).uv(u1, v0).endVertex();
        bufferBuilder.vertex(matrix, x0, y0, z).color(1.0f, 1.0f, 1.0f, alpha).uv(u0, v0).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }
}
