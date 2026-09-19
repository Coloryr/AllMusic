package com.coloryr.allmusic.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;

public record FloatRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0,
                               float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color,
                               ScreenRectangle scissorArea,
                               ScreenRectangle bounds) implements GuiElementRenderState {
    public FloatRenderState(RenderPipeline renderPipeline, TextureSetup textureSetup, Matrix3x2f matrix3x2f,
                            float i, float j, float k, float l, float f, float g, float h, float m, int n, ScreenRectangle screenRectangle) {
        this(renderPipeline, textureSetup, matrix3x2f, i, j, k, l, f, g, h, m, n, screenRectangle, getBounds(i, j, k, l, matrix3x2f, screenRectangle));
    }

    private static ScreenRectangle getBounds(float i, float j, float k, float l, Matrix3x2f matrix3x2f, ScreenRectangle screenRectangle) {
        ScreenRectangle screenRectangle2 = (new ScreenRectangle((int) Math.ceil(i + 1), (int) Math.ceil(j + 1), (int) Math.ceil(k - i + 1), (int) Math.ceil(l - j + 1))).transformMaxBounds(matrix3x2f);
        return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
    }

    public void buildVertices(VertexConsumer vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(pose(), x0(), y0()).setUv(u0(), v0()).setColor(color());
        vertexConsumer.addVertexWith2DPose(pose(), x0(), y1()).setUv(u0(), v1()).setColor(color());
        vertexConsumer.addVertexWith2DPose(pose(), x1(), y1()).setUv(u1(), v1()).setColor(color());
        vertexConsumer.addVertexWith2DPose(pose(), x1(), y0()).setUv(u1(), v0()).setColor(color());
    }
}
