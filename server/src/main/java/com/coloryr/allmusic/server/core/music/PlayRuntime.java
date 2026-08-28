package com.coloryr.allmusic.server.core.music;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.MusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.utils.HudUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayRuntime {
    /**
     * 倒数计数器
     */
    private static int ping = 0;
    /**
     * 歌曲定时器
     */
    private static ScheduledExecutorService service;
    /**
     * 事务定时器
     */
    private static ScheduledExecutorService service2;

    private static boolean isPlay;

    /**
     * 启动歌曲工作
     */
    public static void start() {
        new Thread(PlayRuntime::musicPlayTask, "allmusic_play").start();

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(PlayRuntime::time1, 0, 10, TimeUnit.MILLISECONDS);
        service2 = Executors.newSingleThreadScheduledExecutor();
        service2.scheduleAtFixedRate(PlayRuntime::time3, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 停止歌曲工作
     */
    public static void stop() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        if (service2 != null) {
            service2.shutdown();
            service2 = null;
        }
        PlayMusic.musicLessTime = 0;
    }

    /**
     * 清空歌曲数据
     */
    private static void clear() {
        isPlay = false;

        VoteItem vote = PlayMusic.getVote();
        if (vote != null && vote.getType() == VoteItem.VoteType.NEXT
                && vote.getApi().equalsIgnoreCase(PlayMusic.nowPlayMusic.getApi())
                && vote.getId().equalsIgnoreCase(PlayMusic.nowPlayMusic.getId())) {
            AllMusic.side.broadcastInTask(AllMusic.getMessage().vote.cancel1);
            PlayMusic.removeVote();
        }

        PlayMusic.musicNowTime = 0;
        PlayMusic.musicAllTime = 0;
        PlayMusic.musicLessTime = 0;
        PlayMusic.lyric = null;
        PlayMusic.nowPlayMusic = null;
        AllMusic.side.updateInfo();
        HudUtils.sendClearHud();
        HudUtils.sendHudNowData();
        HudUtils.sendHudTime();
        HudUtils.sendHudLyricData();
    }


    /**
     * 歌曲时间定时器
     */
    private static void time1() {
        if (isPlay) {
            PlayMusic.musicNowTime += 10;
            if (PlayMusic.musicLessTime >= 10) {
                PlayMusic.musicLessTime -= 10;
            } else {
                PlayMusic.musicLessTime = 0;
            }
        }

        if (PlayMusic.lyric != null && PlayMusic.lyric.isHaveLyric()) {
            try {
                if (PlayMusic.lyric == null)
                    return;
                boolean res = PlayMusic.lyric.lyricGetNext(PlayMusic.musicNowTime);
                if (res) {
                    HudUtils.sendHudLyricData();
                    AllMusic.side.updateLyric();
                    AllMusic.side.sendHudKtv();
                }

                if (AllMusic.getConfig().ktvMode) {
                    if (PlayMusic.lyric.ktvGetNext(PlayMusic.musicNowTime)) {
                        AllMusic.side.sendHudKtv();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getMiniVote() {
        return Math.min(AllMusic.getConfig().vote.minVote, AllMusic.side.getPlayers().size());
    }

    public static boolean checkMusic(String id, String api) {
        if (PlayMusic.nowPlayMusic.getId().equalsIgnoreCase(id)
                && PlayMusic.nowPlayMusic.getApi().equalsIgnoreCase(api)) {
            return true;
        }
        return PlayMusic.getMusic(id, api) != null;
    }

    private static void sendVoteInfo(boolean timeout) {
        if (timeout) {
            VoteItem vote = PlayMusic.getVote();
            AllMusic.side.broadcastInTask(AllMusic.getMessage().vote.timeOut
                    .replace(ARG.count, String.valueOf(vote.votePlayer.size()))
                    .replace(ARG.countAll, String.valueOf(getMiniVote())));
        }

        int count = PlayMusic.getVoteCount();
        if (count > 0) {
            AllMusic.side.broadcastInTask(AllMusic.getMessage().vote.list
                    .replace(ARG.count, String.valueOf(count)));
        }
    }

    /**
     * 事务定时器
     */
    private static void time3() {
        try {
            ping++;
            if (ping >= 10) {
                AllMusic.side.ping();
            }

            VoteItem vote = PlayMusic.getVote();
            if (vote != null) {
                PlayMusic.voteTick();
                if (PlayMusic.getVoteTime() <= 0) {
                    sendVoteInfo(true);
                    PlayMusic.removeVote();
                } else {
                    if (PlayMusic.getVote().votePlayer.size() >= getMiniVote()) {
                        sendVoteInfo(false);
                        PlayMusic.doVote();
                    }
                }
            } else {
                vote = PlayMusic.nextVote();
                if (vote != null) {
                    if (vote.getType() == VoteItem.VoteType.NEXT) {
                        String data = AllMusic.getMessage().vote.bq;
                        data = data.replace(ARG.player, vote.getVoteSender())
                                .replace(ARG.time, String.valueOf(AllMusic.getConfig().vote.voteTime))
                                .replace(ARG.countAll, String.valueOf(PlayRuntime.getMiniVote()));
                        AllMusic.side.broadcast(data);
                        AllMusic.side.broadcast(AllMusic.side.miniMessage(AllMusic.getMessage().vote.bq1)
                                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().vote.bq2, "/music agree")));
                    } else if (vote.getType() == VoteItem.VoteType.PUSH) {
                        SongInfoObj music = PlayMusic.getMusic(vote.getId(), vote.getApi());
                        if (music == null) {
                            AllMusic.side.broadcast(AllMusic.getMessage().push.err4);
                            PlayMusic.removeVote();
                            return;
                        }
                        String data = AllMusic.getMessage().push.bq;
                        data = data.replace(ARG.player, vote.getVoteSender())
                                .replace(ARG.time, String.valueOf(AllMusic.getConfig().vote.voteTime))
                                .replace(ARG.musicName, music.getName())
                                .replace(ARG.musicAuthor, music.getAuthor())
                                .replace(ARG.countAll, String.valueOf(PlayRuntime.getMiniVote()));
                        AllMusic.side.broadcast(data);
                        AllMusic.side.broadcast(AllMusic.side.miniMessage(AllMusic.getMessage().push.bq1)
                                .append(AllMusic.side.miniMessageRun(AllMusic.getMessage().push.bq2, "/music agree")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void musicPlayTask() {
        AllMusic.log.data("歌曲播放线程启动");
        while (AllMusic.isRun) {
            try {
                if (PlayMusic.getListSize() == 0) {
                    Thread.sleep(1000);
                    if (PlayMusic.error >= 10) {
                        Thread.sleep(10000);
                    } else if (AllMusic.side.needPlay(true) && PlayMusic.getIdleListSize() > 0) {
                        MusicObj music = PlayMusic.getIdleMusic();
                        if (music != null) {
                            IMusicApi api = AllMusic.MUSIC_APIS.get(music.api);
                            if (api != null && !api.isBusy()) {
                                PlayMusic.addMusic(null, music.id, api, AllMusic.getMessage().custom.idle, true);
                            }
                        }
                    }
                } else {
                    HudUtils.sendClearHud();
                    HudUtils.sendHudNowData();
                    HudUtils.sendHudTime();
                    HudUtils.sendHudLyricData();
                    AllMusic.side.sendHudUtilsAll();
                    PlayMusic.nowPlayMusic = PlayMusic.remove(0);
                    if (AllMusic.side.onMusicPlay(PlayMusic.nowPlayMusic)) {
                        AllMusic.side.broadcastInTask(AllMusic.getMessage().musicPlay.cancel);
                        continue;
                    }

                    IMusicApi api = AllMusic.MUSIC_APIS.get(PlayMusic.nowPlayMusic.getApi());

                    PlayMusic.url = PlayMusic.nowPlayMusic.getPlayerUrl() == null ?
                            api.getPlayUrl(PlayMusic.nowPlayMusic.getId()) :
                            PlayMusic.nowPlayMusic.getPlayerUrl();
                    if (PlayMusic.url == null) {
                        String data = AllMusic.getMessage().musicPlay.emptyCanPlay;
                        AllMusic.side.broadcastInTask(data.replace(ARG.musicId, PlayMusic.nowPlayMusic.getId()));
                        PlayMusic.nowPlayMusic = null;
                        continue;
                    }

                    if (PlayMusic.nowPlayMusic.getPlayerUrl() == null)
                        PlayMusic.lyric = api.getLyric(PlayMusic.nowPlayMusic.getId());
                    else
                        PlayMusic.lyric = new LyricSave();

                    if (PlayMusic.nowPlayMusic.getLength() != 0) {
                        PlayMusic.musicAllTime = PlayMusic.musicLessTime = PlayMusic.nowPlayMusic.getLength() + AllMusic.getConfig().fixSongTime;
                        isPlay = true;
                        AllMusic.side.sendMusic(PlayMusic.url);
                        if (!AllMusic.getConfig().mutePlayMessage) {
                            SongInfoObj music = PlayMusic.nowPlayMusic;
                            if (AllMusic.getConfig().showInBar) {
                                String info = AllMusic.getMessage().musicPlay.nowPlay
                                        .replace(ARG.musicName, HudUtils.messageLimit(music.getName()))
                                        .replace(ARG.musicAuthor, HudUtils.messageLimit(music.getAuthor()))
                                        .replace(ARG.musicAl, HudUtils.messageLimit(music.getAl()))
                                        .replace(ARG.musicAlia, HudUtils.messageLimit(music.getAlia()))
                                        .replace(ARG.player, music.getCall());
                                AllMusic.side.sendBarInTask(info);
                            } else {
                                String info = AllMusic.getMessage().musicPlay.nowPlay
                                        .replace(ARG.musicName, music.getName())
                                        .replace(ARG.musicAuthor, music.getAuthor())
                                        .replace(ARG.musicAl, music.getAl())
                                        .replace(ARG.musicAlia, music.getAlia())
                                        .replace(ARG.player, music.getCall());
                                AllMusic.side.broadcastInTask(info);
                            }
                        }
                        if (PlayMusic.nowPlayMusic.getPicUrl() != null) {
                            AllMusic.side.sendPic(PlayMusic.nowPlayMusic.getPicUrl());
                        }
                        if (PlayMusic.nowPlayMusic.isTrial()) {
                            AllMusic.side.broadcastInTask(AllMusic.getMessage().musicPlay.trail);
                            PlayMusic.musicLessTime = PlayMusic.nowPlayMusic.getTrialInfo().end;
                            PlayMusic.musicNowTime = PlayMusic.nowPlayMusic.getTrialInfo().start;
                        }

                        AllMusic.side.updateInfo();

                        while (PlayMusic.musicLessTime > 0) {
                            HudUtils.sendHudNowData();
                            HudUtils.sendHudTime();
                            if (PlayMusic.nowPlayMusic == null || !AllMusic.side.needPlay(PlayMusic.nowPlayMusic.isList())) {
                                PlayMusic.musicLessTime = 10;
                            }
                            Thread.sleep(AllMusic.getConfig().sendDelay);
                        }
                        AllMusic.side.sendStop();
                    } else {
                        String data = AllMusic.getMessage().musicPlay.emptyCanPlay;
                        AllMusic.side.broadcastInTask(data.replace(ARG.musicId, PlayMusic.nowPlayMusic.getId()));
                    }
                    clear();
                }
            } catch (Exception e) {
                AllMusic.log.data("<red>歌曲播放出现错误");
                e.printStackTrace();
            }
        }
        AllMusic.log.data("歌曲播放线程停止");
    }
}
