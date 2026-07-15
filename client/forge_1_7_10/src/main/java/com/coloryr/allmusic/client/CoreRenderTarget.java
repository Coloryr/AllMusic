package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CoreRenderTarget extends TextFrameBuffer<String> {
    private final boolean isState;

    public CoreRenderTarget(String name) {
        isState = name.equals("state");
    }

    @Override
    public void putText(String text, int y, int color, boolean shadow) {
        color = (color & 0x00FFFFFF) | 0xFF000000;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = font.getStringWidth(text);
        if (width == 0) {
            return;
        }

        int height = font.FONT_HEIGHT + (shadow ? 1 : 0);
        if (isState) {
            y = 0;
        }
        texts.add(new TextItem<>(width, height, y, text, shadow, color));
    }

    @Override
    public void draw(float alpha, int x, int y, int maxWidth, HudPosType dir) {
        if (texts.isEmpty()) {
            return;
        }

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        for (TextItem<String> entry : texts) {
            int displayWidth = maxWidth != -1 ? Math.min(entry.width, maxWidth) : entry.width;
            Point2f point = AllMusicHud.getPos(displayWidth, entry.height, x, y, dir);

            int drawX = (int) point.x;
            int drawY = (int) (point.y + entry.y);
            int finalColor = applyAlpha(entry.color, alpha);

            GL11.glPushMatrix();
            GL11.glTranslatef(drawX, drawY, 0);

            if (maxWidth != -1 && entry.width > maxWidth) {
                int scrollOffset = (int) (offsetX % entry.width);
                enableScissor(drawX, drawY, maxWidth, entry.height);
                drawString(font, entry, -scrollOffset, 0, finalColor);
                if (scrollOffset > 0) {
                    drawString(font, entry, -scrollOffset + entry.width, 0, finalColor);
                }
                disableScissor();
            } else {
                drawString(font, entry, 0, 0, finalColor);
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    public void drawLine(float x, float y, float alpha, int line) {
        if (texts.isEmpty()) {
            return;
        }
        if (line >= texts.size()) {
            return;
        }
        TextItem<String> entry = texts.get(line);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) (y + entry.y), 0);
        drawString(font, entry, 0, 0, applyAlpha(entry.color, alpha));
        GL11.glPopMatrix();
    }

    @Override
    public void drawWithState(float alpha, int x, int y, int maxWidth, float state, HudPosType dir) {
        if (texts.isEmpty()) {
            return;
        }
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        for (TextItem<String> entry : texts) {
            int displayWidth = maxWidth != -1 ? Math.min(entry.width, maxWidth) : entry.width;
            Point2f point = AllMusicHud.getPos(displayWidth, entry.height, x, y, dir);

            int drawX = (int) point.x;
            int drawY = (int) (point.y + entry.y);
            int finalColor = applyAlpha(entry.color, alpha);

            GL11.glPushMatrix();
            GL11.glTranslatef(drawX, drawY, 0);

            if (maxWidth != -1 && entry.width > maxWidth) {
                float maxOffset = entry.width - maxWidth;
                float texOffset = maxOffset * state;
                int revealWidth = (int) (maxWidth * state);

                enableScissor(drawX, drawY, revealWidth, entry.height);
                drawString(font, entry, -(int) texOffset, 0, finalColor);
                disableScissor();
            } else {
                int revealWidth = (int) (entry.width * state);
                enableScissor(drawX, drawY, revealWidth, entry.height);
                drawString(font, entry, 0, 0, finalColor);
                disableScissor();
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    public Point2f getLine(int line) {
        if (line >= texts.size()) {
            return new Point2f(0, 0);
        }
        TextItem<String> entry = texts.get(line);
        return new Point2f(entry.width, entry.height);
    }

    private static void drawString(FontRenderer font, TextItem<String> entry, int x, int y, int color) {
        if (entry.shadow) {
            font.drawStringWithShadow(entry.component, x, y, color);
        } else {
            font.drawString(entry.component, x, y, color);
        }
    }

    private static void enableScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = mc.gameSettings.guiScale;
        if (scale == 0) {
            int k = Math.min(mc.displayWidth, mc.displayHeight);
            scale = Math.max(1, k / 320);
        }
        int sx = x * scale;
        int sy = mc.displayHeight - (y + height) * scale;
        int sw = width * scale;
        int sh = height * scale;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sx, sy, sw, sh);
    }

    private static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private static int applyAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        return (color & 0x00FFFFFF) | (a << 24);
    }
}
