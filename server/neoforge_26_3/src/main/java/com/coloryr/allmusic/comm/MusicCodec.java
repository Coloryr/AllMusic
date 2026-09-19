package com.coloryr.allmusic.comm;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.codec.MusicPacketCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MusicCodec(MusicPack pack) implements CustomPacketPayload {
    public static final Identifier channel =
            Identifier.fromNamespaceAndPath("allmusic", "channel");

    public static final Type<MusicCodec> TYPE = new Type<>(channel);
    public static final StreamCodec<RegistryFriendlyByteBuf, MusicCodec> CODEC = StreamCodec.of((buf, value) -> MusicPacketCodec.pack(buf, value.pack),
            (buf) -> {
                return new MusicCodec(MusicPacketCodec.decode(buf));
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
