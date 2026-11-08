package com.coloryr.allmusic.server.core.utils;

/**
 * 文本工具
 */
public class TextUtils {
    /**
     * 转义外部文本里的 &x，避免被旧版客户端当成颜色码
     * <p>
     * 只在 & 和后面的颜色码字符之间插入一个零宽空格（U+200B）：
     * 客户端匹配不到 &x，显示上还是原来的 &，新旧客户端都看不到多余字符
     *
     * @param text 原始文本（歌名、歌手、专辑、别名、歌词等）
     * @return 处理后的文本
     */
    public static String escapeVar(String text) {
        if (text == null || text.indexOf('&') < 0) {
            return text;
        }

        StringBuilder builder = new StringBuilder(text.length() + 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            builder.append(c);
            if (c == '&' && i + 1 < text.length()
                    && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(text.charAt(i + 1)) >= 0) {
                builder.append('\u200B');
            }
        }

        return builder.toString();
    }
}
