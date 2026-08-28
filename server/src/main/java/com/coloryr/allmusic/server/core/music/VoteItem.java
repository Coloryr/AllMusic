package com.coloryr.allmusic.server.core.music;

import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;

import java.util.HashSet;
import java.util.Set;

public class VoteItem {
    public enum VoteType {
        NEXT, PUSH
    }

    public final Set<String> votePlayer = new HashSet<>();

    private final String api;
    private final String id;
    private final VoteType type;
    private final String voteSender;

    public String getVoteSender() {
        return voteSender;
    }

    public VoteType getType() {
        return type;
    }

    public String getApi() {
        return api;
    }

    public String getId() {
        return id;
    }

    public VoteItem(String api, String id, String voteSender, VoteType type) {
        this.api = api;
        this.id = id;
        this.type = type;
        this.voteSender = voteSender;
    }

    public boolean like(SongInfoObj info) {
        return api.equalsIgnoreCase(info.getApi())
                && id.equalsIgnoreCase(info.getId());
    }
}
