package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.utils.TextUtils;

public class CommandList extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (PlayMusic.nowPlayMusic == null || PlayMusic.nowPlayMusic.isNull()) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.emptyPlayingMusic);
        } else {
            String info = AllMusic.getMessage().musicPlay.nowPlay;
            info = info.replace(ARG.musicName, TextUtils.escapeVar(PlayMusic.nowPlayMusic.getName()))
                    .replace(ARG.musicAuthor, TextUtils.escapeVar(PlayMusic.nowPlayMusic.getAuthor()))
                    .replace(ARG.musicAl, TextUtils.escapeVar(PlayMusic.nowPlayMusic.getAl()))
                    .replace(ARG.musicAlia, TextUtils.escapeVar(PlayMusic.nowPlayMusic.getAlia()))
                    .replace(ARG.player, PlayMusic.nowPlayMusic.getCall());
            AllMusic.side.sendMessage(sender, info);
        }
        if (PlayMusic.getListSize() == 0) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.emptyPlay);
        } else {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.listMusic.head
                    .replace(ARG.count, "" + PlayMusic.getListSize()));
            AllMusic.side.sendMessage(sender, PlayMusic.getAllList());
        }
    }
}
