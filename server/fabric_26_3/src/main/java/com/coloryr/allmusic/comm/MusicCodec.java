package com.coloryr.allmusic.comm;

import com.coloryr.allmusic.codec.MusicPack;
import com.coloryr.allmusic.codec.MusicPacketCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record MusicCodec(MusicPack pack) implements CustomPacketPayload {
    public static final Identifier ChannelID = Identifier.fromNamespaceAndPath("allmusic", "channel");

    public static final Type<MusicCodec> ID = new Type<>(ChannelID);
    public static final StreamCodec<FriendlyByteBuf, MusicCodec> CODEC =
            StreamCodec.of((buf, value) -> MusicPacketCodec.pack(buf, value.pack),
                    buf -> new MusicCodec(MusicPacketCodec.decode(buf)));

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
