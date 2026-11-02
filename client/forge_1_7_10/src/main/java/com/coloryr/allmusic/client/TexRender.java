package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.codec.HudPosType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TexRender extends TextureRender {
    private final Tex sourceTexture;

    public TexRender(String texture) {
        super(texture);

        ResourceLocation location = new ResourceLocation("allmusic_client", texture);

        sourceTexture = new Tex(location);

        try {
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            sourceTexture.loadTexture(resourceManager, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始绘制：绑定材质、设置混合模式
     */
    private void beginDraw() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture.getGlTextureId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(false);
    }

    /**
     * 结束绘制：还原混合模式
     */
    private void endDraw() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

    /**
     * 绘制一个四边形
     *
     * @param x0    X坐标
     * @param y0    Y坐标
     * @param x1    X坐标
     * @param y1    Y坐标
     * @param u0    材质U坐标
     * @param v0    材质V坐标
     * @param u1    材质U坐标
     * @param v1    材质V坐标
     * @param alpha 透明度
     */
    private static void drawQuad(float x0, float y0, float x1, float y1,
                                 float u0, float v0, float u1, float v1, float alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);
        tessellator.addVertexWithUV(x0, y1, 0, u0, v1);
        tessellator.addVertexWithUV(x1, y1, 0, u1, v1);
        tessellator.addVertexWithUV(x1, y0, 0, u1, v0);
        tessellator.addVertexWithUV(x0, y0, 0, u0, v0);
        tessellator.draw();
    }

    @Override
    public void drawPic(float x, float y, float alpha) {
        beginDraw();

        float w1 = (float) width / 2;
        float h1 = (float) height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + w1, y + h1, 0.0f);

        drawQuad(-w1, -h1, w1, h1, 0, 0, 1, 1, alpha);

        endDraw();

        GL11.glPopMatrix();
    }

    @Override
    public void drawPic(float x, float y, float width, float alpha) {
        beginDraw();

        float w1 = (float) (this.width / 2) * width;
        float h1 = (float) height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + w1, y + h1, 0.0f);

        // 这里只画材质的 width 比例（进度条）
        drawQuad(-w1, -h1, w1, h1, 0, 0, width, 1, alpha);

        endDraw();

        GL11.glPopMatrix();
    }

    @Override
    public void drawPic(float x, float y, float width, float height, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(width, height, x, y, dir);

        beginDraw();

        float w1 = width / 2;
        float h1 = height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(point.x + w1, point.y + h1, 0.0f);

        // 整张贴图铺满整个四边形，U/V 都必须是 0~1，不能写成 width
        drawQuad(-w1, -h1, w1, h1, 0, 0, 1, 1, alpha);

        endDraw();

        GL11.glPopMatrix();
    }

    public static class Tex extends SimpleTexture {
        public Tex(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        public void loadTexture(IResourceManager resourceManager, TexRender render) throws IOException {
            this.deleteGlTexture();
            IResource iresource = null;
            InputStream inputstream = null;

            try {
                iresource = resourceManager.getResource(this.textureLocation);
                inputstream = iresource.getInputStream();
                BufferedImage bufferedimage = ImageIO.read(inputstream);
                render.width = bufferedimage.getWidth();
                render.height = bufferedimage.getHeight();
                boolean flag = false;
                boolean flag1 = false;

                if (iresource.hasMetadata()) {
                    try {
                        TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");

                        if (texturemetadatasection != null) {
                            flag = texturemetadatasection.getTextureBlur();
                            flag1 = texturemetadatasection.getTextureClamp();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);

                // HUD 贴图基本都不是 2 的幂（如 bg1.png 238x238），GL_REPEAT 采样会被拉伸/平铺，
                // 这里统一改成 CLAMP_TO_EDGE，避免贴图边缘被重复
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getGlTextureId());
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            } finally {
                IOUtils.closeQuietly(inputstream);
            }
        }
    }
}
