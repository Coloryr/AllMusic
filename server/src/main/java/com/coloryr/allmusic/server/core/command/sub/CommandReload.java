package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.command.ACommand;

public class CommandReload extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        AllMusic.side.reload();
        AllMusic.side.sendMessage(sender, "<light_purple>[AllMusic]<dark_green>已重读配置文件");
    }
}
