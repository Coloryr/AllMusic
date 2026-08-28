package com.coloryr.allmusic.server.core.objs.config;

public class LimitObj {
    /**
     * 开启信息长度限制
     */
    public boolean messageLimit;
    /**
     * 消息限制长度
     */
    public int messageLimitSize;
    /**
     * 开启信息长度限制
     */
    public boolean listLimit;
    /**
     * 消息限制长度
     */
    public int listLimitSize;
    /**
     * 开启信息长度限制
     */
    public boolean infoLimit;
    /**
     * 消息限制长度
     */
    public int infoLimitSize;
    /**
     * 开启歌曲长度限制
     */
    public boolean musicTimeLimit;
    /**
     * 最长音乐长度
     */
    public int maxMusicTime;
    /**
     * 长度替换符号
     */
    public String limitText;
    /**
     * 最大歌曲数
     */
    public int maxPlayList;
    /**
     * 一个玩家最大可点数量
     */
    public int maxPlayerList;

    public static LimitObj make() {
        LimitObj obj = new LimitObj();
        obj.musicTimeLimit = false;
        obj.maxMusicTime = 600;
        obj.infoLimit = false;
        obj.infoLimitSize = 20;
        obj.listLimit = false;
        obj.listLimitSize = 10;
        obj.messageLimit = false;
        obj.messageLimitSize = 20;
        obj.maxPlayList = 10;
        obj.maxPlayerList = 10;
        obj.limitText = "...";
        return obj;
    }
}
