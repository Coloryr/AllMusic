package com.coloryr.allmusic.client.core;

import com.coloryr.allmusic.client.core.objs.ConfigObj;
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
import java.lang.reflect.Method;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * AllMusic核心
 */
public class AllMusicCore {
    public static final CommandType[] types = CommandType.values();
    /**
     * 图片大小的最小值
     */
    public static final int MIN_PIC_SIZE = 16;
    /**
     * 图片大小的最大值
     */
    public static final int MAX_PIC_SIZE = 1024;
    /**
     * 默认静音的日志
     * <p>
     * httpclient 的调试输出会刷 debug.log；
     * 旅行者地图则是按 debug 等级在屏幕上画调试标签（TileDrawStep 里 isDebugEnabled() 就画），
     * 所以一起压到 INFO，避免 debug.log 和屏幕都被刷
     */
    public static final List<String> DEFAULT_LOG_QUIET = Arrays.asList(
            "org.apache.hc.client5",
            "org.apache.hc.core5",
            "journeymap");

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
     * 配置文件
     */
    private static File configFile;
    /**
     * 音频解码器与播放器
     */
    private static AllMusicPlayer player;
    /**
     * 界面显示内容
     */
    private static AllMusicHud hud;

    public static final ScheduledExecutorService service = Executors.newScheduledThreadPool(4);

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

        configFile = new File(file.toFile(), "allmusic_client.json");
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
            config.picSize = 240;
            config.queueSize = 100;
        }
        if (config.logQuiet == null || config.logQuiet.isEmpty()) {
            config.logQuiet = new ArrayList<>(DEFAULT_LOG_QUIET);
        }
        saveConfig();

        AllMusicCore.bridge = bridge;
        player = new AllMusicPlayer(source);

//        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//        Configuration logConfig = ctx.getConfiguration();
//
//        setLoggerLevel(logConfig, "org.apache.hc.client5", Level.INFO);
//        setLoggerLevel(logConfig, "org.apache.hc.core5", Level.INFO);
//
//        ctx.updateLoggers(logConfig);
    }

    /**
     * 只调整指定 logger 的等级，不动根 logger
     * <p>
     * {@link Configuration#getLoggerConfig(String)} 找不到同名 logger 时会返回父级或根 logger，
     * 直接 setLevel 会顺带把根 logger 的等级改掉，等于把玩家自己的日志配置整个覆盖了。
     * 这里只修改同名 logger；没有就新建一个（additive，日志依旧会输出到根 logger 的 appender）
     *
     * @param logConfig 日志配置
     * @param name      logger 名字
     * @param level     日志等级
     */
    private static void setLoggerLevel(Configuration logConfig, String name, Level level) {
        LoggerConfig loggerConfig = logConfig.getLoggerConfig(name);

        if (name.equals(loggerConfig.getName())) {
            loggerConfig.setLevel(level);
            return;
        }

        // 没有同名 logger：新建一个挂进配置。
        // Configuration#addLogger 只存在于实现类（1.7.10 自带的 2.0-beta9 里连接口都没有），
        // 所以按运行时类型反射调用，避免换版本时 NoSuchMethodError
        try {
            Method addLogger = logConfig.getClass()
                    .getMethod("addLogger", String.class, LoggerConfig.class);
            addLogger.invoke(logConfig, name, new LoggerConfig(name, level, true));
        } catch (Exception ignored) {
            // 实在加不进去就保持原样，绝不去改根 logger
        }
    }

    public static void stop() {
        service.close();

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
     * 获取图片大小
     *
     * @return 图片大小
     */
    public static int getPicSize() {
        return config == null ? 0 : config.picSize;
    }

    /**
     * 设置图片大小
     * <p>
     * 立即重建图片渲染缓冲；下一次收到图片（比如下一首歌的封面）时就会用新的大小生成。
     * 当前正在显示的封面会重新拉取一次，没有封面时等下一首歌的图片包。
     * 同时写回 allmusic_client.json，重启后依然生效
     *
     * @param size 图片大小
     * @return 是否设置成功
     */
    public static boolean setPicSize(int size) {
        if (config == null) {
            return false;
        }
        if (size < MIN_PIC_SIZE || size > MAX_PIC_SIZE) {
            return false;
        }

        config.picSize = size;
        saveConfig();
        if (hud != null) {
            hud.setPicSize(size);
        }
        return true;
    }

    /**
     * 保存配置文件
     */
    public static void saveConfig() {
        if (configFile == null || config == null) {
            return;
        }
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

    /**
     * 客户端信息
     *
     * @return 每一行内容
     */
    public static List<String> getInfoLines() {
        List<String> list = new ArrayList<>();

        list.add("配置文件: config/allmusic_client.json");
        list.add("图片大小 picSize: " + getPicSize()
                + "（范围 " + MIN_PIC_SIZE + "-" + MAX_PIC_SIZE + "，/music_client imagesize <大小> 修改）");
        list.add("音频队列: " + (config == null ? 0 : config.queueSize));
        list.add("HUD 渲染层: " + (hud == null ? "未初始化" : "已初始化"));

        if (hud != null) {
            list.add("HUD 数据: " + (hud.hasHudData() ? "已收到" : "未收到（没进服或者服务端没发）"));
            list.add("当前封面: " + (hud.hasImage() ? "有，尺寸 " + hud.getPicSize() + "x" + hud.getPicSize() : "无"));
            String url = hud.getLastPicUrl();
            if (url != null && !url.isEmpty()) {
                list.add("封面地址: " + url);
            }
        }

        list.add("播放状态: " + (isPlay() ? "播放中" : "未播放"));

        return list;
    }

    /**
     * 客户端自检
     *
     * @return 每一行结果，[OK] 正常，[!!] 异常
     */
    public static List<String> getTestLines() {
        List<String> list = new ArrayList<>();

        list.add(check(config != null, "核心初始化"));
        list.add(check(bridge != null, "游戏桥"));
        list.add(check(client != null, "网络客户端"));
        list.add(check(player != null, "音频播放器"));
        list.add(check(hud != null, "HUD 渲染层"));

        if (bridge != null) {
            list.add("  屏幕大小: " + bridge.getScreenWidth() + "x" + bridge.getScreenHeight());
            list.add("  字体高度: " + bridge.getFontHeight());
        }

        if (hud != null) {
            list.addAll(hud.getTestLines());
        }

        return list;
    }

    private static String check(boolean ok, String text) {
        return (ok ? "[OK] " : "[!!] ") + text;
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
        if (player != null)
            player.closePlayer();
        if (hud != null)
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
        if (hud != null)
            hud.update();
    }

    /**
     * 读取数据包
     *
     * @param buffer 数据包
     */
    public static void packRead(ByteBuf buffer) {
        if (buffer == null || !buffer.isReadable()) {
            return;
        }
        // 1.7.10 的 FML 会把原始 payload 直接交给监听器，且 decode 会修改 buffer 的下标，
        // 这里复制一份再解析，避免影响其他监听器/FML 自身对同一个 buffer 的使用
        packDo(MusicPacketCodec.decode(buffer.copy()));
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
