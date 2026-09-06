package com.coloryr.allmusic.server.core.saves;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.config.MusicListObj;
import com.coloryr.allmusic.server.core.objs.music.MusicObj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MusicListSave {
    private static File musicFile;

    private static MusicListObj musicObj;

    public static void init(File file) throws IOException {
        musicFile = new File(file, "music.json");
        if (!musicFile.exists()) {
            musicFile.createNewFile();
        }
    }

    public static void saveMusicList() {
        try {
            String data = AllMusic.gson.toJson(musicObj);
            FileOutputStream out = new FileOutputStream(musicFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
            out.close();
        } catch (Exception e) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>配置文件music.json保存错误");
            e.printStackTrace();
        }
    }

    public static void musicListCheck() {
        if (musicObj == null) {
            musicObj = MusicListObj.make();
            AllMusic.log.data("<light_purple>[AllMusic]<red>配置文件music.json错误，已覆盖");
            saveMusicList();
        } else if (musicObj.check()) {
            AllMusic.log.data("<light_purple>[AllMusic]<red>配置文件music.json错误，已覆盖");
            saveMusicList();
        }
    }

    public static void loadMusic() throws IOException {
        InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(musicFile.toPath()), StandardCharsets.UTF_8);
        BufferedReader bf = new BufferedReader(reader);
        musicObj = AllMusic.gson.fromJson(bf, MusicListObj.class);
        bf.close();
        reader.close();
        musicListCheck();
    }

    /**
     * 随机获取空闲歌单歌曲
     */
    public static MusicObj readListItem() {
        int count = getListSize();

        return readListItem(AllMusic.random.nextInt(count));
    }

    /**
     * 读取空闲歌单列表
     */
    public static MusicObj readListItem(int index) {
        for (Map.Entry<String, List<String>> item : musicObj.musics.entrySet()) {
            if (item.getValue().size() > index) {
                String id = item.getValue().get(index);
                MusicObj obj = new MusicObj();
                obj.api = item.getKey();
                obj.id = id;

                return obj;
            }

            index -= item.getValue().size();
        }

        return null;
    }

    /**
     * 获取空闲歌单歌曲数量
     *
     * @return 歌曲数量
     */
    public static int getListSize() {
        int count = 0;
        for (Map.Entry<String, List<String>> item : musicObj.musics.entrySet()) {
            count += item.getValue().size();
        }

        return count;
    }

    public static void addIdleList(List<String> list, String api) {
        SaveTask.task(() -> {
            List<String> ids = musicObj.musics.get(api);
            if (ids == null) {
                ids = new ArrayList<>();
            }

            ids.addAll(list);

            musicObj.musics.put(api, ids);
            saveMusicList();
        });
    }

    public static void clearIdleList() {
        SaveTask.task(() -> {
            musicObj.musics.clear();
            saveMusicList();
        });
    }
}
