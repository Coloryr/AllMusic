package com.coloryr.allmusic.client.core.render;

import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.codec.HudPosType;

import java.util.ArrayList;
import java.util.List;

public abstract class TextFrameBuffer<T> {
    protected final List<TextItem<T>> texts = new ArrayList<>();
    protected int nowWidth, nowHeight;
    protected long offsetX;
    protected boolean isDraw;
    protected float state;
    /**
     * KTV模式下的强制滚动偏移，>=0 时优先使用
     */
    protected float ktvOffset = -1;

    // 最大公约数（GCD）
    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // 两数最小公倍数（LCM）
    public static long lcm(long a, long b) {
        return a / gcd(a, b) * b;   // 先除后乘防溢出
    }

    public void use() {
        isDraw = true;
        texts.clear();
    }

    public void clear() {
        texts.clear();
    }

    public void unUse() {
        isDraw = false;
    }

    public void resize(int width, int height) {
        nowWidth = width;
        nowHeight = height;
    }

    public abstract void putText(String text, int y, int color, boolean shadow);

    public abstract void drawLine(float x, float y, float alpha, int line);

    public abstract Point2f getLine(int line);

    public abstract void draw(float alpha, int x, int y, int maxWidth, HudPosType dir);

    public abstract void drawWithState(float alpha, int x, int y, int maxWidth, float state, HudPosType dir);

    public void tick() {
        offsetX++;
        if (isDraw) return;
        long temp = 1;
        for (TextItem<T> item : texts) {
            temp = lcm(temp, item.width);
        }
        if (offsetX > temp) {
            offsetX = 0;
        }
    }

    /**
     * 设置KTV模式下的强制滚动偏移
     */
    public void setKtvOffset(float offset) {
        this.ktvOffset = offset;
    }

    /**
     * 清除KTV模式下的强制滚动偏移，恢复默认滚动
     */
    public void clearKtvOffset() {
        this.ktvOffset = -1;
    }

    /**
     * 获取当前应使用的水平滚动偏移
     */
    protected float getOffset(TextItem<T> item, int maxWidth) {
        if (state > 0) {
            float maxOffset = item.width - maxWidth;
            return maxOffset * state;
        }
        if (ktvOffset >= 0) {
            return ktvOffset;
        }
        return offsetX % item.width;
    }

    public void setState(float state) {
        this.state = state;
    }

    public static class TextItem<T> {
        public final T component;
        public final int width;
        public final int height;
        public final boolean shadow;
        public final int color;
        public final float y;

        public TextItem(int width, int height, float y, T component, boolean shadow, int color) {
            this.width = width;
            this.height = height;
            this.component = component;
            this.y = y;
            this.shadow = shadow;
            this.color = color;
        }
    }
}
