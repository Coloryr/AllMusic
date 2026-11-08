package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ICommand;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandCancel implements ICommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (args.length == 1) {
            SongInfoObj id = PlayMusic.findPlayerMusic(name);
            if (id == null) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.err1);
                return;
            }
            if (!id.getCall().equalsIgnoreCase(name)) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.err2.replace(ARG.musicName, TextUtils.escapeVar(id.getName()))
                        .replace(ARG.musicAuthor, TextUtils.escapeVar(id.getAuthor())));
                return;
            }
            PlayMusic.remove(id);
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.done);
        } else if (args.length == 2) {
            try {
                int index = Integer.parseInt(args[1]);
                SongInfoObj id = PlayMusic.findMusicIndex(index);
                if (id == null) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.err3
                            .replace(ARG.index, args[1]));
                    return;
                }
                if (!id.getCall().equalsIgnoreCase(name)) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.err2.replace(ARG.musicName, TextUtils.escapeVar(id.getName()))
                            .replace(ARG.musicAuthor, TextUtils.escapeVar(id.getAuthor())));
                    return;
                }

                PlayMusic.remove(id);
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.done);
            } catch (Exception e) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().cancel.err4);
            }
        } else {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == 1 || (args.length == 2 && args[1].isEmpty())) {
            List<String> list = new ArrayList<>();
            List<SongInfoObj> list1 = PlayMusic.getList();
            if (list1.size() > 1) {
                for (int a = 1; a < list1.size(); a++) {
                    SongInfoObj item = list1.get(a);
                    if (item.getCall().equalsIgnoreCase(name)) {
                        // 用户序号是 1 起（list[a] 对应序号 a+1）
                        list.add(String.valueOf(a + 1));
                    }
                }
            }
            return list;
        }

        return Collections.emptyList();
    }
}
