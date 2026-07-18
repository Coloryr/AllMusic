package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicBridge;
import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.IResource;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(modid = AllMusicClient.MODID, version = "4.0.0", acceptedMinecraftVersions = "[1.12,)")
@SideOnly(Side.CLIENT)
public class AllMusicClient implements AllMusicBridge {
    public static final String MODID = "allmusic_client";

    public static final Logger LOGGER = LogManager.getLogger("AllMusic Client");

    @SubscribeEvent
    public void onMessage(FMLNetworkEvent.ClientCustomPacketEvent message) {
        try {
            AllMusicCore.packRead(message.getPacket().payload());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void preload(final FMLPreInitializationEvent evt) {
        if (!evt.getModConfigurationDirectory().exists()) {
            evt.getModConfigurationDirectory().mkdirs();
        }
        AllMusicCore.init(evt.getModConfigurationDirectory().toPath(), this);
        MinecraftForge.EVENT_BUS.register(this);
        try {
            Class<?> server = Class.forName("com.coloryr.allmusic.server.AllMusicServer");
            Field m = server.getField("channel");
            FMLEventChannel channel = (FMLEventChannel) m.get(null);
            channel.register(this);
        } catch (Exception e) {
            NetworkRegistry.INSTANCE.newEventDrivenChannel("allmusic:channel")
                    .register(this);
        }
    }

    @SubscribeEvent
    public void onSound(final PlaySoundEvent e) {
        if (!AllMusicCore.isPlay()) return;
        SoundCategory data = e.getSound().getCategory();
        switch (data) {
            case MUSIC:
            case RECORDS:
                e.setResultSound(null);
        }
    }

    @SubscribeEvent
    public void onServerQuit(final FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        AllMusicCore.onServerQuit();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderOverlay(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.PORTAL) {
            AllMusicCore.hudUpdate();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AllMusicCore.tick();
        }
    }

    public int getScreenWidth() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledresolution.getScaledWidth();
    }

    public int getScreenHeight() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        return scaledresolution.getScaledHeight();
    }

    public int getTextWidth(String item) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(item);
    }

    public int getFontHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public float getVolume() {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS);
    }

    @Override
    public void stopPlayMusic() {
        Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
        Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.RECORDS);
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
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(MODID, file));
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = readNBytes(inputStream);
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
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(MODID, file));
            return resource.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void drawText(String item, int x, int y, int color, boolean shadow) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        font.drawString(item, x, y, color, shadow);
    }

    public void sendMessage(String data) {
        data = "[AllMusic Client]" + data;
        LOGGER.warn(data);
        String finalData = data;

        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player != null) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(finalData));
            }
        });
    }

    public byte[] readNBytes(InputStream stream) throws IOException {
        int len = Integer.MAX_VALUE;

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, 16384)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = stream.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (Integer.MAX_VALUE - 8 - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    public void kick() {
        Minecraft client = Minecraft.getMinecraft();

        NetHandlerPlayClient packetListener = client.getConnection();
        if (packetListener != null) {
            NetworkManager connection = packetListener.getNetworkManager();
            connection.closeChannel(new TextComponentString("Old AllMusic server"));
        }
    }
}
