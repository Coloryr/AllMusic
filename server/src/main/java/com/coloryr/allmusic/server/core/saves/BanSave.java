package com.coloryr.allmusic.server.core.saves;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.config.BanObj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BanSave {
    private static BanObj ban = BanObj.make();
    private static File banFile;

    public static boolean haveBanPlayer(String player) {
        return ban.banPlayers.contains(player);
    }

    public static boolean haveBanServer(String name) {
        name = name.toLowerCase();
        return ban.banServer.contains(name);
    }

    public static void init(File file) throws IOException {
        banFile = new File(file, "ban.json");
        if (!banFile.exists()) {
            banFile.createNewFile();
        }
    }

    private static void banCheck(BanObj newBan) {
        if (newBan == null || newBan.check()) {
            newBan = BanObj.make();
            ban = newBan;
            AllMusic.log.data("<light_purple>[AllMusic]<red>配置文件ban.json错误，已覆盖");
            saveBan();
        } else {
            ban = newBan;
        }
    }

    public static void saveBan() {
        try {
            String data = AllMusic.gson.toJson(ban);
            FileOutputStream out = new FileOutputStream(banFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
            out.close();
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>配置文件ban.json保存错误");
            e.printStackTrace();
        }
    }

    public static void loadBan() throws IOException {
        InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(banFile.toPath()), StandardCharsets.UTF_8);
        BufferedReader bf = new BufferedReader(reader);
        BanObj newBan = AllMusic.gson.fromJson(bf, BanObj.class);
        bf.close();
        reader.close();
        banCheck(newBan);
    }

    public static void addBanMusic(String music, String api) {
        SaveTask.task(() -> {
            List<String> ids = ban.banMusics.get(api);
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(music);
            ban.banMusics.put(api, ids);
            saveBan();
        });
    }

    public static void removeBanMusic(String id, String api) {
        SaveTask.task(() -> {
            List<String> ids = ban.banMusics.get(api);
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.remove(id);
            ban.banMusics.put(api, ids);
            saveBan();
        });
    }

    /**
     * @param id 歌曲ID
     * @return 结果
     */
    public static boolean checkBanMusic(String id, String api) {
        List<String> ids = ban.banMusics.get(api);
        if (ids == null) {
            return false;
        }

        return ids.contains(id);
    }

    public static void clearBan() {
        SaveTask.task(() -> {
            ban.banMusics.clear();
            saveBan();
        });
    }

    public static void addBanPlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.banPlayers.add(player1);
            saveBan();
        });
    }

    public static void removeBanPlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.banPlayers.remove(player1);
            saveBan();
        });
    }

    /**
     * 检查玩家是否在数据库
     *
     * @param name 用户名
     * @return 结果
     */
    public static boolean checkBanPlayer(String name) {
        name = name.toLowerCase(Locale.ROOT);
        return ban.banPlayers.contains(name);
    }

    public static void clearBanPlayer() {
        SaveTask.task(() -> {
            ban.banPlayers.clear();
            saveBan();
        });
    }

    public static void addMutePlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.mutePlayers.add(player1);
            saveBan();
        });
    }

    public static void removeMutePlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.mutePlayers.remove(player1);
            saveBan();
        });
    }

    public static boolean checkMutePlayer(String name) {
        name = name.toLowerCase(Locale.ROOT);
        return ban.mutePlayers.contains(name);
    }

    public static void addMuteListPlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.muteListPlayers.add(player1);
            saveBan();
        });
    }

    public static void removeMuteListPlayer(String player) {
        SaveTask.task(() -> {
            String player1 = player.toLowerCase(Locale.ROOT);
            ban.muteListPlayers.remove(player1);
            saveBan();
        });
    }

    public static boolean checkMuteListPlayer(String name) {
        name = name.toLowerCase(Locale.ROOT);
        return ban.muteListPlayers.contains(name);
    }

    public static Set<String> getBanPlayers() {
        return ban.banPlayers;
    }
}
