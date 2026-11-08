package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class PicRender extends PictureFrameBuffer {
    private final DynamicTexture sourceTexture;
    private final DynamicTexture rotateTexture;

    public PicRender(int size) {
        // 分配材质会改纹理绑定，初始化也按状态机处理
        try (GlState ignored = GlState.push()) {
            sourceTexture = new DynamicTexture(size, size);
            rotateTexture = new DynamicTexture(size, size);
        }
    }

    @Override
    public void update(byte[] source, byte[] rotate) {
        // 上传材质会绑定纹理、改像素存储状态，结束还原
        try (GlState ignored = GlState.push()) {
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
    }

    @Override
    public void dispose() {
        // 删除的纹理可能正好是当前绑定的纹理，删完不能把已删除的名字绑回去
        int previous = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int source = sourceTexture.getGlTextureId();
        int rotate = rotateTexture.getGlTextureId();

        sourceTexture.deleteGlTexture();
        rotateTexture.deleteGlTexture();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previous != source && previous != rotate ? previous : 0);
    }

    @Override
    public void draw(boolean rotate, int size, float x, float y, int ang, HudPosType dir, float alpha) {
        try (GlState ignored = GlState.push()) {
            Point2f point = AllMusicHud.getPos(size, size, x, y, dir);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, rotate ? rotateTexture.getGlTextureId() : sourceTexture.getGlTextureId());
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

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

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);
            tessellator.addVertexWithUV(x0, y1, z, u0, v1);
            tessellator.addVertexWithUV(x1, y1, z, u1, v1);
            tessellator.addVertexWithUV(x1, y0, z, u1, v0);
            tessellator.addVertexWithUV(x0, y0, z, u0, v0);
            tessellator.draw();

            GL11.glPopMatrix();
        }
    }
}
