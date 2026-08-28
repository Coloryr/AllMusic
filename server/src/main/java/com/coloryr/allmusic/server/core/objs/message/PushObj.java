package com.coloryr.allmusic.server.core.objs.message;

public class PushObj {
    public String noPermission;
    public String doVote;
    public String bq;
    public String bq1;
    public String bq2;
    public String doPush;
    public String noId;
    public String noId1;
    public String pushErr;
    public String cancel;
    public String cancel1;
    public String cancel2;
    public String err1;
    public String err3;
    public String err4;
    public String err5;

    public static PushObj make() {
        PushObj obj = new PushObj();
        obj.init();

        return obj;
    }

    public boolean check() {
        if (noPermission == null)
            return true;
        if (doVote == null)
            return true;
        if (bq == null)
            return true;
        if (bq1 == null)
            return true;
        if (bq2 == null)
            return true;
        if (noId == null)
            return true;
        if (noId1 == null)
            return true;
        if (pushErr == null)
            return true;
        if (cancel == null)
            return true;
        if (err1 == null)
            return true;
        if (err3 == null)
            return true;
        if (err4 == null)
            return true;
        if (err5 == null)
            return true;
        if (cancel1 == null)
            return true;
        if (cancel2 == null)
            return true;
        return doPush == null;
    }

    public void init() {
        if (noPermission == null)
            noPermission = "<light_purple>[AllMusic]<red>你没有权限插歌";
        if (doVote == null)
            doVote = "<light_purple>[AllMusic]<yellow>已发起插歌投票";
        if (bq == null)
            bq = "<light_purple>[AllMusic]<yellow>" + ARG.player + "发起了插歌投票，将曲目" + ARG.musicName + "-" + ARG.musicAuthor + "调整到下一首播放，" + ARG.time + "秒后结束，输入/music push 同意插歌，需要至少" + ARG.countAll + "名玩家同意才会插歌。";
        if (bq1 == null)
            bq1 = "<light_purple>[AllMusic]<yellow>或者点击 ";
        if (bq2 == null)
            bq2 = "<green><underlined>同意插歌";
        if (doPush == null)
            doPush = "<light_purple>[AllMusic]<yellow>播放顺序已调整";
        if (noId == null)
            noId = "<light_purple>[AllMusic]<red>没有找到你的点歌";
        if (noId1 == null)
            noId1 = "<light_purple>[AllMusic]<red>没有找到序号为" + ARG.index + "的点歌";
        if (pushErr == null)
            pushErr = "<light_purple>[AllMusic]<red>这首歌已经是下一首播放了";
        if (err1 == null)
            err1 = "<light_purple>[AllMusic]<red>你没有发起插歌";
        if (err4 == null)
            err4 = "<light_purple>[AllMusic]<red>切歌投票跳过不存在的音乐投票";
        if (err5 == null)
            err5 = "<light_purple>[AllMusic]<red>你已经申请过切歌了，不能再继续申请";
        if (cancel == null)
            cancel = "<light_purple>[AllMusic]<yellow>插歌投票已被发起者取消";
        if (cancel1 == null)
            cancel1 = "<light_purple>[AllMusic]<yellow>已删除你发起的投票";
        if (cancel2 == null)
            cancel2 = "<light_purple>[AllMusic]<yellow>下一首歌就是投票歌曲，已取消投票";
    }
}
