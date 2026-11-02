package com.coloryr.allmusic.codec;

import java.io.ByteArrayOutputStream;

/**
 * 纯 Java 的写入器，不依赖 Netty
 */
public class ByteArrayPacketWriter implements PacketWriter {
    private final ByteArrayOutputStream stream;

    public ByteArrayPacketWriter() {
        this(256);
    }

    public ByteArrayPacketWriter(int size) {
        stream = new ByteArrayOutputStream(size);
    }

    @Override
    public void writeByte(int value) {
        stream.write(value);
    }

    @Override
    public void writeInt(int value) {
        stream.write(value >>> 24);
        stream.write(value >>> 16);
        stream.write(value >>> 8);
        stream.write(value);
    }

    @Override
    public void writeLong(long value) {
        writeInt((int) (value >>> 32));
        writeInt((int) value);
    }

    @Override
    public void writeBytes(byte[] value) {
        stream.write(value, 0, value.length);
    }

    public byte[] toByteArray() {
        return stream.toByteArray();
    }
}
