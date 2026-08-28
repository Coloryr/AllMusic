package com.coloryr.allmusic.server.core;

import com.coloryr.allmusic.server.core.music.MusicHttpClient;
import com.coloryr.allmusic.server.core.music.MusicSearch;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.music.PlayRuntime;
import com.coloryr.allmusic.server.core.objs.CookieObj;
import com.coloryr.allmusic.server.core.objs.config.ConfigObj;
import com.coloryr.allmusic.server.core.objs.message.MessageObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.saves.BanSave;
import com.coloryr.allmusic.server.core.saves.HudSave;
import com.coloryr.allmusic.server.core.saves.MusicListSave;
import com.coloryr.allmusic.server.core.saves.SaveTask;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.core.side.IAllMusicLogger;
import com.coloryr.allmusic.server.core.utils.MusicApiLoader;
import com.coloryr.allmusic.server.core.utils.StringReplacer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class AllMusic {
    public static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static final Random random = new Random();

    public static final Map<String, IMusicApi> MUSIC_APIS = new HashMap<>();

    public static final String SERVER_DIR = "allmusic_server/";

    /**
     * 客户端插件信道名
     */
    public static final String channel = "allmusic:channel";
    /**
     * BC插件信道名
     */
    public static final String channelBC = "allmusic:channelbc";
    /**
     * 插件版本号
     */
    public static final String version = "4.0.0";
    /**
     * 配置文件版本号
     */
    public static final String configVersion = "402";
    /**
     * 语言文件配置版本号
     */
    public static final String messageVersion = "401";
    /**
     * 日志
     */
    public static IAllMusicLogger log;
    /**
     * 服务器端操作
     */
    public static BaseSide side;
    /**
     * 是否在运行
     */
    public static boolean isRun;
    /**
     * Cookie对象
     */
    public static List<CookieObj> cookie;
    /**
     * 经济插件对象
     */
    public static IEconomy economy;
    /**
     * 配置对象
     */
    private static ConfigObj config;
    /**
     * 语言对象
     */
    private static MessageObj message;
    /**
     * 配置文件
     */
    private static File configFile;
    /**
     * Cookie文件
     */
    private static File cookieFile;
    /**
     * 语言文件
     */
    private static File messageFile;

    private static File apis;
    /**
     * 正则替换器
     */
    private static StringReplacer replacer;

    public static StringReplacer getReplacer() {
        return replacer;
    }

    /**
     * 检查配置文件完整性
     */
    public static void configCheck() {
        if (config == null || config.check()) {
            config = ConfigObj.make();
            log.data("<light_purple>[AllMusic]<red>配置文件config.json错误，已覆盖");
            saveConfig();
        }
    }

    /**
     * 检查语言文件完整性
     */
    private static void messageCheck() {
        if (message == null || message.check()) {
            message = MessageObj.make();
            log.data("<light_purple>[AllMusic]<red>配置文件message.json错误，已覆盖");
            saveMessage();
        }
    }

    /**
     * 检查是否需要放歌
     *
     * @param name      用户名
     * @param server    服务器名
     * @param checkPlay 是否检查正在播放的列表
     * @return 是否跳过放歌
     */
    public static boolean isSkip(String name, String server, boolean checkPlay) {
        try {
            if (server != null && BanSave.haveBanServer(server))
                return true;
            if (BanSave.checkMutePlayer(name))
                return true;
            if (PlayMusic.nowPlayMusic != null && PlayMusic.nowPlayMusic.isList() && BanSave.checkMuteListPlayer(name))
                return true;
            if (!checkPlay)
                return false;
            return PlayMusic.containNowPlay(name);
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    /**
     * 检查是否需要放歌
     *
     * @param name      用户名
     * @param server    服务器名
     * @param checkPlay 是否检查正在播放的列表
     * @param islist    是否为空闲歌单的歌
     * @return 是否跳过放歌
     */
    public static boolean isSkip(String name, String server, boolean checkPlay, boolean islist) {
        try {
            name = name.toLowerCase();
            if (server != null && BanSave.haveBanServer(server))
                return true;
            if (BanSave.checkMutePlayer(name))
                return true;
            if (islist && BanSave.checkMuteListPlayer(name))
                return true;
            if (!checkPlay)
                return false;
            return PlayMusic.containNowPlay(name);
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    /**
     * 获取配置文件
     *
     * @return 配置对象
     */
    public static ConfigObj getConfig() {
        if (config == null) {
            log.data("<light_purple>[AllMusic]<red>配置文件config.json错误，已使用默认配置文件");
            config = ConfigObj.make();
        }
        return config;
    }

    /**
     * 获取语言文件
     *
     * @return 语言对象
     */
    public static MessageObj getMessage() {
        if (message == null) {
            log.data("<light_purple>[AllMusic]<red>配置文件message.json错误，已使用默认配置文件");
            message = MessageObj.make();
        }
        return message;
    }

    /**
     * 保存配置文件
     */
    public static void saveConfig() {
        try {
            String data = gson.toJson(config);
            FileOutputStream out = new FileOutputStream(configFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
            out.close();
        } catch (Exception e) {
            log.data("<light_purple>[AllMusic]<red>配置文件config.json保存错误");
            e.printStackTrace();
        }
    }

    public static void saveMessage() {
        try {
            String data = gson.toJson(message);
            FileOutputStream out = new FileOutputStream(messageFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
            out.close();
        } catch (Exception e) {
            log.data("<light_purple>[AllMusic]<red>配置文件message.json保存错误");
            e.printStackTrace();
        }
    }

    /**
     * 保存Cookie
     */
    public static void saveCookie() {
        try {
            String data = gson.toJson(cookie);
            FileOutputStream out = new FileOutputStream(cookieFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
        } catch (Exception e) {
            log.data("<light_purple>[AllMusic]<red>配置文件cookie.json保存错误");
            e.printStackTrace();
        }
    }

    /**
     * 启动插件
     */
    public static void start() {
        isRun = true;

        MusicHttpClient.init();

        PlayMusic.start();
        PlayRuntime.start();
        MusicSearch.start();
        SaveTask.start();

        List<IMusicApi> list = MusicApiLoader.loadFromDirectory(apis);
        for (IMusicApi item : list) {
            AllMusic.log.data("<light_purple>[AllMusic]<yellow>注册音乐API：" + item.getId());
            MUSIC_APIS.put(item.getId(), item);
        }

        if (MUSIC_APIS.isEmpty()) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>没有注册音乐");
        }

        log.data("<light_purple>[AllMusic]<yellow>已启动-" + version);
    }

    /**
     * 停止插件
     */
    public static void stop() {
        isRun = false;
        PlayRuntime.stop();
        SaveTask.stop();
        side.sendStop();
        log.data("<light_purple>[AllMusic]<dark_green><yellow>已停止，感谢使用");
    }

    /**
     * 加载配置文件
     */
    private static void loadConfig() {
        try {
            InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8);
            BufferedReader bf = new BufferedReader(reader);
            config = gson.fromJson(bf, ConfigObj.class);
            bf.close();
            reader.close();
            configCheck();

            reader = new InputStreamReader(Files.newInputStream(messageFile.toPath()), StandardCharsets.UTF_8);
            bf = new BufferedReader(reader);
            message = gson.fromJson(bf, MessageObj.class);
            bf.close();
            reader.close();
            messageCheck();

            log.data("<light_purple>[AllMusic]<yellow>当前语言配置文件版本为：" + messageVersion
                    + "，你的语言文件版本为：" + message.version);

            if (!message.version.equalsIgnoreCase(messageVersion)) {
                log.data("<light_purple>[AllMusic]<red>语言文件版本号错误，运行可能会发生问题，请删除后重载");
            }

            reader = new InputStreamReader(Files.newInputStream(cookieFile.toPath()), StandardCharsets.UTF_8);
            bf = new BufferedReader(reader);
            Type listType = new TypeToken<ArrayList<CookieObj>>() {
            }.getType();
            cookie = gson.fromJson(bf, listType);
            bf.close();
            reader.close();
            if (cookie == null) {
                cookie = new ArrayList<>();
                saveCookie();
            }

            log.data("<light_purple>[AllMusic]<yellow>当前插件配置文件版本为：" + configVersion
                    + "，你的配置文件版本为：" + config.version);

            if (!AllMusic.configVersion.equalsIgnoreCase(config.version)) {
                log.data("<light_purple>[AllMusic]<red>请及时更新配置文件");
            }

            replacer = new StringReplacer();
            if (!config.lyricReplace.isEmpty()) {
                for (Map.Entry<String, String> item : config.lyricReplace.entrySet()) {
                    replacer.put(item.getKey(), item.getValue());
                }
            }

            BanSave.loadBan();
            HudSave.loadHud();
            MusicListSave.loadMusic();
        } catch (Exception e) {
            log.data("<light_purple>[AllMusic]<red>读取配置文件错误");
            e.printStackTrace();
        }
    }

    /**
     * 加入时播放
     *
     * @param player 用户名
     */
    public static void joinPlay(String player) {
        if (AllMusic.side == null) {
            return;
        }
        AllMusic.side.runTask(() -> joinPlayNow(player), AllMusic.config.joinDelay);
    }

    public static void joinPlayNow(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase();
            Object player2 = AllMusic.side.getPlayer(player1);
            String server = AllMusic.side.getPlayerServer(player2);
            if (server != null && BanSave.haveBanServer(server)) {
                return;
            }
            if (BanSave.checkMutePlayer(player1)) {
                return;
            }
            if (BanSave.checkMuteListPlayer(player1)) {
                return;
            }

            AllMusic.side.runTask(() -> {
                SongInfoObj music = PlayMusic.nowPlayMusic;
                if (music != null && PlayMusic.url != null) {
                    AllMusic.side.sendHudPos(player1);
                    AllMusic.side.sendMusic(player1, PlayMusic.url);
                    AllMusic.side.sendPic(player1, music.getPicUrl());
                    AllMusic.side.runTask(() -> AllMusic.side.sendPos(player1, (int) PlayMusic.musicNowTime), 20);
                }
            });
        });
    }

    /**
     * 读取配置文件
     *
     * @param file 配置文件文件夹
     */
    public static void init(File file) {
        log.data("<light_purple>[AllMusic]<yellow>正在启动，感谢使用，本插件交流群：571239090");
        try {
            file.mkdir();

            configFile = new File(file, "config.json");
            messageFile = new File(file, "message.json");
            cookieFile = new File(file, "cookie.json");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            if (!messageFile.exists()) {
                messageFile.createNewFile();
            }
            if (!cookieFile.exists()) {
                cookieFile.createNewFile();
            }

            BanSave.init(file);
            HudSave.init(file);
            MusicListSave.init(file);

            loadConfig();

            apis = new File(file, "api");
            apis.mkdirs();

            isRun = true;
        } catch (IOException e) {
            isRun = false;
            log.data("<light_purple>[AllMusic]<red>启动失败");
            e.printStackTrace();
        }
    }
}
