package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

public class CoreRenderTarget extends TextFrameBuffer<Component> {
    private final boolean isState;

    public CoreRenderTarget(String name) {
        isState = name.equals("state");
    }

    @Override
    public void putText(String text, int y, int color, boolean shadow) {
        color = (color & 0x00FFFFFF) | 0xFF000000;
        Component component = MiniMessage.parse(text);
        Font font = Minecraft.getInstance().font;
        int width = font.width(component);
        if (width == 0) {
            return;
        }

        int height = font.lineHeight + (shadow ? 1 : 0);
        if (isState) {
            y = 0;
        }
        texts.add(new TextItem<>(width, height, y, component, shadow, color));
    }

    @Override
    public void draw(float alpha, int x, int y, int maxWidth, HudPosType dir) {
        if (texts.isEmpty()) {
            return;
        }

        PoseStack matrices = AllMusicClient.context;
        if (matrices == null) return;
        Font font = Minecraft.getInstance().font;

        for (TextItem<Component> entry : texts) {
            int displayWidth = maxWidth != -1 ? Math.min(entry.width, maxWidth) : entry.width;
            Point2f point = AllMusicHud.getPos(displayWidth, entry.height, x, y, dir);

            int drawX = (int) point.x;
            int drawY = (int) (point.y + entry.y);
            int finalColor = applyAlpha(entry.color, alpha);

            if (maxWidth != -1 && entry.width > maxWidth) {
                int scrollOffset = (int) getOffset(entry, maxWidth);
                enableScissor(drawX, drawY, maxWidth, entry.height);
                drawString(font, matrices, entry, drawX - scrollOffset, drawY, finalColor);
                if (scrollOffset > 0) {
                    drawString(font, matrices, entry, drawX - scrollOffset + entry.width, drawY, finalColor);
                }
                disableScissor();
            } else {
                drawString(font, matrices, entry, drawX, drawY, finalColor);
            }
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
        PoseStack matrices = AllMusicClient.context;
        if (matrices == null) return;
        Font font = Minecraft.getInstance().font;
        TextItem<Component> entry = texts.get(line);
        drawString(font, matrices, entry,
                (int) x, (int) (y + entry.y),
                applyAlpha(entry.color, alpha));
    }

    @Override
    public Point2f getLine(int line) {
        if (line >= texts.size()) {
            return new Point2f(0, 0);
        }
        TextItem<Component> entry = texts.get(line);
        return new Point2f(entry.width, entry.height);
    }

    @Override
    public void drawWithState(float alpha, int x, int y, int maxWidth, float state, HudPosType dir) {
        if (texts.isEmpty()) {
            return;
        }
        PoseStack matrices = AllMusicClient.context;
        if (matrices == null) return;
        Font font = Minecraft.getInstance().font;

        for (TextItem<Component> entry : texts) {
            int displayWidth = maxWidth != -1 ? Math.min(entry.width, maxWidth) : entry.width;
            Point2f point = AllMusicHud.getPos(displayWidth, entry.height, x, y, dir);

            int drawX = (int) point.x;
            int drawY = (int) (point.y + entry.y);
            int finalColor = applyAlpha(entry.color, alpha);

            if (maxWidth != -1 && entry.width > maxWidth) {
                float maxOffset = entry.width - maxWidth;
                float texOffset = maxOffset * state;
                int revealWidth = (int) (maxWidth * state);

                enableScissor(drawX, drawY, revealWidth, entry.height);
                drawString(font, matrices, entry, drawX - (int) texOffset, drawY, finalColor);
                disableScissor();
            } else {
                int revealWidth = (int) (entry.width * state);
                enableScissor(drawX, drawY, revealWidth, entry.height);
                drawString(font, matrices, entry, drawX, drawY, finalColor);
                disableScissor();
            }
        }
    }

    private static void drawString(Font font, PoseStack matrices, TextItem<Component> entry,
                                   int x, int y, int color) {
        if (entry.shadow) {
            font.drawShadow(matrices, entry.component, x, y, color);
        } else {
            font.draw(matrices, entry.component, x, (float) y, color);
        }
    }

    private static void enableScissor(int x, int y, int width, int height) {
        Window window = Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        int sx = (int) (x * scale);
        int sy = (int) (window.getHeight() - (y + height) * scale);
        int sw = (int) (width * scale);
        int sh = (int) (height * scale);
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
