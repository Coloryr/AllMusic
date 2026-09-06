package com.coloryr.allmusic.server.core.objs.config;

import java.util.*;

public class BanObj {
    public Set<String> banPlayers;
    public Set<String> banServer;
    public Set<String> mutePlayers;
    public Set<String> muteListPlayers;
    public Map<String, List<String>> banMusics;

    public static BanObj make() {
        BanObj obj = new BanObj();
        obj.banMusics = new HashMap<>();
        obj.banPlayers = new HashSet<>();
        obj.banServer = new HashSet<>();
        obj.mutePlayers = new HashSet<>();
        obj.muteListPlayers = new HashSet<>();

        return obj;
    }

    public boolean check() {
        boolean saveConfig = false;
        if (banPlayers == null) {
            banPlayers = new HashSet<>();
            saveConfig = true;
        }
        if (banServer == null) {
            banServer = new HashSet<>();
            saveConfig = true;
        }
        if (mutePlayers == null) {
            mutePlayers = new HashSet<>();
            saveConfig = true;
        }
        if (muteListPlayers == null) {
            muteListPlayers = new HashSet<>();
            saveConfig = true;
        }
        if (banMusics == null) {
            banMusics = new HashMap<>();
            saveConfig = true;
        }

        return saveConfig;
    }


}
