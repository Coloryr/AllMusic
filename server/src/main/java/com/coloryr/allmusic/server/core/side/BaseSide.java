package com.coloryr.allmusic.server.core.side;

import com.coloryr.allmusic.codec.CommandType;
import com.coloryr.allmusic.codec.HudPosObj;
import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.saves.HudSave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

public abstract class BaseSide {
    /**
     * 主线程运行任务
     *
     * @param run 任务
     */
    public abstract void runTask(Runnable run);

    /**
     * 主线程延迟任务
     *
     * @param run   任务
     * @param delay 延迟
     */
    public abstract void runTask(Runnable run, int delay);

    /**
     * 检查权限
     *
     * @param player 用户
     * @return 是否有权限
     */
    public abstract boolean checkPermission(Object player);

    /**
     * 是否为玩家执行指令
     *
     * @param source 命令源
     * @return 是否为玩家
     */
    public abstract boolean isPlayer(Object source);

    /**
     * 检查权限
     *
     * @param player     用户
     * @param permission 权限
     * @return 是否有权限
     */
    public abstract boolean checkPermission(Object player, String permission);

    /**
     * 是否需要播放
     *
     * @return 结果
     */
    public abstract boolean needPlay(boolean islist);

    /**
     * 获取所有玩家
     *
     * @return 玩家列表
     */
    public abstract Collection<?> getPlayers();

    /**
     * 获取玩家名字
     *
     * @param player 玩家
     * @return 名字
     */
    public abstract String getPlayerName(Object player);

    /**
     * 获取玩家所在的服务器
     *
     * @param player 玩家
     * @return 名字
     */
    public abstract String getPlayerServer(Object player);

    /**
     * 向玩家发送数据包
     *
     * @param player 玩家
     * @param pack   数据
     */
    public abstract void send(Object player, MusicPack pack);

    /**
     * 根据名字获取玩家
     *
     * @param player 名字
     * @return 玩家
     */
    public abstract Object getPlayer(String player);

    /**
     * 发送bar消息
     *
     * @param player 玩家
     * @param data   消息
     */
    public abstract void sendBar(Object player, Component data);

    /**
     * 获取插件配置文件夹
     *
     * @return 文件夹
     */
    public abstract File getFolder();

    /**
     * 发送消息
     *
     * @param obj     接受对象
     * @param message 消息
     */
    public abstract void sendMessage(Object obj, Component message);

    /**
     * 当歌曲添加时触发的事件
     *
     * @param obj 歌曲信息
     * @return 是否阻止播放
     */
    public abstract boolean onMusicPlay(SongInfoObj obj);

    /**
     * 当歌曲播放时触发的事件
     *
     * @param obj   发送者
     * @param music 歌曲信息
     * @return 是否阻止添加
     */
    public abstract boolean onMusicAdd(Object obj, PlayerAddMusicObj music);

    /**
     * 广播消息
     *
     * @param data 消息
     */
    public abstract void broadcast(Component data);

    /**
     * 重载
     */
    public final void reload() {
        File basePath = getFolder();

        AllMusic.init(basePath);

        for (IMusicApi api : AllMusic.MUSIC_APIS.values()) {
            api.reload(basePath);
        }
    }

