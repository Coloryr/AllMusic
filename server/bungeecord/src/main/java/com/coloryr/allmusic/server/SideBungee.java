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
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class SideBungee extends BaseSide implements IEconomy {
    public static final Set<Server> TopServers = new CopyOnWriteArraySet<>();

    public static final Map<String, Integer> SendToBackend = new ConcurrentHashMap<>();

    public static void sendAllToServer(Server server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(0);
        if (PlayMusic.nowPlayMusic == null) {
            out.writeUTF(AllMusic.getMessage().papi.emptyMusic);
        } else {
            out.writeUTF(PlayMusic.nowPlayMusic.getName());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(1);
        if (PlayMusic.nowPlayMusic == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.nowPlayMusic.getAl());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(2);
        if (PlayMusic.nowPlayMusic == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.nowPlayMusic.getAlia());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(3);
        if (PlayMusic.nowPlayMusic == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.nowPlayMusic.getAuthor());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(4);
        if (PlayMusic.nowPlayMusic == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.nowPlayMusic.getCall());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(5);
        out.writeInt(PlayMusic.getListSize());
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(6);
        out.writeUTF(PlayMusic.getAllList());
        server.sendData(AllMusic.channelBC, out.toByteArray());
    }

    public static void sendLyricToServer(Server server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(7);
        if (PlayMusic.lyric == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.lyric.getLyric());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(8);
        if (PlayMusic.lyric == null || PlayMusic.lyric.getTlyric() == null) {
            out.writeUTF("");
        } else {
            out.writeUTF(PlayMusic.lyric.getTlyric());
        }
        server.sendData(AllMusic.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(9);
        out.writeBoolean(PlayMusic.lyric != null && PlayMusic.lyric.getTlyric() != null);
        server.sendData(AllMusic.channelBC, out.toByteArray());
    }

    @Override
    public void broadcast(Component data) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (skip(player)) {
                continue;
            }

            AllMusicBungee.adventure.player(player).sendMessage(data);
        }
    }

    @Override
    public boolean needPlay(boolean islist) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            String server = player.getServer() == null ? null : player.getServer().getInfo().getName();
            if (!AllMusic.isSkip(player.getName(), server, false, islist)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<?> getPlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    @Override
    public String getPlayerName(Object player) {
        if (player instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) player).getName();
        }
        return null;
    }

    @Override
    public String getPlayerServer(Object player) {
        if (player instanceof ProxiedPlayer) {
            Server server = ((ProxiedPlayer) player).getServer();
            if (server != null) {
                return server.getInfo().getName();
            }
        }

        return null;
    }

    @Override
    public void send(Object player, MusicPack pack) {
        if (player instanceof ProxiedPlayer) {
            send((ProxiedPlayer) player, MusicPacketCodec.pack(pack));
        }
    }

    @Override
    public Object getPlayer(String player) {
        return ProxyServer.getInstance().getPlayer(player);
    }

    @Override
    public void sendBar(Object player, Component data) {
        if (player instanceof ProxiedPlayer) {
            AllMusicBungee.adventure.player((ProxiedPlayer) player).sendActionBar(data);
        }
    }

    @Override
    public File getFolder() {
        return AllMusicBungee.plugin.getDataFolder();
    }

    @Override
    public void sendMessage(Object obj, Component message) {
        if (obj instanceof CommandSender) {
            AllMusicBungee.adventure.sender((CommandSender) obj).sendMessage(message);
        }
    }

    @Override
    public void runTask(Runnable run) {
        ProxyServer.getInstance().getScheduler().runAsync(AllMusicBungee.plugin, run);
    }

    @Override
    public boolean checkPermission(Object player, String permission) {
        if (checkPermission(player)) {
            return true;
        }
        if (player instanceof CommandSender) {
            return ((CommandSender) player).hasPermission(permission);
        }
        return false;
    }

    @Override
    public boolean checkPermission(Object player) {
        return player == ProxyServer.getInstance().getConsole();
    }

    @Override
    public boolean isPlayer(Object source) {
        return source instanceof ProxiedPlayer;
    }

    @Override
    public void runTask(Runnable run, int delay) {
        ProxyServer.getInstance().getScheduler()
                .schedule(AllMusicBungee.plugin, run, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void ping() {
        for (Server server : new HashSet<>(TopServers)) {
            if (server.isConnected()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeInt(200);
                server.sendData(AllMusic.channelBC, out.toByteArray());
            } else {
                TopServers.remove(server);
            }
        }
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        MusicPlayEvent event = new MusicPlayEvent(obj);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event.isCancel();
    }

    @Override
    public boolean onMusicAdd(Object obj, PlayerAddMusicObj music) {
        MusicAddEvent event = new MusicAddEvent(music, (CommandSender) obj);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event.isCancel();
    }

    @Override
    public void updateInfo() {
        for (Server server : TopServers) {
            if (server.isConnected()) {
                sendAllToServer(server);
            } else {
                TopServers.remove(server);
            }
        }
    }

    @Override
    public void updateLyric() {
        for (Server server : TopServers) {
            if (server.isConnected()) {
                sendLyricToServer(server);
            } else {
                TopServers.remove(server);
            }
        }
    }

    private void send(ProxiedPlayer player, ByteBuf data) {
        if (player == null) {
            return;
        }
        runTask(() -> player.sendData(AllMusic.channel, data.array()));
    }

    private boolean skip(ProxiedPlayer player) {
        String server = player.getServer() == null ? null : player.getServer().getInfo().getName();
        return AllMusic.isSkip(player.getName(), server, false);
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
        return MiniMessage.miniMessage().deserialize(input);
    }

    @Override
    public Component miniMessageRun(String input, String command) {
        return miniMessage(input).clickEvent(ClickEvent.runCommand(command));
    }

    @Override
    public Component miniMessageSuggest(String input, String command) {
        return miniMessage(input).clickEvent(ClickEvent.suggestCommand(command));
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
        Server toServer = null;
        for (Server connection : TopServers) {
            if (connection.isConnected() && connection.getInfo().getName().equalsIgnoreCase(server)) {
                toServer = connection;
                break;
            }
        }
        if (toServer == null) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>没有找到目标服务器");
            return false;
        }

        out.writeUTF(uuid);
        out.writeInt(cost);
        out.writeUTF(name);

        toServer.sendData(AllMusic.channelBC, out.toByteArray());

        Integer res;
        int count = 0;
        do {
            try {
                res = SendToBackend.get(uuid);
                if (res == null) {
                    return false;
                } else if (res == -1) {
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
