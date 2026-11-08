package com.coloryr.allmusic.server.core.utils;

import java.util.regex.Pattern;

public class Function {
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?\\d*\\.?\\d+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 将字符串转为整数
     *
     * @param str 字符串
     * @param def 无法转换时的默认值
     * @return 结果
     */
    public static int getInt(String str, int def) {
        if (str == null)
            return def;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getString(String a, String b, String c) {
        int x = a.indexOf(b) + b.length();
        int y;
        if (c != null)
            y = a.indexOf(c);
        else y = a.length();
        if (x < 0 || y < 0)
            return a;
        return a.substring(x, y);
    }

    public static int countChar(String a, char b) {
        int count = 0;
        for (char t : a.toCharArray()) {
            if (t == b)
                count++;
        }
        return count;
    }
}
