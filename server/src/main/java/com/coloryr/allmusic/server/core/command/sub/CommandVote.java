package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.PermissionList;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.music.VoteItem;
import com.coloryr.allmusic.server.core.saves.BanSave;

import java.util.Locale;

public class CommandVote extends ACommand {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_VOTE)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.noPermission);
            return;
        } else if (PlayMusic.fullVoteList()) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.err6);
        } else if (PlayMusic.getListSize() == 0 && PlayMusic.getIdleListSize() == 0) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.emptyPlay);
        } else if (PlayMusic.nowPlayMusic == null) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.emptyPlayingMusic);
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("cancel")) {
                VoteItem vote = PlayMusic.getVote();
                if (vote == null) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.err4);
                    return;
                }
                if (!PlayMusic.haveVote(name, VoteItem.VoteType.NEXT)) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.err1);
                    return;
                }
                PlayMusic.removeVote(name, VoteItem.VoteType.NEXT);
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().push.cancel1);
            } else {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            }
            return;
        } else if (args.length > 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            return;
        } else {
            VoteItem item = new VoteItem(PlayMusic.nowPlayMusic.getApi(), PlayMusic.nowPlayMusic.getId(), name, VoteItem.VoteType.NEXT);
            item.votePlayer.add(name.toLowerCase(Locale.ROOT));

            if (PlayMusic.startVote(item)) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.doVote);
            } else {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.err3);
            }
        }
        BanSave.removeMutePlayer(name);
    }
}
