package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.AllMusic;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AllMusicBungee extends Plugin {
    public static AllMusicBungee plugin;
    public static BungeeAudiences adventure;

    @Override
    public void onEnable() {
        plugin = this;
        adventure = BungeeAudiences.create(this);
        AllMusic.log = new LogBungee();
        SideBungee side = new SideBungee();
        AllMusic.side = side;
        AllMusic.economy = side;

        AllMusic.init(getDataFolder());
        if (!AllMusic.isRun) {
            return;
        }

        ProxyServer proxy = ProxyServer.getInstance();
        proxy.registerChannel(AllMusic.channelBC);
        proxy.getPluginManager().registerCommand(this, new CommandBungee());
        proxy.getPluginManager().registerListener(this, new ListenerBungee());

        AllMusic.start();
    }

    @Override
    public void onDisable() {
        AllMusic.stop();
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }
}
