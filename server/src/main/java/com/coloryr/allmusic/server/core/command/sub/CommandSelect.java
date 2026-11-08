package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.CommandEX;
import com.coloryr.allmusic.server.core.command.PermissionList;
import com.coloryr.allmusic.server.core.music.MusicSearch;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.SearchPageObj;
import com.coloryr.allmusic.server.core.saves.SaveTask;
import com.coloryr.allmusic.server.core.utils.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandSelect extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_SEARCH)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.noPer);
            return;
        }
        SearchPageObj obj = MusicSearch.getSearch(name);
        if (obj == null) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.emptySearch);
            return;
        }

        int index = args.length > 1 ? Function.getInt(args[1], -1) : -1;
        if (index < 1 || index > obj.getIndex()) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.errorNum);
            return;
        }

        select(sender, name, obj, index);
    }

    /**
     * 尝试把参数当作搜歌结果的序号来选择
     * <p>
     * 没有搜歌结果或序号无效时返回false，由调用方继续处理
     *
     * @param sender 发送者
     * @param name   用户名
     * @param arg    序号
     * @return 是否已处理
     */
    public static boolean selectSearch(Object sender, String name, String arg) {
        SearchPageObj obj = MusicSearch.getSearch(name);
        if (obj == null) {
            return false;
        }

        int index = Function.getInt(arg, -1);
        if (index < 1 || index > obj.getIndex()) {
            return false;
        }

        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_SEARCH)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.noPer);
            return true;
        }

        select(sender, name, obj, index);
        return true;
    }

    /**
     * 选择搜歌结果
     *
     * @param sender 发送者
     * @param name   用户名
     * @param obj    搜歌结果
     * @param index  序号（从1开始）
     */
    private static void select(Object sender, String name, SearchPageObj obj, int index) {
        String id = obj.getSong((obj.getPage() * 10) + (index - 1));
        AllMusic.side.sendMessage(sender,
                AllMusic.getMessage().search.choice.replace(ARG.index, "" + index));
        SaveTask.task(() -> CommandEX.addMusic(sender, name, obj.getApi(), id));
        MusicSearch.removeSearch(name);
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == 1 || (args.length == 2)) {
            List<String> list = new ArrayList<>();
            SearchPageObj obj = MusicSearch.getSearch(name);
            if (obj != null) {
                for (int a = 0; a < obj.getIndex(); a++) {
                    list.add(String.valueOf(a + 1));
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
