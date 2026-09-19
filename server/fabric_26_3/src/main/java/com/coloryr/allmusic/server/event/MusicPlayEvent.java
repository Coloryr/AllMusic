package com.coloryr.allmusic.server.event;

import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

public interface MusicPlayEvent {
    Event<MusicPlayEvent> EVENT = EventFactory.createArrayBacked(MusicPlayEvent.class,
            (listeners) -> (music) -> {
                for (MusicPlayEvent listener : listeners) {
                    InteractionResult result = listener.interact(music);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    /**
     * 音乐播放事件，返回PASS表示通过
     *
     * @param music 歌曲信息
     * @return 是否通过
     */
    InteractionResult interact(SongInfoObj music);
}
