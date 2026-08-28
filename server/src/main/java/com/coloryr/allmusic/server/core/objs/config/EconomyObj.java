package com.coloryr.allmusic.server.core.objs.config;

public class EconomyObj {
    public String backend;
    public boolean vault;

    public static EconomyObj make() {
        EconomyObj obj = new EconomyObj();
        obj.init();

        return obj;
    }

    public boolean check() {

        return false;
    }

    public void init() {
        backend = "Server1";
        vault = true;
    }
}
