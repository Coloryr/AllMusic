package com.coloryr.allmusic.server.core.utils;

import com.coloryr.allmusic.codec.*;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.music.LyricSave;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.config.LimitObj;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.saves.HudSave;

public class HudUtils {
    /**
     * 设置组件位置
     *
     * @param player 用户名
     * @param pos    位置
     * @param x      x
     * @param y      y
     * @return 组件数据
     */
    public static HudBasePosObj setHudPos(String player, HudType pos, String x, String y) {
        HudPosObj obj = HudSave.getOrNew(player);

        if (!Function.isInteger(x) && !Function.isInteger(y))
            return null;
        int x1 = Integer.parseInt(x);
        int y1 = Integer.parseInt(y);

        HudBasePosObj posOBJ;
        switch (pos) {
            case LYRIC:
                posOBJ = obj.lyric;
                break;
            case INFO:
                posOBJ = obj.info;
                break;
            case PIC:
                posOBJ = obj.pic;
                break;
            case STATE:
                posOBJ = obj.state;
                break;
            default:
                return null;
        }
        posOBJ.x = x1;
        posOBJ.y = y1;

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return posOBJ;
    }

    /**
     * 设置组件透明度
     *
     * @param player 用户名
     * @param pos    位置
     * @param alpha  透明度
     * @return 组件数据
     */
    public static float setHudAlpha(String player, HudType pos, String alpha) {
        HudPosObj obj = HudSave.getOrNew(player);

        if (!Function.isInteger(alpha))
            return -1;
        float alpha1 = Float.parseFloat(alpha);
        if (alpha1 > 1 || alpha1 < 0) {
            alpha1 = 1;
        }
        if (pos == null) {
            obj.info.alpha = alpha1;
            obj.lyric.alpha = alpha1;
            obj.state.alpha = alpha1;
            obj.pic.alpha = alpha1;
        } else {
            switch (pos) {
                case LYRIC:
                    obj.lyric.alpha = alpha1;
                    break;
                case INFO:
                    obj.info.alpha = alpha1;
                    break;
                case STATE:
                    obj.state.alpha = alpha1;
                    break;
                case PIC:
                    obj.pic.alpha = alpha1;
                    break;
            }
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return alpha1;
    }

    /**
     * 设置组件最大宽度
     *
     * @param player 用户名
     * @param pos    位置
     * @param width  最大宽度
     * @return 组件数据
     */
    public static int setHudHudMaxWidth(String player, HudType pos, String width) {
        HudPosObj obj = HudSave.getOrNew(player);

        if (!Function.isInteger(width))
            return -1;
        int maxwidth = Integer.parseInt(width);
        if (maxwidth <= 0) {
            return -1;
        }
        switch (pos) {
            case LYRIC:
                obj.lyric.maxWidth = maxwidth;
                break;
            case INFO:
                obj.info.maxWidth = maxwidth;
                break;
            default:
                return -1;
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return maxwidth;
    }

    /**
     * 更新Hud的信息数据
     */
    public static void sendHudNowData() {
        String info;
        if (PlayMusic.nowPlayMusic == null) {
            info = AllMusic.getMessage().hud.emptyMusic;
        } else {
            info = AllMusic.getMessage().hud.music
                    .replace(ARG.name, TextUtils.escapeVar(infoLimit(PlayMusic.nowPlayMusic.getName())))
                    .replace(ARG.musicAuthor, TextUtils.escapeVar(infoLimit(PlayMusic.nowPlayMusic.getAuthor())))
                    .replace(ARG.musicAlia, TextUtils.escapeVar(infoLimit(PlayMusic.nowPlayMusic.getAlia())))
                    .replace(ARG.musicAl, TextUtils.escapeVar(infoLimit(PlayMusic.nowPlayMusic.getAl())))
                    .replace(ARG.player, PlayMusic.nowPlayMusic.getCall());
        }

        AllMusic.side.sendHudInfo(info);
    }

    public static void sendHudTime() {
        MusicPack.TimeMusicPack pack = new MusicPack.TimeMusicPack(PlayMusic.musicAllTime, PlayMusic.musicNowTime);
        AllMusic.side.sendHudTime(pack);
    }

    /**
     * 更新Hud的歌词数据
     */
    public static void sendHudLyricData() {
        String lyr;
        String tran;
        String ktv;
        LyricSave obj = PlayMusic.lyric;
        if (obj == null) {
            lyr = AllMusic.getMessage().hud.emptyLyric;
            tran = "";
            ktv = "";
        } else {
            String lyric = obj.getLyric();
            String tLyric = obj.getTlyric();
            lyr = AllMusic.getMessage().hud.lyric
                    .replace(ARG.lyric, TextUtils.escapeVar(lyric == null ? "" : lyric));
            tran = AllMusic.getMessage().hud.tlyric
                    .replace(ARG.lyric, TextUtils.escapeVar(tLyric == null ? "" : tLyric));
            ktv = AllMusic.getMessage().hud.klyric
                    .replace(ARG.lyric, TextUtils.escapeVar(lyric == null ? "" : lyric));
        }

        AllMusic.side.sendHudLyric(new MusicPack.LyricMusicPack(lyr, tran, ktv));
    }

    /**
     * 更新Hud的开启关闭数据
     *
     * @param player 用户名
     * @param pos    输入数据
     * @return 设置结果
     */
    public static boolean setHudEnable(String player, HudType pos, String arg) {
        HudPosObj obj = HudSave.getOrNew(player);
        boolean res = false;
        boolean value = false;
        boolean have = false;
        if (arg != null) {
            try {
                value = Boolean.parseBoolean(arg);
                have = true;
            } catch (Exception e) {
                return false;
            }
        }
        if (pos == null) {
            if (have) {
                res = obj.info.enable = obj.lyric.enable =
                        obj.pic.enable = obj.state.enable = value;
            } else if (obj.info.enable && obj.lyric.enable && obj.pic.enable && obj.state.enable) {
                obj.info.enable = obj.lyric.enable = obj.pic.enable = obj.state.enable = false;
            } else {
                res = obj.info.enable = obj.lyric.enable = obj.pic.enable = obj.state.enable = true;
            }
        } else {
            switch (pos) {
                case INFO:
                    res = obj.info.enable = have ? value : !obj.info.enable;
                    break;
                case LYRIC:
                    res = obj.lyric.enable = have ? value : !obj.lyric.enable;
                    break;
                case PIC:
                    res = obj.pic.enable = have ? value : !obj.pic.enable;
                    break;
                case STATE:
                    res = obj.state.enable = have ? value : !obj.state.enable;
                    break;
            }
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);

        return res;
    }

    /**
     * 清空Hud数据
     */
    public static void sendClearHud() {
        AllMusic.side.clearHud();
    }

    /**
     * 清空Hud数
     *
     * @param player 用户名
     */
    public static void sendClearHud(String player) {
        AllMusic.side.clearHud(player);
    }

    /**
     * 发送Hud位置信息
     *
     * @param player 用户名
     */
    public static void sendHudPos(String player) {
        AllMusic.side.runTask(() -> {
            try {
                AllMusic.side.sendHudPos(player);
            } catch (Exception e1) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>数据发送发生错误");
                e1.printStackTrace();
            }
        });
    }

    /**
     * 重置Hud位置
     *
     * @param player 用户名
     */
    public static void reset(String player) {
        HudPosObj obj = HudSave.defaultHud.copy();
        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
    }

    public static void reset(String player, HudType type) {
        HudPosObj obj = HudSave.defaultHud.copy();
        HudPosObj obj1 = HudSave.getOrNew(player);
        switch (type) {
            case INFO:
                obj1.info = obj.info;
                break;
            case LYRIC:
                obj1.lyric = obj.lyric;
                break;
            case PIC:
                obj1.pic = obj.pic;
                break;
        }

        HudSave.update(player, obj1);
        HudUtils.sendHudPos(player);
    }

    /**
     * 设置Hud的图片大小
     *
     * @param player 用户名
     * @param size   大小
     * @return 结果
     */
    public static boolean setPicSize(String player, String size) {
        HudPosObj obj = HudSave.getOrNew(player);
        if (!Function.isInteger(size))
            return false;

        obj.pic.size = Integer.parseInt(size);

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return true;
    }

    /**
     * 图片旋转开关
     *
     * @param player 用户名
     * @param open   开关
     * @return 结果
     */
    public static boolean setPicRotate(String player, String open) {
        HudPosObj obj = HudSave.getOrNew(player);

        if (open != null) {
            obj.pic.rotate = Boolean.parseBoolean(open);
        } else {
            obj.pic.rotate = !obj.pic.rotate;
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return obj.pic.rotate;
    }

    /**
     * 图片旋转开关
     *
     * @param player 用户名
     * @param open   开关
     * @return 结果
     */
    public static boolean setPicBg(String player, String open) {
        HudPosObj obj = HudSave.getOrNew(player);

        if (open != null) {
            obj.pic.bg = Boolean.parseBoolean(open);
        } else {
            obj.pic.bg = !obj.pic.bg;
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return obj.pic.bg;
    }

    /**
     * 图片旋转速度设置
     *
     * @param player 用户名
     * @param size   大小
     * @return 结果
     */
    public static boolean setPicRotateSpeed(String player, String size) {
        HudPosObj obj = HudSave.getOrNew(player);
        if (!Function.isInteger(size))
            return false;

        obj.pic.speed = Integer.parseInt(size);

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);
        return true;
    }

    public static HudPosType setPos(String player, HudType hud, String arg) {
        HudPosType type = null;
        try {
            if (Function.isInteger(arg)) {
                int index = Integer.parseInt(arg);
                type = HudPosType.values()[index];
            } else {
                type = HudPosType.valueOf(arg);
            }
        } catch (Exception ignored) {

        }
        if (type == null) {
            return null;
        }

        HudPosObj obj = HudSave.getOrNew(player);
        switch (hud) {
            case INFO:
                obj.info.pos = type;
                break;
            case LYRIC:
                obj.lyric.pos = type;
                break;
            case PIC:
                obj.pic.pos = type;
                break;
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);

        return type;
    }

    public static int setColor(String player, HudType type, String arg) {
        int color;
        try {
            if (arg.startsWith("0x")) {
                color = Integer.parseUnsignedInt(arg.substring(2), 16);
            } else if (arg.startsWith("#")) {
                color = Integer.parseUnsignedInt(arg.substring(1), 16);
            } else {
                color = Integer.parseUnsignedInt(arg);
            }
        } catch (Exception ignored) {
            return -1;
        }

        color = (color & -67108864) == 0 ? color | 0xFF000000 : color;

        HudPosObj obj = HudSave.getOrNew(player);
        switch (type) {
            case INFO:
                obj.info.color = color;
                break;
            case LYRIC:
                obj.lyric.color = color;
                break;
            case PIC:
                return -1;
        }

        HudSave.update(player, obj);
        HudUtils.sendHudPos(player);

        return color;
    }

    public static boolean setShadow(String name, HudType pos, String arg) {
        HudPosObj obj = HudSave.getOrNew(name);
        boolean res = false;
        boolean value = false;
        boolean have = false;
        if (arg != null) {
            try {
                value = Boolean.parseBoolean(arg);
                have = true;
            } catch (Exception e) {
                return false;
            }
        }

        if (pos == null) {
            if (have) {
                res = obj.info.shadow = obj.state.shadow = obj.lyric.shadow = value;
            } else if (obj.info.shadow && obj.lyric.shadow && obj.state.shadow) {
                obj.info.shadow = obj.lyric.shadow = obj.state.shadow = false;
            } else {
                res = obj.info.shadow = obj.lyric.shadow = obj.state.shadow = true;
            }
        } else {
            switch (pos) {
                case INFO:
                    res = obj.info.shadow = have ? value : !obj.info.shadow;
                    break;
                case LYRIC:
                    res = obj.lyric.shadow = have ? value : !obj.lyric.shadow;
                    break;
            }
        }

        HudSave.update(name, obj);
        HudUtils.sendHudPos(name);

        return res;
    }

    public static String infoLimit(String info) {
        LimitObj limit = AllMusic.getConfig().limit;
        if (limit.infoLimit && info.length() > limit.infoLimitSize)
            info = info.substring(0, limit.infoLimitSize - 1) + limit.limitText;

        return info;
    }

    public static String listLimit(String list) {
        LimitObj limit = AllMusic.getConfig().limit;
        if (limit.listLimit && list.length() > limit.listLimitSize)
            list = list.substring(0, limit.listLimitSize - 1) + limit.limitText;

        return list;
    }

    public static String messageLimit(String message) {
        LimitObj limit = AllMusic.getConfig().limit;
        if (limit.messageLimit && message.length() > limit.messageLimitSize)
            message = message.substring(0, limit.messageLimitSize - 1) + limit.limitText;

        return message;
    }

    public static boolean setLoop(String name, HudType type, String arg) {
        boolean loop = Boolean.parseBoolean(arg);

        HudPosObj obj = HudSave.getOrNew(name);
        switch (type) {
            case INFO:
                obj.info.loop = loop;
                break;
        }

        HudSave.update(name, obj);
        HudUtils.sendHudPos(name);

        return loop;
    }

    public static int setGap(String name, HudType type, String arg) {
        if (!Function.isInteger(arg))
            return -1;
        int gap = Integer.parseInt(arg);
        if (gap <= 0) {
            return -1;
        }

        HudPosObj obj = HudSave.getOrNew(name);
        switch (type) {
            case INFO:
                obj.info.gap = gap;
                break;
            case STATE:
                obj.state.gap = gap;
                break;
            case LYRIC:
                obj.lyric.gap = gap;
                break;
            case PIC:
                return -1;
        }

        HudSave.update(name, obj);
        HudUtils.sendHudPos(name);

        return gap;
    }
}
