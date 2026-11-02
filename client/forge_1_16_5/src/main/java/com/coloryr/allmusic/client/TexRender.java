package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

public class TexRender extends TextureRender {
    private final Tex sourceTexture;

    public TexRender(String texture) {
        super(texture);

        ResourceLocation location = new ResourceLocation(AllMusicClient.MODID, texture);

        sourceTexture = new Tex(location);

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            sourceTexture.load(resourceManager, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawPic(float x, float y, float alpha) {
        RenderSystem.bindTexture(sourceTexture.getId());
        RenderSystem.enableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL30.GL_ALWAYS);

        float w1 = (float) width / 2;
        float h1 = (float) height / 2;

        Matrix4f matrix = Matrix4f.createTranslateMatrix(x + w1, y + h1, 0);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
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

    @Override
    public void drawPic(float x, float y, float width, float alpha) {
        RenderSystem.bindTexture(sourceTexture.getId());
        RenderSystem.enableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL30.GL_ALWAYS);

        float w1 = (float) (this.width / 2) * width;
        float h1 = (float) height / 2;

        Matrix4f matrix = Matrix4f.createTranslateMatrix(x + w1, y + h1, 0);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
        float z = 0;
        float u0 = 0;
        float u1 = width;
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

    @Override
    public void drawPic(float x, float y, float width, float height, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(width, height, x, y, dir);

        RenderSystem.bindTexture(sourceTexture.getId());
        RenderSystem.enableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL30.GL_ALWAYS);

        float w1 = width / 2;
        float h1 = height / 2;

        Matrix4f matrix = Matrix4f.createTranslateMatrix(point.x + w1, point.y + h1, 0);

        float x0 = -w1;
        float x1 = w1;
        float y0 = -h1;
        float y1 = h1;
        float z = 0;
        float u0 = 0;
        // 整张贴图铺满整个四边形，U/V 必须是 0~1，不能写成 width，否则底图会被横向平铺/拉伸
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

    public static class Tex extends SimpleTexture {
        public Tex(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        public void load(ResourceManager resourceManager, TextureRender source) throws IOException {
            TextureImage textureImage = this.getTextureImage(resourceManager);
            textureImage.throwIfError();
            TextureMetadataSection textureMetadataSection = textureImage.getTextureMetadata();
            boolean bl;
            boolean bl2;
            if (textureMetadataSection != null) {
                bl = textureMetadataSection.isBlur();
                bl2 = textureMetadataSection.isClamp();
            } else {
                bl = false;
                bl2 = false;
            }

            NativeImage nativeImage = textureImage.getImage();
            source.width = nativeImage.getWidth();
            source.height = nativeImage.getHeight();
            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(() -> this.doLoad(nativeImage, bl, bl2));
            } else {
                this.doLoad(nativeImage, bl, bl2);
            }
        }

        private void doLoad(NativeImage nativeImage, boolean bl, boolean bl2) {
            TextureUtil.prepareImage(this.getId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
            nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), bl, bl2, false, true);
        }
    }
}
