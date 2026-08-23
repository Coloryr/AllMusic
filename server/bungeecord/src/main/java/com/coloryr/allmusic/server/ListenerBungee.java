package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerBungee implements Listener {
    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        PlayMusic.removeNowPlayPlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        AllMusic.joinPlay(event.getPlayer().getName());
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!event.getTag().equals(AllMusic.channelBC)) {
            return;
        }

        event.setCancelled(true);
        ByteArrayDataInput data = ByteStreams.newDataInput(event.getData());
        int type = data.readInt();
        if (type == 255 && event.getSender() instanceof Server) {
            Server server = (Server) event.getSender();
            SideBungee.TopServers.add(server);
            SideBungee.sendAllToServer(server);
            SideBungee.sendLyricToServer(server);
        } else if (type == 12 || type == 13) {
            String uuid = data.readUTF();
            int res = data.readInt();
            SideBungee.SendToBackend.put(uuid, res);
        }
    }
}
