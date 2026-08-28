package com.coloryr.allmusic.server.core.objs.message;

public class VoteObj {
    public String noPermission;
    public String doVote;
    public String bq;
    public String bq1;
    public String bq2;
    public String agree;
    public String bqAgree;
    public String timeOut;
    public String next;
    public String err1;
    public String err3;
    public String err4;
    public String err5;
    public String err6;
    public String cancel;
    public String cancel1;
    public String list;

    public static VoteObj make() {
        VoteObj obj = new VoteObj();
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
        if (agree == null)
            return true;
        if (bqAgree == null)
            return true;
        if (timeOut == null)
            return true;
        if (err1 == null)
            return true;
        if (err3 == null)
            return true;
        if (err4 == null)
            return true;
        if (err5 == null)
            return true;
        if (err6 == null)
            return true;
        if (cancel == null)
            return true;
        if (cancel1 == null)
            return true;
        if (list == null)
            return true;
        return next == null;
    }

    public void init() {
        if (noPermission == null)
            noPermission = "<light_purple>[AllMusic]<red>你没有权限切歌";
        if (doVote == null)
            doVote = "<light_purple>[AllMusic]<yellow>已发起切歌投票";
        if (bq == null)
            bq = "<light_purple>[AllMusic]<yellow>" + ARG.player + "发起了切歌投票，" + ARG.time + "秒后结束，输入/music agree 同意切歌，需要至少" + ARG.countAll + "名玩家同意才会切歌。";
        if (bq1 == null)
            bq1 = "<light_purple>[AllMusic]<yellow>或者点击 ";
        if (bq2 == null)
            bq2 = "<green><underlined>同意切歌";
        if (agree == null)
            agree = "<light_purple>[AllMusic]<yellow>你同意切歌";
        if (bqAgree == null)
            bqAgree = "<light_purple>[AllMusic]<yellow>" + ARG.player + "同意投票，共有" + ARG.count + "名玩家同意投票，需要至少" + ARG.countAll + "名玩家同意才会通过。";
        if (timeOut == null)
            timeOut = "<light_purple>[AllMusic]<yellow>投票时间结束，共有" + ARG.count + "同意，需要" + ARG.countAll + "才能通过";
        if (err1 == null)
            err1 = "<light_purple>[AllMusic]<red>你没有发起过切歌投票";
        if (err3 == null)
            err3 = "<light_purple>[AllMusic]<red>切歌投票已经在进行中了";
        if (err4 == null)
            err4 = "<light_purple>[AllMusic]<red>当前没有进行中的投票";
        if (err5 == null)
            err5 = "<light_purple>[AllMusic]<red>你已经通过了当前投票";
        if (err6 == null)
            err6 = "<light_purple>[AllMusic]<red>投票队列已满，无法发起投票";
        if (next == null)
            next = "<light_purple>[AllMusic]<yellow>已切换到播放下一首歌";
        if (cancel == null)
            cancel = "<light_purple>[AllMusic]<yellow>切歌投票已被发起者取消";
        if (cancel1 == null)
            cancel1 = "<light_purple>[AllMusic]<yellow>歌曲结束，切歌投票终止";
        if (list == null)
            list = "<light_purple>[AllMusic]<yellow>剩余" + ARG.count + "个投票";
    }
}
