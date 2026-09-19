package com.coloryr.allmusic.client.others;

import com.coloryr.allmusic.client.core.AllMusicCore;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;

public class VoiceChat implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "allmusic";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientReceiveSoundEvent.EntitySound.class, this::play);
        registration.registerEvent(ClientReceiveSoundEvent.LocationalSound.class, this::play);
        registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class, this::play);
    }

    public void play(ClientReceiveSoundEvent.EntitySound event) {
        AllMusicCore.chat();
    }

    public void play(ClientReceiveSoundEvent.LocationalSound event) {
        AllMusicCore.chat();
    }

    public void play(ClientReceiveSoundEvent.StaticSound event) {
        AllMusicCore.chat();
    }
}
