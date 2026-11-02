package com.coloryr.allmusic.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MusicPacketCodec {
    public static final CommandType[] types = CommandType.values();

    /**
     * 打包成 ByteBuf
     * <p>
     * 只有自带 Netty 4 的环境（客户端、Forge/Fabric 服务端）会用到
     */
    public static ByteBuf pack(MusicPack pack) {
        ByteBuf buf = Unpooled.buffer(0);
        write(new NettyPacketWriter(buf), pack);
        return buf;
    }

    /**
     * 打包成精确长度的字节数组
     * <p>
     * {@link #pack(MusicPack)} 返回的 ByteBuf 容量是 Netty 扩容后的值（最小 64，之后按 2 倍增长），
     * 通常大于实际写入的长度。直接发送 {@code array()} 会带上大量 0 字节；
     * 1.7.x 客户端的自定义数据包长度是一个 VarShort，代理会按整个数组长度来写，
     * 数据被撑大后可能超过 32767 的上限，导致客户端收不到数据包。
     * 因此这里只复制真正写入的字节。
     * <p>
     * 另外这里用纯 Java 的 {@link ByteArrayPacketWriter} 写入，不经过 Netty：
     * 1.7.x 的 Spigot 服务端把 Netty 4 重打包成了 {@code net.minecraft.util.io.netty}，
     * 插件的类加载器里没有 {@code io.netty}，之前调用 {@code Unpooled} 会抛
     * NoClassDefFoundError，插件消息就发不出去。
     *
     * @param pack 数据
     * @return 精确长度的数据
     */
    public static byte[] packBytes(MusicPack pack) {
        ByteArrayPacketWriter writer = new ByteArrayPacketWriter();
        write(writer, pack);
        return writer.toByteArray();
    }

    /**
     * 把数据写进已有的 ByteBuf（客户端编码器用）
     *
     * @param buf  ByteBuf
     * @param pack 数据
     */
    public static void pack(ByteBuf buf, MusicPack pack) {
        write(new NettyPacketWriter(buf), pack);
    }

    private static void write(PacketWriter buf, MusicPack pack) {
        buf.writeByte(pack.type.ordinal());
        switch (pack.type) {
            case HUD_DATA:
            case INFO:
            case IMG:
            case PLAY:
                MusicPack.StringMusicPack pack1 = (MusicPack.StringMusicPack) pack;
                writeString(buf, pack1.data);
                break;
            case LYRIC:
                MusicPack.LyricMusicPack pack2 = (MusicPack.LyricMusicPack) pack;
                writeString(buf, pack2.lyric);
                writeString(buf, pack2.tlyric);
                writeString(buf, pack2.klyric);
                break;
            case LYRIC_KTV:
                MusicPack.LyricKtvMusicPack pack3 = (MusicPack.LyricKtvMusicPack) pack;
                if (pack3.data == null) {
                    buf.writeLong(-1);
                } else {
                    buf.writeLong(pack3.time);
                    buf.writeLong(pack3.data.start);
                    buf.writeLong(pack3.data.time);
                    buf.writeInt(pack3.data.charCount);
                    buf.writeInt(pack3.data.items.size());
                    for (KtvLyricObj.KtvItem item : pack3.data.items) {
                        buf.writeLong(item.start);
                        buf.writeLong(item.time);
                        writeString(buf, item.key);
                    }
                }
                break;
            case POS:
                MusicPack.IntMusicPack pack4 = (MusicPack.IntMusicPack) pack;
                buf.writeInt(pack4.data);
                break;
            case TIME:
                MusicPack.TimeMusicPack pack5 = (MusicPack.TimeMusicPack) pack;
                buf.writeLong(pack5.time);
                buf.writeLong(pack5.now);
                break;
        }
    }

    private static void writeString(PacketWriter buf, String text) {
        byte[] temp = text.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(temp.length);
        buf.writeBytes(temp);
    }

    private static String readString(ByteBuf buf) {
        int size = buf.readInt();
        byte[] temp = new byte[size];
        buf.readBytes(temp);

        return new String(temp, StandardCharsets.UTF_8);
    }

    public static MusicPack decode(ByteBuf buf) {
        byte type = buf.readByte();
        if (type >= types.length || type < 0) {
            buf.clear();
            return new MusicPack(CommandType.STOP);
        }
        CommandType type1 = types[type];
        MusicPack pack;
        switch (type1) {
            case INFO:
            case PLAY:
            case IMG:
            case HUD_DATA:
                pack = new MusicPack.StringMusicPack(type1, readString(buf));
                break;
            case LYRIC:
                pack = new MusicPack.LyricMusicPack(readString(buf), readString(buf), readString(buf));
                break;
            case LYRIC_KTV:
                KtvLyricObj ktv = null;
                long state = buf.readLong();
                if (state != -1) {
                    ktv = new KtvLyricObj();
                    ktv.start = buf.readLong();
                    ktv.time = buf.readLong();
                    ktv.charCount = buf.readInt();
                    ktv.items = new ArrayList<>();
                    int size = buf.readInt();
                    for (int i = 0; i < size; i++) {
                        KtvLyricObj.KtvItem item = new KtvLyricObj.KtvItem();
                        item.start = buf.readLong();
                        item.time = buf.readLong();
                        item.key = readString(buf);
                        ktv.items.add(item);
                    }
                }
                pack = new MusicPack.LyricKtvMusicPack(state, ktv);
                break;
            case POS:
                pack = new MusicPack.IntMusicPack(type1, buf.readInt());
                break;
            case TIME:
                pack = new MusicPack.TimeMusicPack(buf.readLong(), buf.readLong());
                break;
            default:
                pack = new MusicPack(type1);
        }
        buf.clear();

        return pack;
    }
}
