package com.coloryr.allmusic.server.core.music;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.objs.config.LimitObj;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.MusicObj;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.saves.MusicListSave;
import com.coloryr.allmusic.server.core.utils.HudUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayMusic {

    /**
     * 播放列表
     */
    private static final List<SongInfoObj> playList = new ArrayList<>();
    private static final Queue<PlayerAddMusicObj> tasks = new ConcurrentLinkedQueue<>();
    private static final Queue<MusicObj> deep = new ConcurrentLinkedQueue<>();
    /**
     * 正在播放的玩家
     */
    private static final Set<String> nowPlayPlayer = new HashSet<>();
    /**
     * 切歌投票的玩家
     */
    private static final Set<String> votePlayer = new HashSet<>();
    /**
     * 插歌投票的玩家
     */
    private static final Set<String> pushPlayer = new HashSet<>();
    /**
     * 总歌曲长度
     */
    public static long musicAllTime = 0;
    /**
     * 剩余歌曲长度
     */
    public static long musicLessTime = 0;
    /**
     * 歌曲现在位置
     */
    public static long musicNowTime = 0;
    /**
     * 当前歌曲信息
     */
    public static SongInfoObj nowPlayMusic;
    /**
     * 当前歌词信息
     */
    public static LyricSave lyric;
    /**
     * 播放链接
     */
    public static String url;
    /**
     * 错误次数
     */
    public static int error;
    /**
     * 切歌投票时间
     */
    private static int voteTime = 0;
    /**
     * 切歌发起人
     */
    private static String voteSender;
    /**
     * 插歌投票时间
     */
    private static int pushTime = 0;
    /**
     * 插歌发起人
     */
    private static String pushSender;
    /**
     * 插歌目标
     */
    private static SongInfoObj push;
    /**
     * 空闲列表取出的歌曲序号
     */
    private static int idleIndex;

    /**
     * 开始歌曲逻辑
     */
    public static void start() {
        new Thread(PlayMusic::task, "allmusic_task").start();
    }

    /**
     * 添加投票的玩家
     *
     * @param player 用户名
     */
    public static void addVote(String player) {
        player = player.toLowerCase();
        votePlayer.add(player);
    }

    public static void startVote(String player) {
        player = player.toLowerCase();
        voteSender = player;
        votePlayer.add(player);
        voteTime = AllMusic.getConfig().voteTime;
    }

    /**
     * 添加投票的玩家
     *
     * @param player 用户名
     */
    public static void addPush(String player) {
        player = player.toLowerCase();
        pushPlayer.add(player);
    }

    public static void startPush(String player, SongInfoObj music) {
        player = player.toLowerCase();
        push = music;
        pushSender = player;
        pushPlayer.add(player);
        pushTime = AllMusic.getConfig().voteTime;
    }

    public static void pushTick() {
        pushTime--;
    }

    public static void voteTick() {
        voteTime--;
    }

    public static SongInfoObj getPush() {
        return push;
    }

    public static int getPushTime() {
        return pushTime;
    }

    public static String getPushSender() {
        return pushSender;
    }

    public static int getVoteTime() {
        return voteTime;
    }

    public static String getVoteSender() {
        return voteSender;
    }

    /**
     * 获取投票数量
     *
     * @return 数量
     */
    public static int getVoteCount() {
        return votePlayer.size();
    }

    public static int getPushCount() {
        return pushPlayer.size();
    }

    /**
     * 清空投票
     */
    public static void clearVote() {
        voteTime = -1;
        voteSender = null;
        votePlayer.clear();
    }

    /**
     * 清空插歌
     */
    public static void clearPush() {
        pushTime = -1;
        push = null;
        pushSender = null;
        pushPlayer.clear();
    }

    /**
     * 是否已经投票了
     *
     * @param player 用户名
     * @return 结果
     */
    public static boolean containVote(String player) {
        player = player.toLowerCase();
        return votePlayer.contains(player);
    }

    public static boolean containPush(String player) {
        player = player.toLowerCase();
        return pushPlayer.contains(player);
    }

    /**
     * 添加点歌任务
     *
     * @param obj 歌曲
     */
    public static void addTask(PlayerAddMusicObj obj) {
        tasks.add(obj);
    }

    private static void task() {
        AllMusic.log.data("歌曲处理线程启动");
        while (AllMusic.isRun) {
            try {
                PlayerAddMusicObj obj = tasks.poll();
                if (obj != null) {
                    IMusicApi api = AllMusic.MUSIC_APIS.get(obj.api);
                    if (api != null) {
                        addMusic(obj.sender, obj.id, api, obj.name, obj.isDefault);
                    }
                }
                Thread.sleep(10);
            } catch (Exception e) {
                AllMusic.log.data("歌曲处理出现问题");
                e.printStackTrace();
            }
        }
        nowPlayPlayer.clear();
        votePlayer.clear();
        pushPlayer.clear();
        playList.clear();
        clearVote();
        clearPush();

        AllMusic.log.data("歌曲处理线程关闭");
    }

    /**
     * 添加歌曲
     *
     * @param sender 发送者
     * @param id     歌曲ID
     * @param player 用户名
     * @param isList 是否是空闲歌单
     */
    public static void addMusic(Object sender, String id, IMusicApi api, String player, boolean isList) {
        if (haveMusic(id, api.getId()))
            return;
        if (sender != null) {
            String text = AllMusic.getMessage().musicPlay.checkMusic
                    .replace(ARG.musicId, id);
            AllMusic.side.sendMessageTask(sender, text);
        }
        AllMusic.log.data("<light_purple>[AllMusic]<yellow>玩家：" + player + " 点歌：" + id);
        try {
            SongInfoObj info = api.getMusic(id, player, isList);
            if (info == null) {
                if (sender != null) {
                    String data = AllMusic.getMessage().musicPlay.emptyCanPlay;
                    AllMusic.side.sendMessageTask(sender, data.replace(ARG.musicId, id));
                }
                return;
            }
            // 预检查歌曲是否可播放
            if (info.getPlayerUrl() == null) {
                String playUrl = api.getPlayUrl(id);
                if (playUrl == null) {
                    if (sender != null) {
                        String data = AllMusic.getMessage().musicPlay.emptyCanPlay;
                        AllMusic.side.sendMessageTask(sender, data.replace(ARG.musicId, id));
                    }
                    return;
                }
                info.setPlayerUrl(playUrl);
            }
            LimitObj limit = AllMusic.getConfig().limit;
            if (limit.musicTimeLimit && info.getLength() / 1000 > limit.maxMusicTime) {
                if (sender != null) {
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.timeOut);
                }
                return;
            }
            playList.add(info);
            if (!AllMusic.getConfig().muteAddMessage) {
                if (AllMusic.getConfig().showInBar) {
                    String data = AllMusic.getMessage().musicPlay.addMusic
                            .replace(ARG.musicName, HudUtils.messageLimit(info.getName()))
                            .replace(ARG.musicAuthor, HudUtils.messageLimit(info.getAuthor()))
                            .replace(ARG.musicAl, HudUtils.messageLimit(info.getAl()))
                            .replace(ARG.musicAlia, HudUtils.messageLimit(info.getAlia()))
                            .replace(ARG.player, info.getCall());
                    AllMusic.side.sendBarInTask(data);
                } else {
                    String data = AllMusic.getMessage().musicPlay.addMusic
                            .replace(ARG.musicName, info.getName())
                            .replace(ARG.musicAuthor, info.getAuthor())
                            .replace(ARG.musicAl, info.getAl())
                            .replace(ARG.musicAlia, info.getAlia())
                            .replace(ARG.player, info.getCall());
                    AllMusic.side.broadcastInTask(data);
                }
            }
            if (AllMusic.getConfig().playListSwitch
                    && (PlayMusic.nowPlayMusic != null && PlayMusic.nowPlayMusic.isList())) {
                PlayMusic.musicLessTime = 10;
                if (!isList) {
                    AllMusic.side.broadcastInTask(AllMusic.getMessage().musicPlay.switchMusic);
                }
            }
            error = 0;
        } catch (Exception e) {
            if (isList) {
                error++;
            }
            if (sender != null) {
                String data = AllMusic.getMessage().musicPlay.emptyCanPlay;
                AllMusic.side.sendMessageTask(sender, data.replace(ARG.musicId, id));
            }
            AllMusic.log.data("<light_purple>[AllMusic]<red>歌曲信息解析错误");
            e.printStackTrace();
        }
    }

    /**
     * 将歌曲移动到队列头
     */
    public static void pushMusic() {
        SongInfoObj obj = push;
        synchronized (playList) {
            playList.remove(obj);
            playList.add(0, obj);
        }
    }

    /**
     * 获取播放列表长度
     *
     * @return 长度
     */
    public static int getListSize() {
        synchronized (playList) {
            return playList.size();
        }
    }

    /**
     * 获取当前播放列表
     *
     * @return 播放列表
     */
    public static List<SongInfoObj> getList() {
        synchronized (playList) {
            return new ArrayList<>(playList);
        }
    }

    /**
     * 清理播放列表
     */
    public static void clear() {
        synchronized (playList) {
            playList.clear();
        }
    }

    /**
     * 从播放列表删除
     *
     * @param index 标号
     * @return 结果
     */
    public static SongInfoObj remove(int index) {
        synchronized (playList) {
            return playList.remove(index);
        }
    }

    /**
     * 从播放列表删除
     *
     * @param index
     */
    public static void remove(SongInfoObj index) {
        synchronized (playList) {
            playList.remove(index);
        }
    }

    /**
     * 获取播放列表所有信息
     *
     * @return 信息
     */
    public static String getAllList() {
        StringBuilder list = new StringBuilder();
        String a;

        SongInfoObj info;
        for (int i = 0; i < playList.size(); i++) {
            info = playList.get(i);
            a = AllMusic.getMessage().musicPlay.listMusic.item;
            a = a.replace(ARG.index, String.valueOf(i + 1))
                    .replace(ARG.musicName, info.getName())
                    .replace(ARG.musicAuthor, info.getAuthor())
                    .replace(ARG.musicAl, info.getAl())
                    .replace(ARG.musicAlia, info.getAlia())
                    .replace(ARG.player, info.getCall());
            list.append(a).append("\n");
        }
        String temp = list.toString();
        if (temp.isEmpty())
            return "";
        return temp.substring(0, temp.length() - 1);
    }

    /**
     * 是否在播放列表中
     *
     * @param music 音乐
     * @return 结果
     */
    public static boolean haveMusic(MusicObj music) {
        return haveMusic(music.id, music.api);
    }

    /**
     * 是否在播放列表中
     *
     * @param id  音乐编号
     * @param api 音乐API编号
     * @return 是否在列表种
     */
    public static boolean haveMusic(String id, String api) {
        if (nowPlayMusic != null && nowPlayMusic.getID().equalsIgnoreCase(id)
                && Objects.equals(nowPlayMusic.getApi(), api))
            return true;
        for (SongInfoObj item : playList) {
            if (item.getID().equalsIgnoreCase(id) && Objects.equals(item.getApi(), api)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断玩家点歌数量是否超上限
     *
     * @param name 玩家名
     * @return 是否超过上限
     */
    public static boolean isPlayerMax(String name) {
        int list = AllMusic.getConfig().maxPlayerList;
        if (list == 0) {
            return false;
        }
        int count = 0;
        for (SongInfoObj obj : playList) {
            if (obj.getCall().equalsIgnoreCase(name)) {
                count++;
            }
        }

        return list <= count;
    }

    public static void clearIdleList() {
        deep.clear();
        MusicListSave.clearIdleList();
    }

    public static int getIdleListSize() {
        return MusicListSave.getListSize();
    }

    /**
     * 检查这个空闲歌是否已经放了
     *
     * @param music 空闲音乐
     * @return 是否已经放过了
     */
    private static boolean checkDeep(MusicObj music) {
        for (MusicObj obj : deep) {
            if (Objects.equals(obj.id, music.id) && obj.api == music.api) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取空闲歌单的一首歌
     *
     * @return 结果
     */
    public static MusicObj getIdleMusic() {
        MusicObj music;
        int len = MusicListSave.getListSize();
        if (len == 0)
            return null;
        if (AllMusic.getConfig().playListRandom) {
            if (len == 1)
                return MusicListSave.readListItem();
            if (len > 10) {
                int size = AllMusic.getConfig().playListEscapeDeep;
                if (size > len / 2) {
                    size = len / 2;
                }
                while (deep.size() >= size) {
                    deep.poll();
                }
                do {
                    music = MusicListSave.readListItem();
                }
                while (checkDeep(music));
                deep.add(music);
            } else {
                music = MusicListSave.readListItem();
            }
        } else {
            music = MusicListSave.readListItem(idleIndex);
            idleIndex++;
            if (idleIndex >= len) {
                idleIndex = 0;
            }
        }
        return music;
    }

    public static SongInfoObj findPlayerMusic(String name) {
        List<SongInfoObj> list1 = getList();
        for (int a = 0; a < playList.size(); a++) {
            SongInfoObj item = list1.get(a);
            if (name.equalsIgnoreCase(item.getCall())) {
                return item;
            }
        }

        return null;
    }

    public static SongInfoObj findMusicIndex(int index) {
        List<SongInfoObj> list1 = getList();
        index--;
        if (index <= 0) {
            return null;
        }
        if (list1.size() <= index) {
            return null;
        }

        return list1.get(index);
    }

    /**
     * 获取正在播放的玩家列表
     *
     * @return 列表
     */
    public static Set<String> getNowPlayPlayer() {
        return nowPlayPlayer;
    }

    /**
     * 是否存在正在播放的玩家
     *
     * @param player 用户名
     * @return 是否存在
     */
    public static boolean containNowPlay(String player) {
        player = player.toLowerCase();
        return !nowPlayPlayer.contains(player);
    }

    /**
     * 添加正在播放的玩家
     *
     * @param player 用户名
     */
    public static void addNowPlayPlayer(String player) {
        player = player.toLowerCase();
        nowPlayPlayer.add(player);
    }

    /**
     * 删除正在播放的玩家
     *
     * @param player 用户名
     */
    public static void removeNowPlayPlayer(String player) {
        player = player.toLowerCase();
        nowPlayPlayer.remove(player);
    }

    /**
     * 清空正在播放玩家的列表
     */
    public static void clearNowPlayer() {
        nowPlayPlayer.clear();
    }
}

