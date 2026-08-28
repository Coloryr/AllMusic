package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.command.ACommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandApiCommand extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (args.length < 2) {
            AllMusic.side.sendMessage(sender, "<light_purple>[AllMusic]<dark_green><red>没有指定API");
            return;
        }

        String apiname = args[1];

        IMusicApi api = AllMusic.MUSIC_APIS.get(apiname);

        if (api == null) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }

        if (args.length == 2) {
            api.command(sender, name, new String[0]);
        } else {
            String[] newArgs = new String[args.length - 2];
            System.arraycopy(args, 2, newArgs, 0, newArgs.length);

            api.command(sender, name, newArgs);
        }
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == 1) {
            return new ArrayList<>(AllMusic.MUSIC_APIS.keySet());
        }

        String apiname = args[1];

        IMusicApi api = AllMusic.MUSIC_APIS.get(apiname);

        if (api == null) {
            AllMusic.side.sendMessage(player, AllMusic.getMessage().musicPlay.error2);
            return Collections.emptyList();
        }

        if (args.length == 2) {
            return api.tab(player, name, new String[0]);
        } else {
            String[] newArgs = new String[args.length - 2];
            System.arraycopy(args, 2, newArgs, 0, newArgs.length);

            return api.tab(player, name, newArgs);
        }
    }
}
