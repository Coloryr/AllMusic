package com.coloryr.allmusic.server.core.objs.config;

import com.coloryr.allmusic.server.core.AllMusic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件对象
 */
public class ConfigObj {
    /**
     * 最大歌曲数
     */
    public int maxPlayList;
    /**
     * 一个玩家最大可点数量
     */
    public int maxPlayerList;
    /**
     * 最小通过投票数
     */
    public int minVote;
    /**
     * 投票时间
     */
    public int voteTime;
    /**
     * 歌曲延迟
     */
    public int lyricDelay;
    /**
     * 默认添加歌曲方式
     */
    public int defaultAddMusic;
    /**
     * KTV模式歌词加时
     */
    public int ktvLyricDelay;
    /**
     * 管理员ID列表
     */
    public Set<String> adminList;
    /**
     * 玩家点歌后是否直接从空闲歌单切换至玩家歌曲
     */
    public boolean playListSwitch;
    /**
     * 空闲歌单随机播放
     */
    public boolean playListRandom;
    /**
     * 多少首歌不重复
     */
    public int playListEscapeDeep;
    /**
     * 显示歌词
     */
    public boolean sendLyric;
    /**
     * 指令需要权限
     */
    public boolean needPermission;
    /**
     * 顶层模式，用于和BC交换数据
     */
    public boolean topAPI;
    /**
     * 不发送播放信息
     */
    public boolean mutePlayMessage;
    /**
     * 不发送点歌信息
     */
    public boolean muteAddMessage;
    /**
     * 将信息显示在bar处
     */
    public boolean showInBar;
    /**
     * K歌歌词
     */
    public boolean ktvMode;
    /**
     * 配置文件版本号
     */
    public String version;
    /**
     * 经济插件挂钩
     */
    public EconomyObj economy;
    /**
     * 娱乐选项
     */
    public FunConfigObj funConfig;
    /**
     * 限制设置
     */
    public LimitObj limit;
    /**
     * 花费点歌
     */
    public CostObj cost;
    /**
     * 默认音乐API
     */
    public String defaultApi;
    /**
     * 信息更新延迟
     */
    public int sendDelay;
    /**
     * 中途加入延迟
     */
    public int joinDelay;
    /**
     * 歌词正则替换
     */
    public Map<String, String> lyricReplace;
    /**
     * 歌曲时间修正
     */
    public int fixSongTime;

    public static ConfigObj make() {
        ConfigObj config = new ConfigObj();
        config.init();

        return config;
    }

    public boolean check() {
        boolean saveConfig = false;
        if (adminList == null) {
            saveConfig = true;
            adminList = new HashSet<>();
        }
        if (economy == null || economy.check()) {
            saveConfig = true;
            economy = EconomyObj.make();
        }
        if (funConfig == null) {
            saveConfig = true;
            funConfig = FunConfigObj.make();
        }
        if (limit == null) {
            saveConfig = true;
            limit = LimitObj.make();
        }
        if (cost == null) {
            saveConfig = true;
            cost = CostObj.make();
        }
        if (lyricReplace == null) {
            saveConfig = true;
            lyricReplace = new HashMap<>();
        }

        return saveConfig;
    }

    public void init() {
        maxPlayerList = 0;
        fixSongTime = 0;
        maxPlayList = 10;
        minVote = 3;
        voteTime = 30;
        adminList = new HashSet<>();
        adminList.add("color_yr");
        playListSwitch = true;
        playListRandom = true;
        playListEscapeDeep = 40;
        sendLyric = true;
        needPermission = false;
        mutePlayMessage = false;
        muteAddMessage = false;
        showInBar = false;
        defaultAddMusic = 0;
        defaultApi = "netapi";
        topAPI = false;
        limit = LimitObj.make();
        economy = EconomyObj.make();
        funConfig = FunConfigObj.make();
        cost = CostObj.make();
        ktvMode = true;
        ktvLyricDelay = 0;
        lyricDelay = 0;
        sendDelay = 1000;
        joinDelay = 1000;
        lyricReplace = new HashMap<>();
        version = AllMusic.configVersion;
    }
}