    /**
     * 发送歌曲位置信息
     *
     * @param player 用户名
     * @param pos    歌曲位置(毫秒)
     */
    public final void sendPos(String player, int pos) {
        Object player1 = getPlayer(player);
        if (player1 == null)
            return;
        if (AllMusic.isSkip(player, getPlayerServer(player), true))
            return;
        try {
            send(player1, new MusicPack.IntMusicPack(CommandType.POS, pos));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>清空Hud发生出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送停止指令
     *
     * @param name 用户名
     */
    public final void sendStop(String name) {
        PlayMusic.removeNowPlayPlayer(name);
        Object player = getPlayer(name);
        if (player == null)
            return;
        try {
            send(player, new MusicPack(CommandType.STOP));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>停止指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送停止指令
     */
    public final void sendStop() {
        PlayMusic.clearNowPlayer();
        try {
            for (Object player : getPlayers()) {
                send(player, new MusicPack(CommandType.STOP));
            }
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>停止指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 向指定玩家发送播放歌曲指令
     *
     * @param player 玩家
     * @param url    地址
     */
    public final void sendMusic(String player, String url) {
        PlayMusic.addNowPlayPlayer(player);
        Object player1 = getPlayer(player);
        if (player1 == null)
            return;
        try {
            if (AllMusic.isSkip(player, getPlayerServer(player), false))
                return;
            send(player1, new MusicPack.StringMusicPack(CommandType.PLAY, url));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>歌曲指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送Hud的歌词数据
     *
     * @param pack 数据
     */
    public final void sendHudLyric(MusicPack.LyricMusicPack pack) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                continue;
            try {
                HudPosObj obj = HudSave.getOrNew(name);
                if (!obj.lyric.enable)
                    continue;
                send(player, pack);
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>歌词发送出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送Hud的歌词数据
     */
    public final void sendHudKtv() {
        MusicPack.LyricKtvMusicPack pack = new MusicPack.LyricKtvMusicPack(PlayMusic.musicNowTime, PlayMusic.lyric.getKtvNow());

        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                continue;
            try {
                HudPosObj obj = HudSave.getOrNew(name);
                if (!obj.lyric.enable)
                    continue;
                send(player, pack);
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>歌词发送出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送Hud的信息数据
     *
     * @param data 数据
     */
    public final void sendHudInfo(String data) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                continue;
            try {
                HudPosObj obj = HudSave.getOrNew(name);
                if (!obj.info.enable)
                    continue;
                send(player, new MusicPack.StringMusicPack(CommandType.INFO, data));
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>歌词信息发送出错");
                e.printStackTrace();
            }
        }
    }

    public final void sendHudTime(MusicPack.TimeMusicPack pack) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                continue;
            try {
                HudPosObj obj = HudSave.getOrNew(name);
                if (!obj.state.enable)
                    continue;
                send(player, pack);
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>歌词信息发送出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送Hud的位置信息
     *
     * @param name 用户名
     */
    public final void sendHudPos(String name) {
        Object player = getPlayer(name);
        if (player == null)
            return;
        if (AllMusic.isSkip(name, null, false))
            return;
        try {
            HudPosObj obj = HudSave.getOrNew(name);
            String data = AllMusic.gson.toJson(obj);
            send(player, new MusicPack.StringMusicPack(CommandType.HUD_DATA, data));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>界面位置发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送Hud数据
     *
     * @param name 用户名
     * @param data 信息
     */
    public final void sendInfo(String name, String data) {
        try {
            Object player = getPlayer(name);
            if (player == null)
                return;
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                return;
            send(player, new MusicPack.StringMusicPack(CommandType.INFO, data));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>停止指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送Hud数据
     *
     * @param name 用户名
     * @param pack 数据
     */
    public final void sendLyric(String name, MusicPack.LyricMusicPack pack) {
        try {
            Object player = getPlayer(name);
            if (player == null)
                return;
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                return;
            send(player, pack);
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>停止指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 发送Hud位置数据
     */
    public final void sendHudUtilsAll() {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            try {
                HudPosObj obj = HudSave.getOrNew(name);
                String data = AllMusic.gson.toJson(obj);
                send(player, new MusicPack.StringMusicPack(CommandType.HUD_DATA, data));
            } catch (Exception e1) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>数据发送发生错误");
                e1.printStackTrace();
            }
        }
    }

    /**
     * 发送Bar数据
     *
     * @param data 数据
     */
    public final void sendBar(Component data) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            try {
                if (AllMusic.isSkip(name, getPlayerServer(player), true))
                    continue;
                sendBar(player, data);
            } catch (Exception e1) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>数据发送发生错误");
                e1.printStackTrace();
            }
        }
    }

    /**
     * 发送播放歌曲指令
     *
     * @param url 歌曲Url
     */
    public final void sendMusic(String url) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), false))
                continue;
            try {
                send(player, new MusicPack.StringMusicPack(CommandType.PLAY, url));
                PlayMusic.addNowPlayPlayer(name);
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>歌曲指令发送出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送图片数据指令
     *
     * @param url 图片地址
     */
    public final void sendPic(String url) {
        for (Object player : getPlayers()) {
            String name = getPlayerName(player);
            if (name == null)
                continue;
            name = name.toLowerCase(Locale.ROOT);
            if (AllMusic.isSkip(name, getPlayerServer(player), true))
                continue;
            HudPosObj obj = HudSave.getOrNew(name);
            if (!obj.pic.enable)
                continue;
            try {
                send(player, new MusicPack.StringMusicPack(CommandType.IMG, url));
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>图片指令发送出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送图片数据指令
     *
     * @param player 用户名
     * @param url    图片地址
     */
    public final void sendPic(String player, String url) {
        Object player1 = getPlayer(player);
        if (player1 == null)
            return;
        if (AllMusic.isSkip(player, getPlayerServer(player), true))
            return;
        try {
            send(player1, new MusicPack.StringMusicPack(CommandType.IMG, url));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>图片指令发送出错");
            e.printStackTrace();
        }
    }

    /**
     * 清理Hud数据
     *
     * @param name 用户名
     */
    public final void clearHud(String name) {
        Object player = getPlayer(name);
        if (player == null)
            return;
        try {
            send(player, new MusicPack(CommandType.CLEAR));
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>清空Hud发生出错");
            e.printStackTrace();
        }
    }

    /**
     * 清理Hud数据
     */
    public final void clearHud() {
        for (Object player : getPlayers()) {
            try {
                send(player, new MusicPack(CommandType.CLEAR));
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>清空Hud发生出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 在主线程中广播消息
     *
     * @param data 消息
     */
    public final void broadcastInTask(Component data) {
        runTask(() -> broadcast(data));
    }

    /**
     * 在主线程中广播消息
     *
     * @param data 消息
     */
    public final void broadcastInTask(String data) {
        broadcastInTask(miniMessage(data));
    }

    /**
     * 在主线程发送消息
     *
     * @param obj     接受对象
     * @param message 消息
     */
    public final void sendMessageTask(Object obj, String message) {
        runTask(() -> sendMessage(obj, miniMessage(message)));
    }

    /**
     * 广播消息
     *
     * @param data 消息
     */
    public final void broadcast(String data) {
        broadcast(miniMessage(data));
    }

    /**
     * 发送消息
     *
     * @param obj     接受对象
     * @param message 消息
     */
    public final void sendMessage(Object obj, String message) {
        sendMessage(obj, miniMessage(message));
    }

    public abstract Component miniMessage(String input);

    public abstract Component miniMessageRun(String input, String command);

    public abstract Component miniMessageSuggest(String input, String command);

    /**
     * 更新信息
     */
    public void updateInfo() {

    }

    /**
     * 更新歌词
     */
    public void updateLyric() {

    }

    /**
     * BC发送ping包
     */
    public void ping() {

    }

    /***
     * 在主线程中广播
     * @param data 数据
     */
    public void sendBarInTask(Component data) {
        runTask(() -> sendBar(data));
    }

    /***
     * 在主线程中广播
     * @param data 数据
     */
    public void sendBarInTask(String data) {
        sendBarInTask(miniMessage(data));
    }
}
