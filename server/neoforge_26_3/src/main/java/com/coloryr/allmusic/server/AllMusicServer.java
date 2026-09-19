package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@EventBusSubscriber(modid = "allmusic_server", value = Dist.DEDICATED_SERVER)
public class AllMusicServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AllMusic Server");
    public static MinecraftServer server;
    public static MinecraftServerAudiences audiences;

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        AllMusic.log = new LogNeoForge();
        AllMusic.side = new SideNeoForge();
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (event.getCommandSelection() != Commands.CommandSelection.INTEGRATED) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            CommandNeoForge.instance.register(dispatcher);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()) {
            audiences = MinecraftServerAudiences.of(event.getServer());
            server = event.getServer();
            AllMusic.init(new File(AllMusic.SERVER_DIR));
            AllMusic.start();
            Tasks.init();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        if (event.getServer().isDedicatedServer()) {
            AllMusic.stop();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (server != null) {
            PlayMusic.removeNowPlayPlayer(event.getEntity().getName().getString());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (server != null) {
            AllMusic.joinPlay(event.getEntity().getName().getString());
        }
    }
}
