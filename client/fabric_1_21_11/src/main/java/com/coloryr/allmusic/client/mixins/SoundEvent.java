package com.coloryr.allmusic.client.mixins;

import com.coloryr.allmusic.client.core.AllMusicCore;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundEvent {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (AllMusicCore.isPlay()) {
            SoundSource data = sound.getSource();
            switch (data) {
                case RECORDS, MUSIC: {
                    cir.cancel();
                    cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
                }
            }
        }
    }

    @Inject(method = "playDelayed", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, int delay, CallbackInfo info) {
        if (AllMusicCore.isPlay()) {
            SoundSource data = sound.getSource();
            switch (data) {
                case RECORDS, MUSIC -> info.cancel();
            }
        }
    }
}
