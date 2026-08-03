package com.coloryr.allmusic.server.core.command;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;
import com.coloryr.allmusic.server.core.command.sub.*;
import com.coloryr.allmusic.server.core.music.MusicSearch;
import com.coloryr.allmusic.server.core.music.PlayMusic;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.objs.music.PlayerAddMusicObj;
import com.coloryr.allmusic.server.core.saves.BanSave;
import com.coloryr.allmusic.server.core.saves.SaveTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandEX {

    public static final Map<String, ICommand> commandList = new HashMap<>();
    public static final Map<String, ICommand> commandAdminList = new HashMap<>();
    private static final List<String> normal = new ArrayList<>();
    /**
     * 管理员的指令
     */
    private static final List<String> admin = new ArrayList<>();
    /**
     * 搜歌的指令
     */
    private static final List<String> search = new ArrayList<>();

    static {
        commandList.put("stop", new CommandStop());
        commandList.put("help", new CommandHelp());
        commandList.put("list", new CommandList());
        commandList.put("vote", new CommandVote());
        commandList.put("mute", new CommandMute());
        commandList.put("search", new CommandSearch());
        commandList.put("searchapi", new CommandSearchApi());
        commandList.put("select", new CommandSelect());
        commandList.put("nextpage", new CommandNextPage());
        commandList.put("lastpage", new CommandLastPage());
        commandList.put("hud", new CommandHud());
        commandList.put("push", new CommandPush());
        commandList.put("join", new CommandJoin());
        commandList.put("cancel", new CommandCancel());

        commandAdminList.put("reload", new CommandReload());
        commandAdminList.put("next", new CommandNext());
        commandAdminList.put("ban", new CommandBan());
        commandAdminList.put("unban", new CommandUnban());
        commandAdminList.put("banplayer", new CommandBanPlayer());
        commandAdminList.put("unbanplayer", new CommandUnbanPlayer());
        commandAdminList.put("delete", new CommandDelete());
        commandAdminList.put("addlist", new CommandAddList());
        commandAdminList.put("clearlist", new CommandClearList());
        commandAdminList.put("clearban", new CommandClearBanList());
        commandAdminList.put("clearbanplayer", new CommandClearBanPlayerList());
        commandAdminList.put("test", new CommandTest());

        normal.addAll(commandList.keySet());
        admin.addAll(commandAdminList.keySet());

        search.add("select");
        search.add("nextpage");
        search.add("lastpage");
    }

    /**
     * 搜索音乐
     *
     * @param sender    发送者
     * @param name      用户名
     * @param args      参数
     * @param isDefault 是否是默认点歌方式
     */
    public static void searchMusic(Object sender, String name, String[] args, boolean isDefault) {

        String apiname = AllMusic.getConfig().defaultApi;

        if (!AllMusic.MUSIC_APIS.containsKey(apiname)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }

        PlayerAddMusicObj obj = new PlayerAddMusicObj();
        obj.sender = sender;
        obj.name = name;
        obj.args = args;
        obj.isDefault = isDefault;
        obj.api = apiname;

        if (AllMusic.side.onMusicAdd(sender, obj)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().addMusic.cancel);
            return;
        }

        MusicSearch.addSearch(obj);
    }

    /**
     * 搜索音乐
     *
     * @param sender    发送者
     * @param name      用户名
     * @param args      参数
     * @param isDefault 是否是默认点歌方式
     */
    public static void searchMusicApi(Object sender, String name, String[] args, boolean isDefault) {
        if (args == null || args.length < 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }

        String apiname = args[0];

        if (!AllMusic.MUSIC_APIS.containsKey(apiname)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }
        
        PlayerAddMusicObj obj = new PlayerAddMusicObj();
        obj.sender = sender;
        obj.name = name;
        obj.args = args;
        obj.isDefault = isDefault;
        obj.api = apiname;

        if (AllMusic.side.onMusicAdd(sender, obj)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().addMusic.cancel);
            return;
        }

        MusicSearch.addSearch(obj);
    }

    /**
     * 检查金额是否够
     *
     * @param sender 发送者
     * @param name   用户名
     * @param cost   金额
     * @return 结果
     */
    public static boolean checkMoney(Object sender, String name, int cost) {
        if (!AllMusic.getConfig().cost.useCost || AllMusic.economy == null) {
            return false;
        }

        if (!AllMusic.economy.check(name, cost)) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().cost.noMoney);
            return true;
        }
        return false;
    }

    /**
     * 扣钱
     *
     * @param sender  发送者
     * @param name    用户名
     * @param cost    金额
     * @param message 结果消息
     * @return 结果
     */
    public static boolean cost(Object sender, String name, int cost, String message) {
        if (!AllMusic.getConfig().cost.useCost || AllMusic.economy == null) {
            return false;
        }

        if (AllMusic.economy.cost(name, cost)) {
            AllMusic.side.sendMessage(sender, message
                    .replace(ARG.cost, String.valueOf(cost)));
            return false;
        } else {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().cost.costFail);
            return true;
        }
    }

    /**
     * 添加音乐
     *
     * @param sender 发送者
     * @param name   用户名
     * @param arg    参数
     */
    public static void addMusic(Object sender, String name, String api, String arg) {
        String musicID;

        IMusicApi api1 = AllMusic.MUSIC_APIS.get(api);
        if (api1 == null) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().musicPlay.error2);
            return;
        }

        musicID = api1.getMusicId(arg);

        if (api1.checkId(musicID)) {
            if (PlayMusic.getListSize() >= AllMusic.getConfig().maxPlayList) {
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.listFull);
            } else if (BanSave.checkBanMusic(musicID, api)) {
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.banMusic);
            } else if (PlayMusic.haveMusic(musicID, api)) {
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.existMusic);
            } else if (PlayMusic.isPlayerMax(name)) {
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.playerToMany);
            } else if (BanSave.checkBanPlayer(name)) {
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.playerBan);
            } else {
                if (checkMoney(sender, name, AllMusic.getConfig().cost.addMusicCost)) {
                    return;
                }
                if (cost(sender, name, AllMusic.getConfig().cost.addMusicCost,
                        AllMusic.getMessage().cost.addMusic)) {
                    return;
                }
                BanSave.removeMutePlayer(name);
                if (AllMusic.side.needPlay(false)) {
                    PlayerAddMusicObj obj = new PlayerAddMusicObj();
                    obj.sender = sender;
                    obj.id = musicID;
                    obj.name = name;
                    obj.isDefault = false;
                    obj.api = api;

                    if (AllMusic.side.onMusicAdd(sender, obj)) {
                        AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.cancel);
                        return;
                    }

                    PlayMusic.addTask(obj);
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.success);
                } else
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.noPlayer);
            }
        } else
            AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().addMusic.noID);
    }

    private static boolean isAdmin(String name) {
        for (String item : AllMusic.getConfig().adminList) {
            if (item.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行命令
     *
     * @param sender 发送者
     * @param name   用户名
     * @param args   参数
     */
    public static void execute(Object sender, String name, String[] args) {
        try {
            if (args.length == 0) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            ICommand command = commandList.get(args[0]);
            if (command != null) {
                command.execute(sender, name, args);
                return;
            }

            if (checkAdmin(sender, name)) {
                command = commandAdminList.get(args[0]);
                if (command != null) {
                    command.execute(sender, name, args);
                    return;
                }
            }
            if (AllMusic.getConfig().needPermission &&
                    !AllMusic.side.checkPermission(sender, PermissionList.PERMISSION_ADD_MUSIC))
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.noPer);
            else {
                switch (AllMusic.getConfig().defaultAddMusic) {
                    case 1:
                        searchMusic(sender, name, args, true);
                        break;
                    case 0:
                    default:
                        if (args.length == 1) {
                            SaveTask.task(() -> addMusic(sender, name, AllMusic.getConfig().defaultApi, args[0]));
                        } else if (args.length == 2) {
                            SaveTask.task(() -> addMusic(sender, name, args[0], args[1]));
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Tab指令列表
     *
     * @param name 用户名
     * @param arg  参数
     * @return 指令列表
     */
    public static List<String> getTabList(Object sender, String name, String[] arg) {
        List<String> arguments = new ArrayList<>();
        if (arg.length == 0) {
            arguments.addAll(normal);
            if (checkAdmin(sender, name)) {
                arguments.addAll(admin);
            }
            if (MusicSearch.getSearch(name) != null) {
                return search;
            }
        } else {
            if (arg[0] == null || arg[0].isEmpty() || arg.length == 1) {
                arguments.addAll(normal);
                if (checkAdmin(sender, name)) {
                    arguments.addAll(admin);
                }
                if (arg[0] == null || arg[0].isEmpty()) {
                    if (MusicSearch.getSearch(name) != null) {
                        return search;
                    }
                }
            } else {
                ICommand command = CommandEX.commandList.get(arg[0]);
                if (command != null) {
                    arguments.addAll(command.tab(sender, name, arg, 1));
                }
                if (checkAdmin(sender, name)) {
                    command = CommandEX.commandAdminList.get(arg[0]);
                    if (command != null) {
                        arguments.addAll(command.tab(sender, name, arg, 1));
                    }
                }
            }
        }

        return arguments;
    }

    /**
     * 检查是否为管理员
     *
     * @param sender 用户
     * @param name   用户名
     * @return 是否为管理员
     */
    public static boolean checkAdmin(Object sender, String name) {
        return isAdmin(name) || AllMusic.side.checkPermission(sender);
    }
}
