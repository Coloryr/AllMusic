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

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * A FLAC input stream backed by the stream of an {@link AllMusicPlayer}.
 * <p>
 * The player streams the music over HTTP and can re-open its stream at any byte position
 * (by issuing a range request), which is what {@link #seekTo(long)} uses. Whenever the
 * stream is re-opened, all the data that was buffered for the previous position is dropped.
 * </p>
 */
public final class SeekableFileFlacInput extends AbstractFlacLowLevelInput {

    /*---- Fields ----*/

    // The player that provides the stream; also knows the total length and can seek it.
    private final AllMusicPlayer player;

    // The underlying byte-based input stream to read from.
    private BufferedInputStream raf;

    /*---- Constructors ----*/

    public SeekableFileFlacInput(AllMusicPlayer player) {
        super();
        this.player = player;
        this.raf = new BufferedInputStream(player);
    }

    /*---- Methods ----*/

    public long getLength() {
        // The total length of the resource; -1 if the server did not tell us.
        return player.getContentLength();
    }

    protected int readUnderlying(byte[] buf, int off, int len) throws IOException {
        return raf.read(buf, off, len);
    }

    // Re-opens the underlying stream at the given byte position and discards all the data
    // that was buffered for the previous position. This is cheap compared to reading (and
    // decoding) everything in between the old and the new position.
    public void seekTo(long pos) throws IOException {
        if (pos < 0) throw new IllegalArgumentException("Negative seek position");
        player.setLocal(pos);
        raf = new BufferedInputStream(player);
        positionChanged(pos);
    }

    // Closes the underlying stream (very important).
    public void close() throws IOException {
        if (raf != null) {
            raf.close();
            raf = null;
            super.close();
        }
    }
}
