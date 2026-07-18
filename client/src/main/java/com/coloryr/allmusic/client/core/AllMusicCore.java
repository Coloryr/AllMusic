package com.coloryr.allmusic.client.core;

import com.coloryr.allmusic.client.core.objs.ConfigObj;
import com.coloryr.allmusic.client.core.player.AllMusicPlayer;
import com.coloryr.allmusic.codec.CommandType;
import com.coloryr.allmusic.codec.HudPosObj;
import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.codec.MusicPacketCodec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.*;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * AllMusic核心
 */
public class AllMusicCore {
    public static final CommandType[] types = CommandType.values();

    private static final Gson gson = new Gson();
    /**
     * 与游戏链接的桥
     */
    public static AllMusicBridge bridge;
    /**
     * 配置文件
     */
    public static ConfigObj config;
    public static CloseableHttpClient client;
    /**
     * 音频解码器与播放器
     */
    private static AllMusicPlayer player;
    /**
     * 界面显示内容
     */
    private static AllMusicHud hud;

    /**
     * 更新音频缓存
     */
    public static void tick() {
        if (player != null) {
            player.tick();
        }
    }

    /**
     * 降低播放音量
     */
    public static void chat() {
        if (player != null) {
            player.setChat();
        }
    }

    /**
     * 是否正在播放音乐
     *
     * @return 是否在播放
     */
    public static boolean isPlay() {
        if (player == null) {
            return false;
        }
        return player.isPlay();
    }

    public static void init(Path file, AllMusicBridge bridge, IntBuffer source) {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0"));
        client = HttpClients.custom().setDefaultHeaders(headers).build();

        File configFile = new File(file.toFile(), "allmusic_client.json");
        if (configFile.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(
                        Files.newInputStream(configFile.toPath()),
                        StandardCharsets.UTF_8);
                BufferedReader bf = new BufferedReader(reader);
                config = new Gson().fromJson(bf, ConfigObj.class);
                bf.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (config == null) {
            config = new ConfigObj();
            config.picSize = 120;
            config.queueSize = 100;
            try {
                String data = new GsonBuilder().setPrettyPrinting()
                        .create()
                        .toJson(config);
                FileOutputStream out = new FileOutputStream(configFile);
                OutputStreamWriter write = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                write.write(data);
                write.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AllMusicCore.bridge = bridge;
        player = new AllMusicPlayer(source);

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig loggerConfig = config.getLoggerConfig("org.apache.hc.client5");
        loggerConfig.setLevel(Level.INFO);

        LoggerConfig coreConfig = config.getLoggerConfig("org.apache.hc.core5");
        coreConfig.setLevel(Level.INFO);

        ctx.updateLoggers(config);
    }

    public static void stop() {
        if (player != null) {
            player.stop();
        }
        if (hud != null) {
            hud.stop();
        }
    }

    /**
     * 初始化核心
     *
     * @param file   配置文件
     * @param bridge 游戏桥
     */
    public static void init(Path file, AllMusicBridge bridge) {
        init(file, bridge, null);
    }

    /**
     * 贴图初始化
     */
    public static void renderInit() {
        hud = new AllMusicHud(config.picSize);
    }

    /**
     * 退出服务器时
     */
    public static void onServerQuit() {
        try {
            stopPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }
        hud.close();
    }

    /**
     * 停止播放
     */
    private static void stopPlaying() {
        player.closePlayer();
        hud.clear();
    }

    /**
     * 重载音频
     */
    public static void reload() {
        if (player != null) {
            player.setReload();
        }
    }

    /**
     * 更新显示内容
     */
    public static void hudUpdate() {
        hud.update();
    }

    /**
     * 读取数据包
     *
     * @param buffer 数据包
     */
    public static void packRead(ByteBuf buffer) {
        packDo(MusicPacketCodec.decode(buffer));
    }

    /**
     * 解析数据包
     *
     * @param pack 数据
     */
    public static void packDo(MusicPack pack) {
        if (pack.type == CommandType.PLAY) {
            bridge.stopPlayMusic();
        }

        switch (pack.type) {
            case LYRIC:
                MusicPack.LyricMusicPack pack1 = (MusicPack.LyricMusicPack) pack;
                hud.setLyric(pack1.lyric, pack1.tlyric, pack1.klyric);
                break;
            case LYRIC_KTV:
                MusicPack.LyricKtvMusicPack pack2 = (MusicPack.LyricKtvMusicPack) pack;
                hud.setKtv(pack2.time, pack2.data);
                break;
            case INFO:
                MusicPack.StringMusicPack pack3 = (MusicPack.StringMusicPack) pack;
                hud.setInfo(pack3.data);
                break;
            case PLAY:
                MusicPack.StringMusicPack pack5 = (MusicPack.StringMusicPack) pack;
                stopPlaying();
                player.setMusic(pack5.data);
                break;
            case IMG:
                MusicPack.StringMusicPack pack6 = (MusicPack.StringMusicPack) pack;
                hud.setImg(pack6.data);
                break;
            case STOP:
                stopPlaying();
                break;
            case CLEAR:
                hud.clear();
                break;
            case POS:
                MusicPack.IntMusicPack pack7 = (MusicPack.IntMusicPack) pack;
                player.setTime(pack7.data);
                break;
            case HUD_DATA:
                MusicPack.StringMusicPack pack8 = (MusicPack.StringMusicPack) pack;
                boolean fail = hud.setPos(gson.fromJson(pack8.data, HudPosObj.class));
                if (!fail) {
                    bridge.kick();
                }
                break;
            case TIME:
                MusicPack.TimeMusicPack pack9 = (MusicPack.TimeMusicPack) pack;
                hud.setTime(pack9.time, pack9.now);
                break;
        }
    }
}
