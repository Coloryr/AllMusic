package com.coloryr.allmusic.server.core.objs.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MusicListObj {
    public Map<String, List<String>> musics;

    public static MusicListObj make() {
        MusicListObj obj = new MusicListObj();
        obj.musics = new HashMap<>();

        return obj;
    }

    public boolean check() {
        boolean saveConfig = false;
        if (musics == null) {
            musics = new HashMap<>();
            saveConfig = true;
        }
        return saveConfig;
    }
}
