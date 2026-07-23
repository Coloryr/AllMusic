/*
 * FLAC library (Java)
 * Copyright (c) Project Nayuki
 * https://www.nayuki.io/page/flac-library-java
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.coloryr.allmusic.client.core.player.decoder.flac;

import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.AllMusicPlayer;
import com.coloryr.allmusic.client.core.player.decoder.BuffPack;
import com.coloryr.allmusic.client.core.player.decoder.IDecoder;

import java.io.IOException;

/**
 * Handles high-level decoding and seeking in FLAC files. Also returns metadata blocks.
 * Every object is stateful, not thread-safe, and needs to be closed. Sample usage:
 *
 * <pre>
 * // Create a decoder
 * FlacDecoder dec = new FlacDecoder(...);
 *
 * &#x2F;/ Make the decoder process all metadata blocks internally.
 * &#x2F;/ We could capture the returned data for extra processing.
 * &#x2F;/ We must read all metadata before reading audio data.
 * while (dec.readAndHandleMetadataBlock() != null);
 *
 * &#x2F;/ Read audio samples starting from beginning
 * int[][] samples = (...);
 * dec.readAudioBlock(samples, ...);
 * dec.readAudioBlock(samples, ...);
 * dec.readAudioBlock(samples, ...);
 *
 * &#x2F;/ Seek to some position and continue reading
 * dec.seekAndReadAudioBlock(..., samples, ...);
 * dec.readAudioBlock(samples, ...);
 * dec.readAudioBlock(samples, ...);
 *
 * &#x2F;/ Close underlying file stream
 * dec.close();
 * </pre>
 *
 * @see FrameDecoder
 * @see FlacLowLevelInput
 */
public final class FlacDecoder implements AutoCloseable, IDecoder {

    private final AllMusicPlayer player;
    /*---- Fields ----*/
    private final BuffPack pack = new BuffPack();
    public StreamInfo streamInfo;
    public SeekTable seekTable;
    private FlacLowLevelInput input;
    private long metadataEndPos;

    /*---- Constructors ----*/
    private FrameDecoder frameDec;

    /*---- Methods ----*/
    private int[][] samples;
    private byte[] sampleBytes;

    // Constructs a new FLAC decoder to read the given file.
    // This immediately reads the basic header but not metadata blocks.
    public FlacDecoder(AllMusicPlayer player) {
        // Initialize streams
        this.player = player;
    }

    // Reads, handles, and returns the next metadata block. Returns a pair (Integer type, byte[] data) if the
    // next metadata block exists, otherwise returns null if the final metadata block was previously read.
    // In addition to reading and returning data, this method also updates the internal state
    // of this object to reflect the new data seen, and throws exceptions for situations such as
    // not starting with a stream info metadata block or encountering duplicates of certain blocks.
    public Object[] readAndHandleMetadataBlock() throws IOException {
        if (metadataEndPos != -1) return null; // All metadata already consumed

        // Read entire block
        boolean last = input.readUint(1) != 0;
        int type = input.readUint(7);
        int length = input.readUint(24);
        byte[] data = new byte[length];
        input.readFully(data);

        // Handle recognized block
        if (type == 0) {
            if (streamInfo != null) throw new DataFormatException("Duplicate stream info metadata block");
            streamInfo = new StreamInfo(data);
        } else {
            if (streamInfo == null) throw new DataFormatException("Expected stream info metadata block");
            if (type == 3) {
                if (seekTable != null) throw new DataFormatException("Duplicate seek table metadata block");
                seekTable = new SeekTable(data);
            }
        }

        if (last) {
            metadataEndPos = input.getPosition();
            frameDec = new FrameDecoder(input, streamInfo.sampleDepth);
        }
        return new Object[]{type, data};
    }

    // Reads and decodes the next block of audio samples into the given buffer,
    // returning the number of samples in the block. The return value is 0 if the read
    // started at the end of stream, or a number in the range [1, 65536] for a valid block.
    // All metadata blocks must be read before starting to read audio blocks.
    public int readAudioBlock(int[][] samples, int off) throws IOException {
        if (frameDec == null) throw new IllegalStateException("Metadata blocks not fully consumed yet");

        FrameInfo frame = frameDec.readFrame(samples, off);
        if (frame == null) return 0;
        else {
            return frame.blockSize; // In the range [1, 65536]
        }
    }

    @Override
    public BuffPack decodeFrame() throws Exception {
        int blockSamples = readAudioBlock(samples, 0);
        int sampleBytesLen = 0;
        for (int i = 0; i < blockSamples; i++) {
            for (int ch = 0; ch < streamInfo.numChannels; ch++) {
                int val = samples[ch][i];
                if (streamInfo.sampleDepth == 24) {
                    float temp = val / 16777216f;
                    val = (int) (temp * 0x7FFF);
                } else if (streamInfo.sampleDepth == 32) {
                    float temp = val / 1099511627776f;
                    val = (int) (temp * 0x7FFF);
                }
                for (int j = 0; j < 2; j++, sampleBytesLen++) sampleBytes[sampleBytesLen] = (byte) (val >>> (j << 3));
            }
        }
        pack.len = sampleBytesLen;
        pack.buff = sampleBytes;
        return pack;
    }

    // Closes the underlying input streams and discards object data.
    // This decoder object becomes invalid for any method calls or field usages.
    public void close() throws IOException {
        if (input != null) {
            streamInfo = null;
            seekTable = null;
            frameDec = null;
            input.close();
            input = null;
        }
    }

    @Override
    public boolean set() throws Exception {
        input = new SeekableFileFlacInput(player);

        // Read basic header
        if (input.readUint(32) != 0x664C6143) // Magic string "fLaC"
        {
            return false;
        }
        metadataEndPos = -1;

        while (readAndHandleMetadataBlock() != null) {

        }
        int bytesPerSample = streamInfo.sampleDepth / 8;
        samples = new int[streamInfo.numChannels][65536];
        sampleBytes = new byte[65536 * streamInfo.numChannels * bytesPerSample];

        return true;
    }

    @Override
    public int getOutputFrequency() {
        return streamInfo.sampleRate;
    }

    @Override
    public int getOutputChannels() {
        return streamInfo.numChannels;
    }

    @Override
    public void set(int time) {
        AllMusicCore.bridge.sendMessage("不支持中间播放");
    }
}
