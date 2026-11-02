package com.coloryr.allmusic.server;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.codec.MusicPacketCodec;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.event.MusicAddEvent;
import com.coloryr.allmusic.server.event.MusicPlayEvent;
import com.coloryr.allmusic.server.hooks.CitizensNPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;

import java.io.File;
import java.util.Collection;

public class SideBukkit extends BaseSide {
    @Override
    public void broadcast(Component data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!AllMusic.isSkip(player.getName(), null, false)) {
                AllMusicBukkit.adventure.player(player).sendMessage(data);
            }
        }
    }

    @Override
    public boolean needPlay(boolean islist) {
        int online = Bukkit.getOnlinePlayers().size();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (CitizensNPC.isNPC(player))
                online--;
            else if (AllMusic.isSkip(player.getName(), null, false, islist)) {
                online--;
            }
        }
        return online > 0;
    }

    @Override
    public Collection<?> getPlayers() {
        return Bukkit.getOnlinePlayers();
    }

    @Override
    public String getPlayerName(Object player) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            return player1.getName();
        }

        return null;
    }

    @Override
    public String getPlayerServer(Object player) {
        return null;
    }

    @Override
    public void send(Object player, MusicPack pack) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            try {
                send(player1, MusicPacketCodec.packBytes(pack));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getPlayer(String player) {
        return Bukkit.getPlayer(player);
    }

    @Override
    public void sendBar(Object player, Component message) {
        if (player instanceof Player) {
            Player player1 = (Player) player;
            AllMusicBukkit.adventure.player(player1).sendActionBar(message);
        }
    }

    @Override
    public File getFolder() {
        return AllMusicBukkit.plugin.getDataFolder();
    }

    @Override
    public void sendMessage(Object obj, Component message) {
        if (obj instanceof CommandSender) {
            CommandSender sender = (CommandSender) obj;
            AllMusicBukkit.adventure.sender(sender).sendMessage(message);
        }
    }

    @Override
    public void runTask(Runnable run) {
        Bukkit.getScheduler().runTask(AllMusicBukkit.plugin, run);
    }

    @Override
    public boolean checkPermission(Object player, String permission) {
        if (checkPermission(player)) {
            return true;
        }
        if (player instanceof Permissible) {
            Permissible player1 = (Permissible) player;
            return player1.hasPermission(permission);
        }

        return false;
    }

    @Override
    public boolean checkPermission(Object player) {
        if (player instanceof ConsoleCommandSender) {
            return true;
        }
        if (player instanceof ServerOperator) {
            ServerOperator sender = (ServerOperator) player;
            return sender.isOp();
        }

        return false;
    }

    @Override
    public boolean isPlayer(Object source) {
        return source instanceof Player;
    }

    @Override
    public void runTask(Runnable run, int delay) {
        Bukkit.getScheduler().runTaskLater(AllMusicBukkit.plugin, run, delay);
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        MusicPlayEvent event = new MusicPlayEvent(obj);
        Bukkit.getScheduler().callSyncMethod(AllMusicBukkit.plugin, () -> {
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancel()) {
                FunCore.addMusic();
            }
            return event;
        });
        return event.isCancel();
    }

    @Override
    public boolean onMusicAdd(Object obj, PlayerAddMusicObj music) {
        MusicAddEvent event = new MusicAddEvent(music, (CommandSender) obj);
        Bukkit.getScheduler().callSyncMethod(AllMusicBukkit.plugin, () -> {
            Bukkit.getPluginManager().callEvent(event);
            return event;
        });
        return event.isCancel();
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

    private void send(Player players, byte[] data) {
        if (players == null)
            return;

        if (AllMusic.isRun) {
            runTask(() -> players.sendPluginMessage(AllMusicBukkit.plugin, AllMusic.channel, data));
        }
    }
}
