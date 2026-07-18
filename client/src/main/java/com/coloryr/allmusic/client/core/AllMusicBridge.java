package com.coloryr.allmusic.client.core;

import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;

import java.io.InputStream;

/**
 * AllMusic 核心桥
 */
public interface AllMusicBridge {
    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    int getScreenWidth();

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    int getScreenHeight();

    /**
     * 获取文字宽度
     *
     * @param item 文字内容
     * @return 宽度
     */
    int getTextWidth(String item);

    /**
     * 获取字体高度
     *
     * @return 字体高度
     */
    int getFontHeight();

    /**
     * 显示消息
     *
     * @param data 显示内容
     */
    void sendMessage(String data);

    /**
     * 获取当前音量
     *
     * @return 音量
     */
    float getVolume();

    /**
     * 停止播放其他音频
     */
    void stopPlayMusic();

    /**
     * 创建渲染层
     *
     * @return 渲染层
     */
    TextFrameBuffer makeTextRender(String name);

    /**
     * 创建渲染层
     *
     * @return 渲染层
     */
    TextureRender makeTextureRender(String file);

    /**
     * 创建图片渲染层
     *
     * @param size 图片大小
     * @return 图片渲染
     */
    PictureFrameBuffer makePictureRender(int size);

    /**
     * 读取文件
     *
     * @param file 文件位置
     * @return 内容
     */
    String readText(String file);

    /**
     * 读取文件
     *
     * @param file 文件位置
     * @return 内容
     */
    InputStream readFile(String file);

    /**
     * 踢出服务器
     */
    void kick();
}
