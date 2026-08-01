package com.coloryr.allmusic.comm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class AllMusicInit implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(MusicCodec.ID, MusicCodec.CODEC);
    }
}
