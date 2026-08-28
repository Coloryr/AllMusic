package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.saves.BanSave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandBan extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (args.length < 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            return;
        }

        String musicID = null;
        IMusicApi api = null;

        if (args.length == 2) {
            api = AllMusic.MUSIC_APIS.get(AllMusic.getConfig().defaultApi);
            musicID = args[1];
        } else if (args.length == 3) {
            api = AllMusic.MUSIC_APIS.get(args[1]);
            musicID = args[2];
        } else {
            AllMusic.side.sendMessage(sender, "<light_purple>[AllMusic]<dark_green>错误的指令");
        }

        if (api == null) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }

        if (api.checkId(musicID)) {
            BanSave.addBanMusic(musicID, api.getId());
            AllMusic.side.sendMessage(sender, "<light_purple>[AllMusic]<dark_green>音乐API " + api.getId() + " 已禁止点歌" + musicID);
        } else {
            AllMusic.side.sendMessage(sender, "<light_purple>[AllMusic]<dark_green>请输入有效的ID");
        }
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == index || (args.length == index + 1)) {
            List<String> list = new ArrayList<>();
            if (PlayMusic.nowPlayMusic != null) {
                list.add(PlayMusic.nowPlayMusic.getId());
            }
            for (SongInfoObj item : PlayMusic.getList()) {
                list.add(item.getId());
            }

            return list;
        }

        return Collections.emptyList();
    }
}
