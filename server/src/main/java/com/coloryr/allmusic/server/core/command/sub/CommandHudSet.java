package com.coloryr.allmusic.server.core.command.sub;

import com.coloryr.allmusic.codec.HudBasePosObj;
import com.coloryr.allmusic.codec.HudPosType;
import com.coloryr.allmusic.codec.HudType;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.command.ACommand;
import com.coloryr.allmusic.server.core.command.AHudCommand;
import com.coloryr.allmusic.server.core.command.ICommand;
import com.coloryr.allmusic.server.core.objs.message.ARG;
import com.coloryr.allmusic.server.core.utils.HudUtils;

import java.util.*;

public class CommandHudSet extends AHudCommand {
    /**
     * Hud的指令
     */
    private static final List<String> hud = new ArrayList<String>() {{
        this.add("enable");
        this.add("pos");
        this.add("dir");
        this.add("alpha");
        this.add("reset");
    }};
    private static final List<String> pic = new ArrayList<String>() {{
        this.add("size");
        this.add("rotate");
        this.add("speed");
    }};
    private static final List<String> info = new ArrayList<String>() {{
        this.add("color");
        this.add("shadow");
        this.add("loop");
        this.add("gap");
        this.add("maxwidth");
    }};
    private static final List<String> state = new ArrayList<String>() {{
        this.add("color");
        this.add("shadow");
        this.add("gap");
    }};
    private static final List<String> lyric = new ArrayList<String>() {{
        this.add("color");
        this.add("shadow");
        this.add("gap");
        this.add("maxwidth");
    }};

    private final Map<String, ICommand> commandList = new HashMap<>();

    public CommandHudSet(HudType type) {
        super(type);
        commandList.put("enable", new HudEnable(type));
        commandList.put("reset", new HudReset(type));
        commandList.put("pos", new HudPos(type));
        commandList.put("alpha", new HudAlpha(type));
        commandList.put("dir", new HudDir(type));
        if (type == HudType.PIC) {
            commandList.put("size", new PicSize());
            commandList.put("rotate", new PicRotate());
            commandList.put("speed", new PicRotateSpeed());
            commandList.put("bg", new PicBg());
        } else if (type == HudType.INFO) {
            commandList.put("color", new HudColor(type));
            commandList.put("shadow", new HudShadow(type));
            commandList.put("loop", new HudLoop(type));
            commandList.put("gap", new HudGap(type));
            commandList.put("maxwidth", new HudMaxWidth(type));
        } else if (type == HudType.STATE) {
            commandList.put("color", new HudColor(type));
            commandList.put("shadow", new HudShadow(type));
            commandList.put("gap", new HudGap(type));
        } else if (type == HudType.LYRIC) {
            commandList.put("color", new HudColor(type));
            commandList.put("shadow", new HudShadow(type));
            commandList.put("gap", new HudGap(type));
            commandList.put("maxwidth", new HudMaxWidth(type));
        }
    }

    @Override
    public void execute(Object sender, String name, String[] args) {
        if (args.length == 2) {
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        } else {
            ICommand command = commandList.get(args[2]);
            if (command != null) {
                command.execute(sender, name, args);
            } else {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            }
        }
    }

    @Override
    public List<String> tab(Object player, String name, String[] args, int index) {
        if (args.length == index + 1) {
            List<String> list = new ArrayList<>(hud);
            if (type == HudType.PIC) {
                list.addAll(pic);
            } else if (type == HudType.INFO) {
                list.addAll(info);
            } else if (type == HudType.STATE) {
                list.addAll(state);
            } else if (type == HudType.LYRIC) {
                list.addAll(lyric);
            }

            return list;
        } else {
            ICommand command = commandList.get(args[index]);
            if (command != null) {
                return command.tab(player, name, args, index + 1);
            }
        }
        return Collections.emptyList();
    }

