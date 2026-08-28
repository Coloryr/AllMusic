package com.coloryr.allmusic.server.core;

import com.coloryr.allmusic.server.core.music.LyricSave;
import com.coloryr.allmusic.server.core.objs.music.SearchPageObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;

import java.io.File;
import java.util.List;

public interface IMusicApi {
    void reload(File path);

    String getId();

    SongInfoObj getMusic(String id, String player, boolean isList);

    SearchPageObj search(String[] args);

    void setList(String id, Object sender);

    LyricSave getLyric(String id);

    String getPlayUrl(String id);

    boolean isBusy();

    String getMusicId(String arg);

    boolean checkId(String id);

    void command(Object sender, String name, String[] args);

    List<String> tab(Object sender, String name, String[] args);
}
