package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.command.CommandEX;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandBungee extends Command implements TabExecutor {
    public CommandBungee() {
        super("music");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandEX.execute(sender, sender.getName(), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return CommandEX.getTabList(sender, sender.getName(), args);
    }
}
