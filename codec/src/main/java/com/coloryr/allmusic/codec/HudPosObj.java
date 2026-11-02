package com.coloryr.allmusic.codec;

public class HudPosObj {
    public HudLyricPosObj lyric;
    public HudInfoPosObj info;
    public HudStatePosObj state;
    public HudPicPosObj pic;

    public static HudPosObj make() {
        HudPosObj obj = new HudPosObj();
        obj.init();

        return obj;
    }

    public static HudInfoPosObj makeInfo() {
        HudInfoPosObj obj = new HudInfoPosObj();
        obj.x = 74;
        obj.y = 2;
        obj.pos = HudPosType.TOP_LEFT;
        obj.color = 0xffffff;
        obj.enable = true;
        obj.shadow = true;
        obj.alpha = 1.0f;
        obj.loop = true;
        obj.maxWidth = 300;
        obj.gap = 10;
        return obj;
    }

    public static HudLyricPosObj makeLyric() {
        HudLyricPosObj obj = new HudLyricPosObj();
        obj.x = 74;
        obj.y = 53;
        obj.gap = 10;
        obj.pos = HudPosType.TOP_LEFT;
        obj.color = 0xffffff;
        obj.enable = true;
        obj.shadow = true;
        obj.alpha = 1.0f;
        obj.maxWidth = 300;
        return obj;
    }

    public static HudStatePosObj makeState() {
        HudStatePosObj obj = new HudStatePosObj();
        obj.x = 74;
        obj.y = 43;
        obj.pos = HudPosType.TOP_LEFT;
        obj.color = 0xffffff;
        obj.enable = true;
        obj.shadow = true;
        obj.gap = 5;
        obj.alpha = 1.0f;
        return obj;
    }

    public static HudPicPosObj makePic() {
        HudPicPosObj obj = new HudPicPosObj();
        obj.x = 2;
        obj.y = 2;
        obj.pos = HudPosType.TOP_LEFT;
        obj.size = 70;
        obj.enable = true;
        obj.rotate = true;
        obj.alpha = 1.0f;
        obj.speed = 30;
        obj.bg = true;
        return obj;
    }

    public HudPosObj copy() {
        HudPosObj obj1 = new HudPosObj();
        obj1.info = this.info.copy();
        obj1.lyric = this.lyric.copy();
        obj1.state = this.state.copy();
        obj1.pic = this.pic.copy();
        return obj1;
    }

    public boolean check() {
        boolean save = false;
        if (lyric == null) {
            save = true;
            lyric = makeLyric();
        }
        if (info == null) {
            save = true;
            info = makeInfo();
        }
        if (state == null) {
            save = true;
            state = makeState();
        }
        if (pic == null) {
            save = true;
            pic = makePic();
        }
        return save;
    }

    public void init() {
        lyric = makeLyric();
        info = makeInfo();
        state = makeState();
        pic = makePic();
    }
}