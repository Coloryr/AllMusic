package com.coloryr.allmusic.server.core.objs.message;

public class SearchObj {
    public String noPer;
    public String cantSearch;
    public String res;
    public String emptySearch;
    public String errorNum;
    public String choice;
    public String select;
    public String cantNext;
    public String cantLast;
    public String startSearch;
    public String lastPage;
    public String nextPage;

    public static SearchObj make() {
        SearchObj obj = new SearchObj();
        obj.init();

        return obj;
    }

    public boolean check() {
        if (noPer == null)
            return true;
        if (cantSearch == null)
            return true;
        if (res == null)
            return true;
        if (emptySearch == null)
            return true;
        if (errorNum == null)
            return true;
        if (choice == null)
            return true;
        if (cantNext == null)
            return true;
        if (cantLast == null)
            return true;
        if (lastPage == null)
            return true;
        if (nextPage == null)
            return true;
        if (select == null)
            return true;
        return startSearch == null;
    }

    public void init() {
        if (startSearch == null)
            startSearch = "<light_purple>[AllMusic]<yellow>正在排队搜歌";
        if (noPer == null)
            noPer = "<light_purple>[AllMusic]<red>你没有权限搜歌";
        if (cantSearch == null)
            cantSearch = "<light_purple>[AllMusic]<red>无法搜索歌曲：" + ARG.name;
        if (res == null)
            res = "<light_purple>[AllMusic]<yellow>搜索结果";
        if (emptySearch == null)
            emptySearch = "<light_purple>[AllMusic]<red>你没有搜索音乐";
        if (errorNum == null)
            errorNum = "<light_purple>[AllMusic]<red>请输入正确的序号";
        if (choice == null)
            choice = "<light_purple>[AllMusic]<yellow>你选择了序号：" + ARG.index;
        if (select == null)
            select = "<light_purple>[AllMusic]<dark_green>输入/music select [序号] 或 /music [序号] 选择搜索结果";
        if (cantNext == null)
            cantNext = "<light_purple>[AllMusic]<red>无法下一页";
        if (cantLast == null)
            cantLast = "<light_purple>[AllMusic]<red>无法上一页";
        if (lastPage == null)
            lastPage = "<light_purple>[AllMusic]<dark_green>输入/music lastpage上一页";
        if (nextPage == null)
            nextPage = "<light_purple>[AllMusic]<dark_green>输入/music nextpage下一页";
    }
}

