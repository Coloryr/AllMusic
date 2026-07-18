package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicBridge;
import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AllMusicClient implements ClientModInitializer, AllMusicBridge {
    public static final String MODID = "allmusic_client";

    public static final ResourceLocation ID = new ResourceLocation("allmusic", "channel");
    public static final Logger LOGGER = LogManager.getLogger("AllMusic Client");
    public static GuiGraphics context;

    public static void update(GuiGraphics context) {
        AllMusicClient.context = context;
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
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(MODID, file)).orElseThrow();
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
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(MODID, file)).orElseThrow();
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
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buffer, responseSender) -> {
            try {
                AllMusicCore.packRead(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AllMusicCore.init(FabricLoader.getInstance().getConfigDir(), this);
        RenderSystem.recordRenderCall(AllMusicCore::renderInit);

        ClientLifecycleEvents.CLIENT_STOPPING.register((a) -> AllMusicCore.stop());
    }
}
