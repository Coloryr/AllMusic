package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class PicRender extends PictureFrameBuffer {
    private final DynamicTexture sourceTexture;
    private final DynamicTexture rotateTexture;

    public PicRender(int size) {
        sourceTexture = new DynamicTexture(size, size);
        rotateTexture = new DynamicTexture(size, size);
    }

    @Override
    public void update(byte[] source, byte[] rotate) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(source);
            BufferedImage image = ImageIO.read(stream);
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), sourceTexture.getTextureData(), 0, image.getWidth());
            sourceTexture.updateDynamicTexture();

            stream = new ByteArrayInputStream(rotate);
            image = ImageIO.read(stream);
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), rotateTexture.getTextureData(), 0, image.getWidth());
            rotateTexture.updateDynamicTexture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(boolean rotate, int size, float x, float y, int ang, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(size, size, x, y, dir);

        GlStateManager.bindTexture(rotate ? rotateTexture.getGlTextureId() : sourceTexture.getGlTextureId());
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
//        GlStateManager.depthFunc(GL11.GL_ALWAYS);

        int a = size / 2;
        GL11.glPushMatrix();
        GL11.glTranslatef(point.x + a, point.y + a, 0.0f);

        if (ang > 0) {
            GL11.glRotatef(ang, 0, 0, 1f);
        }

        int x0 = -a;
        int x1 = a;
        int y0 = -a;
        int y1 = a;
        int z = 0;
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
}
