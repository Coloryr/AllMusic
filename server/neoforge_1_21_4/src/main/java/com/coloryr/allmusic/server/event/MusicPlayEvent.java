package com.coloryr.allmusic.server.event;

import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import net.neoforged.bus.api.Event;

public class MusicPlayEvent extends Event {
    private final SongInfoObj music;
    private boolean cancel;

    public MusicPlayEvent(SongInfoObj music) {
        this.music = music;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public SongInfoObj getMusic() {
        return music;
    }
}
