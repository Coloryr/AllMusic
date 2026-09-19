package com.coloryr.allmusic.comm;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

@Mod(AllMusicInit.MODID)
public class AllMusicInit implements IPayloadHandler<MusicCodec> {

    public static final String MODID = "allmusic";

    public static IPayloadHandler<MusicCodec> handler;

    public AllMusicInit(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::register);
    }

    public void register(final RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .optional()
                .playToClient(MusicCodec.TYPE, MusicCodec.CODEC, this);
    }

    @Override
    public void handle(MusicCodec data, IPayloadContext iPayloadContext) {
        if (handler != null) {
            handler.handle(data, iPayloadContext);
        }
    }
}