    private static class HudEnable extends AHudCommand {
        public HudEnable(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length == 3 || args.length == 4) {
                boolean temp = HudUtils.setHudEnable(name, type, args.length == 4 ? args[3] : null);
                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().hud.state
                        .replace(ARG.value, temp
                                ? AllMusic.getMessage().hudList.enable
                                : AllMusic.getMessage().hudList.disable)
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type)));
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }

    private static class HudReset extends AHudCommand {
        public HudReset(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            HudUtils.reset(name, type);
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.reset
                    .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type)));
        }
    }

    private static class HudPos extends AHudCommand {
        public HudPos(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            try {
                if (args.length != 5) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                    return;
                }
                HudBasePosObj obj = HudUtils.setHudPos(name, type, args[3], args[4]);
                if (obj == null) {
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().command.error);
                    return;
                }

                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().hud.set
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                        .replace(ARG.x, String.valueOf(obj.x))
                        .replace(ARG.y, String.valueOf(obj.y)));
            } catch (Exception e) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            }
        }
    }

    private static class HudAlpha extends AHudCommand {
        public HudAlpha(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            try {
                if (args.length != 4) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                    return;
                }
                float obj = HudUtils.setHudAlpha(name, type, args[3]);
                if (obj == -1) {
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().command.error);
                    return;
                }

                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().hud.set4
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                        .replace(ARG.value, String.valueOf(obj)));
            } catch (Exception e) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            }
        }
    }

    private static class HudMaxWidth extends AHudCommand {
        public HudMaxWidth(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            try {
                if (args.length != 4) {
                    AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                    return;
                }
                int width = HudUtils.setHudHudMaxWidth(name, type, args[3]);
                if (width == -1) {
                    AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().command.error);
                    return;
                }

                AllMusic.side.sendMessageTask(sender, AllMusic.getMessage().hud.set6
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                        .replace(ARG.value, String.valueOf(width)));
            } catch (Exception e) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
            }
        }
    }

    private static class HudDir extends AHudCommand {
        private static final List<String> dir = new ArrayList<String>() {{
            for (HudPosType type : HudPosType.values()) {
                this.add(type.name());
            }
        }};

        public HudDir(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length != 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            HudPosType type1 = HudUtils.setPos(name, type, args[3]);
            if (type1 == null) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.set1
                    .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                    .replace(ARG.value, AllMusic.getMessage().hudList.getDir(type1)));
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return dir;
            }
            return Collections.emptyList();
        }
    }

    private static class HudColor extends AHudCommand {
        public HudColor(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length != 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            int color = HudUtils.setColor(name, type, args[3]);
            if (color == -1) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.set2
                    .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                    .replace(ARG.value, String.format("%06X", color & 0xFFFFFF)));
        }
    }

    private static class HudShadow extends AHudCommand {
        public HudShadow(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length == 3 || args.length == 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.set3
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                        .replace(ARG.value, HudUtils.setShadow(name, type, args.length == 4 ? args[3] : null)
                                ? AllMusic.getMessage().hudList.enable
                                : AllMusic.getMessage().hudList.disable));
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }

    private static class HudLoop extends AHudCommand {
        public HudLoop(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length == 3 || args.length == 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.set5
                        .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                        .replace(ARG.value, HudUtils.setLoop(name, type, args.length == 4 ? args[3] : null)
                                ? AllMusic.getMessage().hudList.enable
                                : AllMusic.getMessage().hudList.disable));
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }

    private static class HudGap extends AHudCommand {
        public HudGap(HudType type) {
            super(type);
        }

        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length != 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            int loop = HudUtils.setGap(name, type, args[3]);
            if (loop == -1) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.set7
                    .replace(ARG.hud, AllMusic.getMessage().hudList.getHud(type))
                    .replace(ARG.value, String.valueOf(loop)));
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }

    private static class PicSize extends ACommand {
        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length != 4 || !HudUtils.setPicSize(name, args[3])) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            AllMusic.side.sendMessage(sender,
                    AllMusic.getMessage().hud.picSize.replace(ARG.value, args[2]));
        }
    }

    private static class PicRotate extends ACommand {
        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length == 3 || args.length == 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.picRotate
                        .replace(ARG.value, HudUtils.setPicRotate(name, args.length == 4 ? args[3] : null)
                                ? AllMusic.getMessage().hudList.enable
                                : AllMusic.getMessage().hudList.disable));
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }

    private static class PicRotateSpeed extends ACommand {
        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length != 4 || !HudUtils.setPicRotateSpeed(name, args[3])) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
                return;
            }
            AllMusic.side.sendMessage(sender,
                    AllMusic.getMessage().hud.picSpeed.replace(ARG.value, args[3]));
        }
    }

    private static class PicBg extends ACommand {
        @Override
        public void execute(Object sender, String name, String[] args) {
            if (args.length == 3 || args.length == 4) {
                AllMusic.side.sendMessage(sender, AllMusic.getMessage().hud.picRotate
                        .replace(ARG.value, HudUtils.setPicBg(name, args.length == 4 ? args[3] : null)
                                ? AllMusic.getMessage().hudList.enable
                                : AllMusic.getMessage().hudList.disable));
                return;
            }
            AllMusic.side.sendMessage(sender, AllMusic.getMessage().command.error);
        }

        @Override
        public List<String> tab(Object player, String name, String[] args, int index) {
            if (args.length == index + 1) {
                return CommandHud.tf;
            }
            return Collections.emptyList();
        }
    }
}
