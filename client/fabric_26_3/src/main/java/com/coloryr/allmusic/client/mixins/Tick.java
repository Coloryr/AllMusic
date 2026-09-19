package com.coloryr.allmusic.client.mixins;

import com.coloryr.allmusic.client.core.AllMusicCore;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class Tick {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        AllMusicCore.tick();
    }
}
