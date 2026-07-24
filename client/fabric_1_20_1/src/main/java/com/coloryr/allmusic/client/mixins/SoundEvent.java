package com.coloryr.allmusic.client.mixins;

import com.coloryr.allmusic.client.core.AllMusicCore;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundEvent {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance soundInstance, CallbackInfo ci) {
        if (AllMusicCore.isPlay()) {
            SoundSource data = soundInstance.getSource();
            switch (data) {
                case RECORDS, MUSIC -> ci.cancel();
            }
        }
    }

    @Inject(method = "playDelayed", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance soundInstance, int delay, CallbackInfo info) {
        if (AllMusicCore.isPlay()) {
            SoundSource data = soundInstance.getSource();
            switch (data) {
                case RECORDS, MUSIC -> info.cancel();
            }
        }
    }
}
