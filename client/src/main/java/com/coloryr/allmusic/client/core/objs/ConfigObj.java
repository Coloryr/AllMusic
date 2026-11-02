package com.coloryr.allmusic.client.core.objs;

import java.util.List;

public class ConfigObj {
    /**
     * 图片大小
     */
    public int picSize;
    /**
     * 音频队列大小
     */
    public int queueSize;
    /**
     * 需要静音（设成 INFO）的日志
     */
    public List<String> logQuiet;
}
