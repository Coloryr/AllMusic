package com.coloryr.allmusic.server.event;

import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public interface MusicAddEvent {
    Event<MusicAddEvent> EVENT = EventFactory.createArrayBacked(MusicAddEvent.class,
            (listeners) -> (player, music) -> {
                for (MusicAddEvent listener : listeners) {
                    InteractionResult result = listener.interact(player, music);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(ServerPlayer player, PlayerAddMusicObj music);
}
