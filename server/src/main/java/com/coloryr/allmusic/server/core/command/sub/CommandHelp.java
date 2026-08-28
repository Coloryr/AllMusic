package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;

public class CommandHelp extends ACommand {

    @Override
    public void execute(Object sender, String name, String[] args) {
        AllMusic.side.sendMessage(sender, AllMusic.getMessage().help.normal.head);
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.base)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.stop)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music stop")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.list)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music list")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.cancel)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music cancel")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.vote)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music vote")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.vote1)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music vote cancel")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.push)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickRun, "/music push ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.push1)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music push cancel")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.vote)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music vote")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.mute)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music mute")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.mutelist)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music mute list")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.search)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music search ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.select)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music select ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.nextpage)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music nextpage")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.lastpage)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music lastpage")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud9)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music hud enable")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud10)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music hud reset")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud1)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud2)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud6)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud7)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud8)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud11)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud12)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud13)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud14)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud3)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud pic size ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud4)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud pic rotate ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.hud5)
                .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music hud pic speed ")));
        AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.normal.join)
                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music join")));
        if (AllMusic.side.checkPermission(sender)) {
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.reload)
                    .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music reload")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.next)
                    .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music next")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.ban)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music ban ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.ban1)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music ban ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.banPlayer)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music banplayer ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.unban)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music unban ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.unbanPlayer)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music unbanplayer ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.delete)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music delete ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.addList)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music addlist ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.addList1)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music addlist ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.clearList)
                    .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music clearlist")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.clearBanList)
                    .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music clearban")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.clearBanPlayerList)
                    .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().click.clickRun, "/music clearbanplayer")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.test)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music test ")));
            AllMusic.side.sendMessage(sender, AllMusic.side.miniMessage(AllMusic.getMessage().help.admin.test1)
                    .append(AllMusic.side.miniMessageSuggest(AllMusic.getMessage().click.clickCheck, "/music test ")));
        }
    }
}
