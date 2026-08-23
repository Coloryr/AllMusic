package com.coloryr.allmusic.server;

import com.coloryr.allmusic.server.core.side.IAllMusicLogger;
import net.kyori.adventure.text.Component;

public class LogBungee implements IAllMusicLogger {
    @Override
    public void data(Component data) {
        AllMusicBungee.adventure.console().sendMessage(data);
    }
}
