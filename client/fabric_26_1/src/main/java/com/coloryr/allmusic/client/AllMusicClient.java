package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicBridge;
import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.comm.MusicCodec;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AllMusicClient implements ClientModInitializer, AllMusicBridge {
    public static final String MODID = "allmusic_client";
    public static final Logger LOGGER = LogManager.getLogger("AllMusic Client");
    public static GuiGraphicsExtractor context;

    public static void update(GuiGraphicsExtractor draw) {
        context = draw;
        AllMusicCore.hudUpdate();
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

    public float getVolume() {
        return Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS);
    }

    @Override
    public void stopPlayMusic() {
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.RECORDS);
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
            var man = Minecraft.getInstance().getResourceManager();
            Resource resource = man.getResource(Identifier.fromNamespaceAndPath(MODID, file)).orElseThrow();
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
            var man = Minecraft.getInstance().getResourceManager();
            Resource resource = man.getResource(Identifier.fromNamespaceAndPath(MODID, file)).orElseThrow();
            return resource.open();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MusicCodec.ID, (pack, handler) -> AllMusicCore.packDo(pack.pack()));

        AllMusicCore.init(FabricLoader.getInstance().getConfigDir(), this);

        ClientLifecycleEvents.CLIENT_STOPPING.register((a) -> AllMusicCore.stop());
    }
}
