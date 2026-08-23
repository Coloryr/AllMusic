package com.coloryr.allmusic.server.event;

import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

/**
 * 音乐添加事件
 */
public class MusicAddEvent extends Event {
    /**
     * 添加的音乐
     */
    private final PlayerAddMusicObj music;
    /**
     * 添加者
     */
    private final CommandSender player;
    /**
     * 是否取消添加
     */
    private boolean cancel = false;

    public MusicAddEvent(PlayerAddMusicObj id, CommandSender player) {
        this.music = id;
        this.player = player;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public CommandSender getPlayer() {
        return player;
    }

    public PlayerAddMusicObj getMusic() {
        return music;
    }
}
