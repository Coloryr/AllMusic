package com.coloryr.allmusic.codec;

import io.netty.buffer.ByteBuf;

/**
 * Netty 的写入器
 * <p>
 * 只有自带 Netty 4 的环境（客户端、Forge/Fabric 服务端）才会加载这个类
 */
public class NettyPacketWriter implements PacketWriter {
    private final ByteBuf buf;

    public NettyPacketWriter(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeByte(int value) {
        buf.writeByte(value);
    }

    @Override
    public void writeInt(int value) {
        buf.writeInt(value);
    }

    @Override
    public void writeLong(long value) {
        buf.writeLong(value);
    }

    @Override
    public void writeBytes(byte[] value) {
        buf.writeBytes(value);
    }
}
