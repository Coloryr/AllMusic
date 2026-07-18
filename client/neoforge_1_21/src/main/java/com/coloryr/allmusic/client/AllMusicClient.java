package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicBridge;
import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.comm.AllMusicInit;
import com.coloryr.allmusic.comm.MusicCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;
import net.neoforged.neoforge.client.event.sound.SoundEngineLoadEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@EventBusSubscriber(modid = AllMusicClient.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AllMusicClient implements IPayloadHandler<MusicCodec>, AllMusicBridge {
    public static final String MODID = "allmusic_client";
    public static final Logger LOGGER = LogManager.getLogger("AllMusic Client");

    public static GuiGraphics context;

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        AllMusicClient client = new AllMusicClient();
        AllMusicInit.handler = client;
        AllMusicCore.init(FMLPaths.CONFIGDIR.get(), client);
        event.enqueueWork(AllMusicCore::renderInit);
    }

    @SubscribeEvent
    public static void onLoad(final SoundEngineLoadEvent e) {
        AllMusicCore.reload();
    }

    public void sendMessage(String data) {
        data = "[AllMusic Client]" + data;
        LOGGER.warn(data);
        String finalData = data;
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().player == null)
                return;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(finalData));
        });
    }

    @Override
    public void handle(MusicCodec pack, IPayloadContext iPayloadContext) {
        try {
            AllMusicCore.packDo(pack.pack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlayMusic() {
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.RECORDS);
    }

    public int getScreenWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public int getScreenHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public int getTextWidth(String item) {
        return Minecraft.getInstance().font.width(item);
    }

    public int getFontHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }

    public float getVolume() {
        return Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS);
    }

    @Override
    public TextFrameBuffer makeTextRender(String name) {
        return new CoreRenderTarget(name);
    }

    @Override
    public TextureRender makeTextureRender(String file) {
        return new TexRender(file);
    }

    @Override
    public PictureFrameBuffer makePictureRender(int size) {
        return new PicRender(size);
    }

    @Override
    public String readText(String file) {
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(MODID, file)).orElseThrow();
            try (InputStream inputStream = resource.open()) {
                byte[] bytes = inputStream.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream readFile(String file) {
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(MODID, file)).orElseThrow();
            return resource.open();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class MusicEvent {
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post event) {
            AllMusicCore.tick();
        }

        @SubscribeEvent
        public static void onSound(final PlaySoundSourceEvent e) {
            if (!AllMusicCore.isPlay()) return;
            SoundSource data = e.getSound().getSource();
            switch (data) {
                case MUSIC, RECORDS -> e.getChannel().stop();
            }
        }

        @SubscribeEvent
        public static void onSound(final PlayStreamingSourceEvent e) {
            if (!AllMusicCore.isPlay()) return;
            SoundSource data = e.getSound().getSource();
            switch (data) {
                case MUSIC, RECORDS -> e.getChannel().stop();
            }
        }

        @SubscribeEvent
        public static void onServerQuit(final ClientPlayerNetworkEvent.LoggingOut e) {
            AllMusicCore.onServerQuit();
        }

        @SubscribeEvent
        public static void onRenderOverlay(RenderGuiLayerEvent.Post e) {
            if (e.getName().equals(VanillaGuiLayers.CAMERA_OVERLAYS)) {
                context= e.getGuiGraphics();
                AllMusicCore.hudUpdate();
            }
        }
    }

    @Override
    public void kick() {
        Minecraft client = Minecraft.getInstance();

        ClientPacketListener packetListener = client.getConnection();
        if (packetListener != null) {
            Connection connection = packetListener.getConnection();
            connection.disconnect(Component.nullToEmpty("Old AllMusic server"));
        }
    }
}
