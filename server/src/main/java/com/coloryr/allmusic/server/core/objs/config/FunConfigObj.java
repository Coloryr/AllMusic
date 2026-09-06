package com.coloryr.allmusic.server.core.objs.config;

public class FunConfigObj {
    public boolean rain;
    public int rainRate;

    public static FunConfigObj make() {
        FunConfigObj obj = new FunConfigObj();
        obj.rain = true;
        obj.rainRate = 10;

        return obj;
    }
}
