package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.joml.Matrix3x2fStack;

import java.io.ByteArrayInputStream;

public class PicRender extends PictureFrameBuffer {
    private final DynamicTexture sourceTexture;
    private final DynamicTexture rotateTexture;

    public PicRender(int size) {
        sourceTexture = new DynamicTexture("allmusic source", size, size, false);
        rotateTexture = new DynamicTexture("allmusic rotate", size, size, false);
    }

    @Override
    public void update(byte[] source, byte[] rotate) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(source);
            var image1 = NativeImage.read(stream);
            sourceTexture.setPixels(image1);
            sourceTexture.upload();

            stream = new ByteArrayInputStream(rotate);
            var image2 = NativeImage.read(stream);
            rotateTexture.setPixels(image2);
            rotateTexture.upload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(boolean rotate, int size, float x, float y, int ang, HudPosType dir, float alpha) {
        Point2f point = AllMusicHud.getPos(size, size, x, y, dir);

        Matrix3x2fStack matrix = new Matrix3x2fStack(2);

        float a = (float) size / 2;

        if (rotate) {
            matrix.translation(point.x + a, point.y + a);
            matrix.pushMatrix().rotate((float) Math.toRadians(ang));
        } else {
            matrix.translation(point.x + a, point.y + a);
        }

        float x0 = -a;
        float x1 = a;
        float y0 = -a;
        float y1 = a;
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        int color = 0xFFFFFF00 + (int) (255 * alpha);

        AllMusicClient.context.submitGuiElementRenderState(new FloatRenderState(RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(rotate ? rotateTexture.getTextureView() : sourceTexture.getTextureView(), rotate ? rotateTexture.getSampler() : sourceTexture.getSampler()),
                matrix, x0, y0, x1, y1, u0, u1, v0, v1, color, AllMusicClient.context.peekScissorStack()));
    }
}
