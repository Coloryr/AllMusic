package com.coloryr.allmusic.codec;

/**
 * 打包写入器
 * <p>
 * 打包逻辑不能只依赖 Netty 的 ByteBuf：1.7.x 的 Spigot 服务端把 Netty 4 重打包成了
 * {@code net.minecraft.util.io.netty}，插件的类加载器里没有 {@code io.netty}，
 * 直接用 {@code Unpooled} 会抛 NoClassDefFoundError。
 * 这里把写入抽象出来，纯 Java 和 Netty 各自实现一份。
 */
public interface PacketWriter {
    void writeByte(int value);

    void writeInt(int value);

    void writeLong(long value);

    void writeBytes(byte[] value);
}
