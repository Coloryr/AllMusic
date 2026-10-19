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

import com.coloryr.allmusic.client.core.AllMusicPlayer;
import com.coloryr.allmusic.client.core.player.decoder.BuffPack;
import com.coloryr.allmusic.client.core.player.decoder.IDecoder;

import java.io.EOFException;
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
 * dec.set(60000);
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

    // The maximum number of times the byte position is re-estimated while seeking in a stream
    // that does not contain a seek table.
    private static final int MAX_SEEK_ATTEMPTS = 8;

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

    // Samples that were decoded but not handed out yet. This happens after a seek when the
    // requested sample lies in the middle of a block: [decodedOffset, decodedOffset +
    // decodedLength) of every channel still has to be returned by decodeFrame().
    private int decodedOffset;
    private int decodedLength;

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
        int blockSamples = decodedLength;
        if (blockSamples == 0) {
            blockSamples = readAudioBlock(samples, 0);
            decodedOffset = 0;
        }
        decodedLength = 0;
        int sampleBytesLen = 0;
        for (int i = 0; i < blockSamples; i++) {
            for (int ch = 0; ch < streamInfo.numChannels; ch++) {
                int val = samples[ch][decodedOffset + i];
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
        decodedOffset = 0;
        decodedLength = 0;
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
        decodedOffset = 0;
        decodedLength = 0;

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
        if (input == null || streamInfo == null || frameDec == null) {
            return;
        }
        try {
            long sample = (long) time * streamInfo.sampleRate / 1000L;
            if (sample < 0) sample = 0;
            if (streamInfo.numSamples > 0 && sample >= streamInfo.numSamples) {
                sample = streamInfo.numSamples - 1;
            }
            seekToSample(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*---- Seeking ----*/

    // Seeks to the given sample index (counted from the beginning of the audio stream) and
    // positions the decoder so that the next decoded block starts at that sample. The stream is
    // moved with a seek (a range request), so the data before the target is not read or decoded.
    private void seekToSample(long target) throws IOException {
        decodedOffset = 0;
        decodedLength = 0;

        // A seek table maps sample indices to byte positions, so it allows jumping directly to
        // the closest known frame before the target and decoding only the frames in between.
        SeekTable.SeekPoint point = findSeekPoint(target);
        if (point != null) {
            input.seekTo(metadataEndPos + point.fileOffset);
            skipSamples(target - point.sampleOffset);
            return;
        }

        long length = input.getLength();
        if (streamInfo.numSamples <= 0 || length <= metadataEndPos) {
            // The total length of the stream is unknown, so no byte position can be estimated;
            // fall back to decoding from the beginning of the audio data.
            input.seekTo(metadataEndPos);
            skipSamples(target);
            return;
        }

        // There is no seek table: estimate the byte position from the average bitrate. Scanning
        // forward from a position always finds the frame after it, so the position is moved back
        // by a safety margin to make sure that the frame before the target (or an even earlier
        // one) is found. If the bitrate is not constant the margin is doubled until it is enough.
        long pos = estimatePosition(target, streamInfo.numSamples, metadataEndPos, length);
        long margin = Math.max(65536L, streamInfo.maxFrameSize * 4L);
        for (int i = 0; i < MAX_SEEK_ATTEMPTS; i++) {
            pos = Math.max(metadataEndPos, pos - margin);
            input.seekTo(pos);
            long frameSample = scanToNextFrame();
            if (frameSample < 0) {
                break; // No frame was found, the estimate was too close to the end of the stream
            }
            if (frameSample <= target) {
                skipSamples(target - frameSample);
                return;
            }
            // The frame found is still after the target: back off further and try again.
            margin = Math.min(margin * 2, Math.max(1L, pos - metadataEndPos));
        }

        // Decoding from the beginning is always correct, it is only used as a last resort.
        input.seekTo(metadataEndPos);
        skipSamples(target);
    }

    // Returns the seek point with the greatest sample offset that is not after the given sample,
    // or null if the stream does not contain a usable seek table.
    private SeekTable.SeekPoint findSeekPoint(long sample) {
        if (seekTable == null || seekTable.points == null) return null;
        SeekTable.SeekPoint result = null;
        for (SeekTable.SeekPoint point : seekTable.points) {
            if (point == null || point.sampleOffset < 0) continue; // Placeholder point
            if (point.sampleOffset > sample) continue;
            if (result == null || point.sampleOffset > result.sampleOffset) result = point;
        }
        return result;
    }

    // Returns the byte position that corresponds to the given sample index, assuming that the
    // samples are spread evenly over the byte range [start, end). Pure function.
    private static long estimatePosition(long sample, long totalSamples, long start, long end) {
        if (sample <= 0 || totalSamples <= 0 || end <= start) return start;
        long pos = start + (long) ((double) sample / totalSamples * (end - start));
        if (pos < start) return start;
        if (pos >= end) return end - 1;
        return pos;
    }

    // Scans forward from the current byte position for the next valid FLAC frame header, leaves
    // the stream at the beginning of that frame, and returns its sample index, or -1 if the end
    // of the stream was reached. This is used to seek in streams without a seek table.
    private long scanToNextFrame() throws IOException {
        long pos = input.getPosition();
        int previous = -1;
        while (true) {
            int current;
            try {
                current = input.readByte();
            } catch (EOFException e) {
                return -1;
            }
            if (current == -1) return -1;

            // The sync code is 14 one-bits followed by a zero reserved bit, so the first two
            // bytes of a frame are 0xFF and 0xF8 or 0xF9.
            if (previous == 0xFF && (current & 0xFE) == 0xF8) {
                long start = pos - 1;
                if (!input.seekWithinBuffer(start)) input.seekTo(start);

                long index = -1;
                try {
                    FrameInfo info = FrameInfo.readFrame(input);
                    if (info == null) return -1; // Clean end of stream
                    index = getSampleIndex(info);
                } catch (DataFormatException e) {
                    // A false sync code inside a frame; keep searching
                } catch (EOFException e) {
                    return -1;
                }

                if (index >= 0) {
                    // Rewind so that the frame can be decoded from its very first byte
                    if (!input.seekWithinBuffer(start)) input.seekTo(start);
                    return index;
                }

                // Continue scanning right after the sync code that turned out to be invalid
                if (!input.seekWithinBuffer(start + 1)) input.seekTo(start + 1);
                pos = start + 1;
                previous = -1;
                continue;
            }

            previous = current;
            pos++;
        }
    }

    // Returns the absolute sample index of the given frame header, or -1 if the header does not
    // carry one (which the FLAC format guarantees, so this should not happen).
    private long getSampleIndex(FrameInfo info) {
        if (info.sampleOffset >= 0) return info.sampleOffset;
        if (info.frameIndex >= 0) return (long) info.frameIndex * streamInfo.maxBlockSize;
        return -1;
    }

    // Decodes and discards audio blocks until the given number of samples has been skipped. The
    // block that contains the target is kept for the following decodeFrame() calls, so playback
    // continues exactly at the requested sample.
    private void skipSamples(long skip) throws IOException {
        decodedOffset = 0;
        decodedLength = 0;
        while (skip > 0) {
            int blockSamples = readAudioBlock(samples, 0);
            if (blockSamples == 0) return; // End of stream
            if (skip >= blockSamples) {
                skip -= blockSamples;
            } else {
                decodedOffset = (int) skip;
                decodedLength = blockSamples - (int) skip;
                return;
            }
        }
    }
}
