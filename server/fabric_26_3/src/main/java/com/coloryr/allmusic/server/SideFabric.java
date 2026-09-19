package com.coloryr.allmusic.server;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.comm.MusicCodec;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.event.MusicAddEvent;
import com.coloryr.allmusic.server.event.MusicPlayEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.InteractionResult;

import java.io.File;
import java.util.Collection;

public class SideFabric extends BaseSide {
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
    public boolean checkPermission(Object player, String permission) {
        return checkPermission(player);
    }

    @Override
    public boolean needPlay(boolean islist) {
        for (var player : AllMusicServer.server.getPlayerList().getPlayers()) {
            if (!com.coloryr.allmusic.server.core.AllMusic.isSkip(player.getName().getString(), null, false, islist)) {
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
        return AllMusicServer.server.getPlayerList().getPlayer(player);
    }

    @Override
    public void sendBar(Object player, net.kyori.adventure.text.Component data) {
        if (player instanceof ServerPlayer player1) {
            player1.sendActionBar(data);
        }
    }

    @Override
    public File getFolder() {
        return new File(AllMusic.SERVER_DIR);
    }

    @Override
    public void broadcast(net.kyori.adventure.text.Component message) {
        for (var player : AllMusicServer.server.getPlayerList().getPlayers()) {
            if (!AllMusic.isSkip(player.getName().getString(), null, false)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void sendMessage(Object obj, net.kyori.adventure.text.Component message) {
        if (obj instanceof CommandSourceStack source) {
            source.sendMessage(message);
        }
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        return MusicPlayEvent.EVENT.invoker().interact(obj) != InteractionResult.PASS;
    }

    @Override
    public boolean onMusicAdd(Object obj, PlayerAddMusicObj music) {
        CommandSourceStack source = (CommandSourceStack) obj;
        return MusicAddEvent.EVENT.invoker().interact(source.getPlayer(), music) != InteractionResult.PASS;
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
        runTask(() -> ServerPlayNetworking.send(players, new MusicCodec(data)));
    }
}
