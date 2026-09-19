package com.coloryr.allmusic.server;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.comm.MusicCodec;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.event.MusicAddEvent;
import com.coloryr.allmusic.server.event.MusicPlayEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.File;
import java.util.Collection;

public class SideNeoForge extends BaseSide {
    @Override
    public void runTask(Runnable run) {
        AllMusicServer.server.execute(run);
    }

    @Override
    public void runTask(Runnable run1, int delay) {
        Tasks.add(new TaskItem() {{
            tick = delay;
            run = run1;
        }});
    }

    @Override
    public boolean checkPermission(Object player, String permission) {
        return checkPermission(player);
    }

    @Override
    public boolean checkPermission(Object player) {
        CommandSourceStack source = (CommandSourceStack) player;
        return source.permissions() == PermissionSet.ALL_PERMISSIONS;
    }

    @Override
    public boolean isPlayer(Object player) {
        CommandSourceStack source = (CommandSourceStack) player;
        return source.isPlayer();
    }

    @Override
    public boolean needPlay(boolean islist) {
        for (ServerPlayer player : AllMusicServer.server.getPlayerList().getPlayers()) {
            if (!AllMusic.isSkip(player.getName().getString(), null, false, islist)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<?> getPlayers() {
        return AllMusicServer.server.getPlayerList().getPlayers();
    }

    @Override
    public String getPlayerName(Object player) {
        if (player instanceof ServerPlayer player1) {
            return player1.getName().getString();
        }

        return null;
    }

    @Override
    public String getPlayerServer(Object player) {
        return null;
    }

    @Override
    public void send(Object player, MusicPack pack) {
        if (player instanceof ServerPlayer player1) {
            send(player1, pack);
        }
    }

    @Override
    public Object getPlayer(String player) {
        return AllMusicServer.server.getPlayerList().getPlayerByName(player);
    }

    @Override
    public void sendBar(Object player, Component data) {
        if (player instanceof ServerPlayer player1) {
            AllMusicServer.audiences.audience(player1).sendActionBar(data);
        }
    }

    @Override
    public File getFolder() {
        return new File(AllMusic.SERVER_DIR);
    }

    @Override
    public void broadcast(Component message) {
        for (ServerPlayer player : AllMusicServer.server.getPlayerList().getPlayers()) {
            if (!AllMusic.isSkip(player.getName().getString(), null, false)) {
                AllMusicServer.audiences.audience(player).sendActionBar(message);
            }
        }
    }

    @Override
    public void sendMessage(Object obj, Component message) {
        if (obj instanceof CommandSourceStack source) {
            AllMusicServer.audiences.audience(source).sendActionBar(message);
        }
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        MusicPlayEvent event = new MusicPlayEvent(obj);
        NeoForge.EVENT_BUS.post(event);
        return event.isCancel();
    }

    @Override
    public boolean onMusicAdd(Object obj, PlayerAddMusicObj music) {
        CommandSourceStack source = (CommandSourceStack) obj;
        MusicAddEvent event = new MusicAddEvent(music, source.getPlayer());
        NeoForge.EVENT_BUS.post(event);
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

    private void send(ServerPlayer players, MusicPack data) {
        if (players == null)
            return;
        runTask(() -> PacketDistributor.sendToPlayer(players, new MusicCodec(data)));
    }
}
