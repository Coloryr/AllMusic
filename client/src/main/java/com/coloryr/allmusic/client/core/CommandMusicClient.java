package com.coloryr.allmusic.client.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * /music_client 指令的通用实现
 * <p>
 * 这里只做参数解析和输出内容，不依赖任何 Minecraft 类，
 * 每个版本各自实现一层很薄的指令壳（比如 1.7.10 的 {@code CommandMusicClientForge}），
 * 把 {@link #execute(String[])} 的结果发给玩家即可
 */
public class CommandMusicClient {
    public static final String NAME = "music_client";

    /**
     * 子指令：帮助
     */
    public static final String ARG_HELP = "help";
    /**
     * 子指令：状态
     */
    public static final String ARG_INFO = "info";
    /**
     * 子指令：自检
     */
    public static final String ARG_TEST = "test";
    /**
     * 子指令：图片大小
     */
    public static final String ARG_IMAGE_SIZE = "imagesize";

    /**
     * 所有子指令，Tab 补全用
     */
    public static final String[] SUB_COMMANDS = {ARG_HELP, ARG_INFO, ARG_TEST, ARG_IMAGE_SIZE};

    /**
     * 普通输出
     */
    public static final int STYLE_NORMAL = 0;
    /**
     * 正常（[OK]）
     */
    public static final int STYLE_OK = 1;
    /**
     * 异常（[!!]）
     */
    public static final int STYLE_ERROR = 2;

    /**
     * 指令用法
     *
     * @return 用法
     */
    public static String usage() {
        return "/" + NAME + " " + ARG_HELP;
    }

    /**
     * 执行指令
     *
     * @param args 参数（不含指令名本身）
     * @return 要显示的内容
     */
    public static Result execute(String[] args) {
        if (args == null || args.length == 0) {
            return help();
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case ARG_HELP:
                return help();
            case ARG_INFO:
                return new Result("AllMusic 客户端信息", new ArrayList<>(AllMusicCore.getInfoLines()));
            case ARG_TEST:
                return test();
            case ARG_IMAGE_SIZE:
                return imageSize(args);
            default:
                return help();
        }
    }

    /**
     * 执行指令并把结果发到聊天栏
     * <p>
     * 各版本的指令壳解析出参数后直接调用这里，输出统一走游戏桥，
     * 不用关心各版本聊天 API 的差异
     *
     * @param args 参数（不含指令名本身）
     * @return Brigadier 需要的返回值
     */
    public static int run(String[] args) {
        if (AllMusicCore.bridge == null) {
            return 0;
        }

        Result result = execute(args);
        if (result.title != null) {
            AllMusicCore.bridge.sendMessage(result.title);
        }
        for (String line : result.lines) {
            AllMusicCore.bridge.sendMessage(line);
        }

        return 1;
    }

    /**
     * 处理一条聊天栏输入
     * <p>
     * 没有客户端指令 API 的版本（比如 1.16.5 Forge）在拦截「发送聊天/指令」时调用这里，
     * 命中 {@code /music_client} 就本地处理并返回 true，调用方把这条消息吞掉即可
     *
     * @param message 原始输入，例如 {@code /music_client imagesize 128}
     * @return 是否已处理（true 表示不要发给服务端）
     */
    public static boolean handleChat(String message) {
        if (message == null) {
            return false;
        }

        String text = message.trim();
        if (!text.startsWith("/")) {
            return false;
        }

        String[] parts = text.substring(1).trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty() || !NAME.equalsIgnoreCase(parts[0])) {
            return false;
        }

        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        run(args);

        return true;
    }

    /**
     * 输出行的样式，方便各版本上色
     *
     * @param line 一行内容
     * @return 样式
     */
    public static int styleOf(String line) {
        if (line == null) {
            return STYLE_NORMAL;
        }
        if (line.startsWith("[OK]")) {
            return STYLE_OK;
        }
        if (line.startsWith("[!!]")) {
            return STYLE_ERROR;
        }
        return STYLE_NORMAL;
    }

    private static Result help() {
        List<String> lines = new ArrayList<>();

        lines.add("/" + NAME + " " + ARG_HELP + " - 显示帮助");
        lines.add("/" + NAME + " " + ARG_INFO + " - 显示客户端状态（图片大小、封面、播放状态等）");
        lines.add("/" + NAME + " " + ARG_TEST + " - 运行客户端自检并输出结果");
        lines.add("/" + NAME + " " + ARG_IMAGE_SIZE + " - 查看当前图片大小");
        lines.add("/" + NAME + " " + ARG_IMAGE_SIZE + " <" + AllMusicCore.MIN_PIC_SIZE + "-"
                + AllMusicCore.MAX_PIC_SIZE + "> - 修改图片大小");
        lines.add("当前图片大小: " + AllMusicCore.getPicSize());

        return new Result("AllMusic 客户端指令", lines);
    }

    private static Result test() {
        List<String> lines = new ArrayList<>(AllMusicCore.getTestLines());

        lines.add("自检完成: " + count(lines, "[OK]") + " 项正常, " + count(lines, "[!!]") + " 项异常");

        return new Result("AllMusic 客户端自检", lines);
    }

    private static Result imageSize(String[] args) {
        List<String> lines = new ArrayList<>();
        String title = "AllMusic 图片大小";

        if (args.length != 2) {
            if (args.length == 1) {
                lines.add("当前图片大小: " + AllMusicCore.getPicSize());
            } else {
                lines.add("用法: /" + NAME + " " + ARG_IMAGE_SIZE + " <"
                        + AllMusicCore.MIN_PIC_SIZE + "-" + AllMusicCore.MAX_PIC_SIZE + ">");
            }
            return new Result(title, lines);
        }

        int size;
        try {
            size = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            lines.add("不是有效的数字: " + args[1]);
            return new Result(title, lines);
        }

        if (!AllMusicCore.setPicSize(size)) {
            lines.add("图片大小必须在 " + AllMusicCore.MIN_PIC_SIZE + "-"
                    + AllMusicCore.MAX_PIC_SIZE + " 之间");
            return new Result(title, lines);
        }

        lines.add("图片大小已设置为 " + size + "，正在重新加载封面；没有封面时下一首歌生效");
        lines.add("当前图片大小: " + AllMusicCore.getPicSize());

        return new Result(title, lines);
    }

    private static int count(List<String> lines, String prefix) {
        int count = 0;
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 指令执行结果
     */
    public static class Result {
        /**
         * 标题，可为 null
         */
        public final String title;
        /**
         * 内容行
         */
        public final List<String> lines;

        public Result(String title, List<String> lines) {
            this.title = title;
            this.lines = lines;
        }
    }
}
