package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(modid = "allmusic_server", version = AllMusic.version, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.7,)")
public class AllMusicServer {
    public static final Logger LOGGER = LogManager.getLogger("AllMusic Server");
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.builder()
            .downsampleColors()
            .build();
    public static MinecraftServer server;
    public static FMLEventChannel channel;

    public static IChatComponent parse(Component input) {
        String json = GSON_SERIALIZER.serialize(input);
        return IChatComponent.Serializer.func_150699_a(json);
    }

    @Mod.EventHandler
    private void commonSetup(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("allmusic:channel");

        AllMusic.log = new LogForge();
        AllMusic.side = new SideForge();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        AllMusic.start();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        server = event.getServer();

        new AllMusic().init(new File(AllMusic.SERVER_DIR));

        ServerCommandManager commandManager = (ServerCommandManager) server.getCommandManager();
        commandManager.registerCommand(new CommandForge());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        AllMusic.stop();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayMusic.removeNowPlayPlayer(event.player.getCommandSenderName());
    }

    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Tasks.tick();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        AllMusic.joinPlay(event.player.getCommandSenderName());
    }
}
