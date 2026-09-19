package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.AllMusic;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class AllMusicServer implements ModInitializer {
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        AllMusic.log = new LogFabric();
        AllMusic.side = new SideFabric();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)
                -> CommandFabric.instance.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register((a) -> {
            server = a;
            AllMusic.init(new File(AllMusic.SERVER_DIR));
            AllMusic.start();
            Tasks.init();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((a) -> {
            AllMusic.stop();
        });
    }
}
