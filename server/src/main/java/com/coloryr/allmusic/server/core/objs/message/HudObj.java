package com.coloryr.allmusic.server.core.objs.message;

public class HudObj {
    public String state;
    public String set;
    public String set1;
    public String set2;
    public String set3;
    public String set4;
    public String set5;
    public String set6;
    public String set7;
    public String reset;
    public String emptyMusic;
    public String emptyList;
    public String emptyLyric;
    public String picSize;
    public String picRotate;
    public String picSpeed;
    public String picBg;
    public String music;
    public String lyric;
    public String tlyric;
    public String klyric;

    public static HudObj make() {
        HudObj obj = new HudObj();
        obj.init();

        return obj;
    }

    public boolean check() {
        if (state == null)
            return true;
        if (set == null)
            return true;
        if (set1 == null)
            return true;
        if (set2 == null)
            return true;
        if (set3 == null)
            return true;
        if (set4 == null)
            return true;
        if (set5 == null)
            return true;
        if (set6 == null)
            return true;
        if (reset == null)
            return true;
        if (emptyMusic == null)
            return true;
        if (emptyList == null)
            return true;
        if (emptyLyric == null)
            return true;
        if (picSize == null)
            return true;
        if (music == null)
            return true;
        if (lyric == null)
            return true;
        if (tlyric == null)
            return true;
        if (picRotate == null)
            return true;
        if (picSpeed == null)
            return true;
        if (picBg == null)
            return true;
        return klyric == null;
    }

    public void init() {
        if (state == null)
            state = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的状态为[<blue>" + ARG.value + "<yellow>]";
        if (set == null)
            set = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的坐标为[<blue>" + ARG.x + ", " + ARG.y + "<yellow>]";
        if (set1 == null)
            set1 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的对齐方式为[<blue>" + ARG.value + "<yellow>]";
        if (set2 == null)
            set2 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的颜色为[<blue>" + ARG.value + "<yellow>]";
        if (set3 == null)
            set3 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的阴影为[<blue>" + ARG.value + "<yellow>]";
        if (set4 == null)
            set4 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的透明度为[<blue>" + ARG.value + "<yellow>]";
        if (set5 == null)
            set5 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的循环模式为[<blue>" + ARG.value + "<yellow>]";
        if (set6 == null)
            set6 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的最大宽度为[<blue>" + ARG.value + "<yellow>]";
        if (set7 == null)
            set7 = "<light_purple>[AllMusic]<yellow>已设置[<aqua>" + ARG.hud + "<yellow>]的间距为[<blue>" + ARG.value + "<yellow>]";
        if (reset == null)
            reset = "<light_purple>[AllMusic]<yellow>界面[<aqua>" + ARG.hud + "<yellow>]恢复至默认位置";
        if (emptyMusic == null)
            emptyMusic = "没有播放的音乐";
        if (emptyList == null)
            emptyList = "队列中无歌曲";
        if (emptyLyric == null)
            emptyLyric = "无歌词";
        if (picSize == null)
            picSize = "<light_purple>[AllMusic]<yellow>设置图片尺寸为[" + ARG.value + "]";
        if (picRotate == null)
            picRotate = "<light_purple>[AllMusic]<yellow>设置图片旋转为[" + ARG.value + "]";
        if (picSpeed == null)
            picSpeed = "<light_purple>[AllMusic]<yellow>设置图片旋转速度为[" + ARG.value + "]";
        if (picBg == null)
            picBg = "<light_purple>[AllMusic]<yellow>设置图片底图为[" + ARG.value + "]";
        if (music == null)
            music = ARG.name + " " + ARG.musicAuthor + "\n" + ARG.musicAlia + "\n" + ARG.musicAl
                    + "\nby: " + ARG.player;
        if (lyric == null)
            lyric = ARG.lyric;
        if (tlyric == null)
            tlyric = ARG.lyric;
        if (klyric == null)
            klyric = "§b" + ARG.lyric;
    }
}
