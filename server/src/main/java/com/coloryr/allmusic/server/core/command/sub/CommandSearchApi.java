package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.CommandEX;
import com.coloryr.allmusic.server.core.command.PermissionList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandSearchApi extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_SEARCH)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.noPer);
            return;
        }

        if (CommandEX.checkMoney(sender, name, AllMusic.getConfig().cost.searchCost)) {
            return;
        }

        if (CommandEX.cost(sender, name, AllMusic.getConfig().cost.searchCost,
                AllMusic.getMessage().cost.search)) {
            return;
        }

        if (args.length < 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }
        if (args.length < 3) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.emptySearch);
            return;
        }

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        AllMusic.side.sendMessage(sender, AllMusic.getMessage().search.startSearch);
        CommandEX.searchMusicApi(sender, name, newArgs);
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (index == 1 && args.length <= index + 1) {
            return new ArrayList<>(AllMusic.MUSIC_APIS.keySet());
        }
        return Collections.emptyList();
    }
}