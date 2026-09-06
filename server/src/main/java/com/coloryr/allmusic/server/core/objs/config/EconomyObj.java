package com.coloryr.allmusic.server.core.objs.config;

public class EconomyObj {
    public String backend;
    public boolean vault;

    public static EconomyObj make() {
        EconomyObj obj = new EconomyObj();
        obj.backend = "server1";
        obj.vault = true;
        return obj;
    }
}
