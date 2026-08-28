package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.PermissionList;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.music.VoteItem;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.saves.BanSave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandPush extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_PUSH)) {
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().push.noPermission));
            return;
        }
        if (PlayMusic.getListSize() == 0 && PlayMusic.getIdleListSize() == 0) {
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().musicPlay.emptyPlay));
        }
        SongInfoObj music = null;
        if (args.length == 1) {
            music = PlayMusic.findPlayerMusic(name);
            if (music == null) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.noId);
                return;
            }
            SongInfoObj id1 = PlayMusic.findMusicIndex(1);
            if (id1 != null && id1.getId().equalsIgnoreCase(music.getId())) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.pushErr);
                return;
            }

            VoteItem item = new VoteItem(music.getApi(), music.getId(), name, VoteItem.VoteType.PUSH);
            if (PlayMusic.startVote(item)) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.doVote);
            } else {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.err3);
            }

            AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.doVote);
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("cancel")) {
                if (!PlayMusic.haveVote(name, VoteItem.VoteType.PUSH)) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.err1);
                    return;
                }
                PlayMusic.removeVote(name, VoteItem.VoteType.PUSH);
                AllMusic.side.sendMessage(name, AllMusic.getMessage().push.cancel1);
                return;
            } else {
                try {
                    int index = Integer.parseInt(args[1]);
                    music = PlayMusic.findMusicIndex(index);
                } catch (Exception e) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.noId);
                    return;
                }
                if (music == null) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.noId1.replace(ARG.index, args[1]));
                    return;
                }

                VoteItem item = new VoteItem(music.getApi(), music.getId(), name, VoteItem.VoteType.PUSH);
                if (PlayMusic.startVote(item)) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.doVote);
                } else {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.err3);
                }

                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.doVote);
            }
        } else if (args.length > 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            return;
        }
        BanSave.removeMutePlayer(name);
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == 1 || (args.length == 2 && args[1].isEmpty())) {
            List<String> list = new ArrayList<>();
            List<SongInfoObj> list1 = PlayMusic.getList();
            for (int a = 1; a < list1.size(); a++) {
                SongInfoObj item = list1.get(a);
                if (item.getCall().equalsIgnoreCase(name)) {
                    list.add(String.valueOf(a));
                }
            }

            return list;
        }
        return Collections.emptyList();
    }
}
