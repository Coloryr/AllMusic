package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.codec.HudPosType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TexRender extends TextureRender {
    private final Tex sourceTexture;

    public TexRender(String texture) {
        super(texture);

        ResourceLocation location = new ResourceLocation(AllMusicClient.MODID, texture);

        sourceTexture = new Tex(location);

        try {
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            sourceTexture.loadTexture(resourceManager, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawPic(float x, float y, float alpha) {
        GlStateManager.bindTexture(sourceTexture.getGlTextureId());
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
//        GlStateManager.depthFunc(GL30.GL_ALWAYS);

        float w1 = (float) width / 2;
        float h1 = (float) height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + w1, y + h1, 0.0f);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
        float z = 0;
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x0, y1, z).tex(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y1, z).tex(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y0, z).tex(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x0, y0, z).tex(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GL11.glPopMatrix();
    }

    @Override
    public void drawPic(float x, float y, float width, float alpha) {
        GlStateManager.bindTexture(sourceTexture.getGlTextureId());
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
//GlStateManager.depthFunc(GL30.GL_ALWAYS);

        float w1 = (float) (this.width / 2) * width;
        float h1 = (float) height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + w1, y + h1, 0.0f);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
        float z = 0;
        float u0 = 0;
        float u1 = width;
        float v0 = 0;
        float v1 = 1;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x0, y1, z).tex(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y1, z).tex(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y0, z).tex(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x0, y0, z).tex(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GL11.glPopMatrix();
    }

    @Override
    public void drawPic(float x, float y, float width, float height, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(width, height, x, y, dir);

        GlStateManager.bindTexture(sourceTexture.getGlTextureId());
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
//GlStateManager.depthFunc(GL30.GL_ALWAYS);

        float w1 = width / 2;
        float h1 = height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(point.x + w1, point.y + h1, 0.0f);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
        float z = 0;
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x0, y1, z).tex(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y1, z).tex(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x1, y0, z).tex(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.pos(x0, y0, z).tex(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GL11.glPopMatrix();
    }

    public static class Tex extends SimpleTexture {
        public Tex(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        public void loadTexture(IResourceManager resourceManager, TexRender render) throws IOException {
            this.deleteGlTexture();
            IResource iresource = null;

            try {
                iresource = resourceManager.getResource(this.textureLocation);
                BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
                render.width = bufferedimage.getWidth();
                render.height = bufferedimage.getHeight();
                boolean flag = false;
                boolean flag1 = false;

                if (iresource.hasMetadata()) {
                    try {
                        TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                        if (texturemetadatasection != null) {
                            flag = texturemetadatasection.getTextureBlur();
                            flag1 = texturemetadatasection.getTextureClamp();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
            } finally {
                IOUtils.closeQuietly(iresource);
            }
        }
    }
}
