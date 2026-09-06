package com.coloryr.allmusic.server.core.objs.config;

import java.util.*;

public class BanObj {
    public Set<String> banPlayers = new HashSet<>();
    public Set<String> banServer = new HashSet<>();
    public Set<String> mutePlayers = new HashSet<>();
    public Set<String> muteListPlayers = new HashSet<>();
    public Map<String, List<String>> banMusics = new HashMap<>();

    public static BanObj make() {
        return new BanObj();
    }

    public boolean check() {
        return banPlayers == null || banServer == null || banMusics == null || mutePlayers == null
                || muteListPlayers == null;
    }


}
