package com.coloryr.allmusic.server;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.codec.MusicPacketCodec;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IEconomy;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.event.MusicAddEvent;
import com.coloryr.allmusic.server.event.MusicPlayEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class SideVelocity extends BaseSide implements IEconomy {
    public static final Set<ServerConnection> TopServers = new CopyOnWriteArraySet<>();

    public static final Map<String, Integer> SendToBackend = new ConcurrentHashMap<>();

    public static void sendAllToServer(ServerConnection server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(0);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF(AllMusic.getMessage().papi.emptyMusic);
        else {
            out.writeUTF(PlayMusic.nowPlayMusic.getName());
        }
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(1);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAl());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(2);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAlia());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(3);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAuthor());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(4);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getCall());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(5);
        out.writeInt(PlayMusic.getListSize());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(6);
        out.writeUTF(PlayMusic.getAllList());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
    }

    public static void sendLyricToServer(ServerConnection server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(7);
        if (PlayMusic.lyric == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.lyric.getLyric());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(8);
        if (PlayMusic.lyric == null || PlayMusic.lyric.getTlyric() == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.lyric.getTlyric());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(9);
        out.writeBoolean(PlayMusic.lyric != null && PlayMusic.lyric.getTlyric() != null);
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
    }

    @Override
    public void broadcast(Component data) {
        for (Player player : AllMusicVelocity.plugin.server.getAllPlayers()) {
            if (skip(player)) {
                continue;
            }

            player.sendMessage(data);
        }
    }

    @Override
    public boolean needPlay(boolean islist) {
        for (Player player : AllMusicVelocity.plugin.server.getAllPlayers()) {
            String server = null;
            if (player.getCurrentServer().isPresent()) {
                server = player.getCurrentServer().get().getServerInfo().getName();
            }

            if (!AllMusic.isSkip(player.getUsername(), server, false, islist)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<?> getPlayers() {
        return AllMusicVelocity.plugin.server.getAllPlayers();
    }

    @Override
    public String getPlayerName(Object player) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            return player1.getUsername();
        }
        return null;
    }

    @Override
    public String getPlayerServer(Object player) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            Optional<ServerConnection> serverConnection = player1.getCurrentServer();
            if (serverConnection.isPresent()) {
                return serverConnection.get().getServer().getServerInfo().getName();
            }
        }

        return null;
    }

    @Override
    public void send(Object player, MusicPack pack) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            send(player1, MusicPacketCodec.pack(pack));
        }
    }

    @Override
    public Object getPlayer(String player) {
        Optional<Player> player1 = AllMusicVelocity.plugin.server.getPlayer(player);
        return player1.orElse(null);
    }

    @Override
    public void sendBar(Object player, Component data) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            player1.sendActionBar(data);
        }
    }

    @Override
    public File getFolder() {
        return AllMusicVelocity.plugin.dataDirectory.toFile();
    }

    @Override
    public void sendMessage(Object obj, Component message) {
        if (obj instanceof CommandSource) {
            CommandSource source = (CommandSource) obj;
            source.sendMessage(message);
        }
    }

    @Override
    public void runTask(Runnable run) {
        AllMusicVelocity.plugin.server.getScheduler().buildTask(AllMusicVelocity.plugin, run).schedule();
    }

    @Override
    public boolean checkPermission(Object player, String permission) {
        if (checkPermission(player)) {
            return true;
        }
        if (player instanceof PermissionSubject) {
            return ((PermissionSubject) player).hasPermission(permission);
        }
        return false;
    }

    @Override
    public boolean checkPermission(Object player) {
        return player instanceof ConsoleCommandSource;
    }

    @Override
    public boolean isPlayer(Object source) {
        return source instanceof Player;
    }

    @Override
    public void runTask(Runnable run, int delay) {
        AllMusicVelocity.plugin.server.getScheduler().buildTask(AllMusicVelocity.plugin, run)
                .delay(delay, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void ping() {
        for (ServerConnection server : new HashSet<>(TopServers)) {
            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeInt(200);
                server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
            } catch (Exception e) {
                TopServers.remove(server);
            }
        }
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        MusicPlayEvent event = new MusicPlayEvent(obj);
        AllMusicVelocity.plugin.server.getEventManager().fire(event).join();
        return event.isCancel();
    }

    @Override
    public boolean onMusicAdd(Object obj, PlayerAddMusicObj music) {
        MusicAddEvent event = new MusicAddEvent(music, (CommandSource) obj);
        AllMusicVelocity.plugin.server.getEventManager().fire(event).join();
        return event.isCancel();
    }

    @Override
    public void updateInfo() {
        for (ServerConnection server : TopServers) {
            try {
                sendAllToServer(server);
            } catch (Exception e) {
                TopServers.remove(server);
            }
        }
    }

    @Override
    public void updateLyric() {
        for (ServerConnection server : TopServers) {
            try {
                sendLyricToServer(server);
            } catch (Exception e) {
                TopServers.remove(server);
            }
        }
    }

    private void send(Player players, ByteBuf data) {
        if (players == null)
            return;
        runTask(() -> players.sendPluginMessage(AllMusicVelocity.channel, data.array()));
    }

    private boolean skip(Player player) {
        String server = null;
        if (player.getCurrentServer().isPresent()) {
            server = player.getCurrentServer().get().getServerInfo().getName();
        }

        return AllMusic.isSkip(player.getUsername(), server, false);
    }

    @Override
    public boolean check(String name, int cost) {
        return topEconomy(name, cost, 12);
    }

    @Override
    public boolean cost(String name, int cost) {
        return topEconomy(name, cost, 13);
    }

    @Override
    public Component miniMessage(String input) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(input);
    }

    @Override
    public Component miniMessageRun(String input, String command) {
        Component component = miniMessage(input);
        return component.clickEvent(ClickEvent.runCommand(command));
    }

    @Override
    public Component miniMessageSuggest(String input, String command) {
        Component component = miniMessage(input);
        return component.clickEvent(ClickEvent.suggestCommand(command));
    }

    private boolean topEconomy(String name, int cost, int type) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(type);
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (SendToBackend.containsKey(uuid));

        SendToBackend.put(uuid, -1);
        String server = AllMusic.getConfig().economy.backend;
        RegisteredServer toServer = null;
        for (RegisteredServer connection : AllMusicVelocity.plugin.server.getAllServers()) {
            if (connection.getServerInfo().getName().equalsIgnoreCase(server)) {
                toServer = connection;
            }
        }
        if (toServer == null) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>没有找到目标服务器");
            return false;
        }

        out.writeUTF(uuid);
        out.writeInt(cost);
        out.writeUTF(name);

        toServer.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        Integer res;

        int count = 0;

        do {
            try {
                res = SendToBackend.get(uuid);
                if (res == null)
                    return false;
                else if (res == -1) {
                    Thread.sleep(1);
                    count++;
                } else if (res == 0) {
                    AllMusic.log.data("<light_purple>[AllMusic]<red>后端经济插件错误");
                    SendToBackend.remove(uuid);
                    return false;
                } else if (res == 1) {
                    SendToBackend.remove(uuid);
                    return false;
                } else if (res == 2) {
                    SendToBackend.remove(uuid);
                    return true;
                }
            } catch (Exception e) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>经济数据发送错误");
                e.printStackTrace();
            }
        } while (count < 100);

        AllMusic.log.data("<light_purple>[AllMusic]<red>经济数据请求超时");

        return false;
    }
}
