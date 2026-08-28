package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.PermissionList;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.music.PlayRuntime;
import com.coloryr.allmusic.server.core.music.VoteItem;
import com.coloryr.allmusic.server.core.objs.message.ARG;

public class CommandAgree extends ACommand  {
    @Override
    public void execute(Object sender, String name, String[] args) {
        if (AllMusic.getConfig().needPermission &&
                !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_VOTE)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().vote.noPermission);
            return;
        }

        VoteItem vote = PlayMusic.getVote();

        if (vote == null) {
            AllMusic.side.sendMessage(name, AllMusic.getMessage().vote.err4);
            return;
        }
        if (vote.votePlayer.contains(name)) {
            AllMusic.side.sendMessage(name, AllMusic.getMessage().vote.err5);
            return;
        }

        AllMusic.side.sendMessage(name, AllMusic.getMessage().vote.agree);
        String data = AllMusic.getMessage().vote.bqAgree;
        data = data.replace(ARG.player, name)
                .replace(ARG.count, String.valueOf(vote.votePlayer.size()))
                .replace(ARG.countAll, String.valueOf(PlayRuntime.getMiniVote()));
        AllMusic.side.broadcast(data);

        PlayMusic.addVote(name);
    }
}
