package com.coloryr.allmusic.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniMessage {
    // 正则表达式匹配标签
    private static final Pattern TAG_PATTERN = Pattern.compile(
            "<(/?)(\\w+)(?:\\(([^)]+)\\))?(?::([^>]+))?>"
    );

    // 颜色映射表
    private static final Map<String, ChatFormatting> COLOR_MAP = new HashMap<>();
    // 装饰器映射
    private static final Map<String, ChatFormatting> DECORATION_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("black", ChatFormatting.BLACK);
        COLOR_MAP.put("dark_blue", ChatFormatting.DARK_BLUE);
        COLOR_MAP.put("dark_green", ChatFormatting.DARK_GREEN);
        COLOR_MAP.put("dark_aqua", ChatFormatting.DARK_AQUA);
        COLOR_MAP.put("dark_red", ChatFormatting.DARK_RED);
        COLOR_MAP.put("dark_purple", ChatFormatting.DARK_PURPLE);
        COLOR_MAP.put("gold", ChatFormatting.GOLD);
        COLOR_MAP.put("gray", ChatFormatting.GRAY);
        COLOR_MAP.put("dark_gray", ChatFormatting.DARK_GRAY);
        COLOR_MAP.put("blue", ChatFormatting.BLUE);
        COLOR_MAP.put("green", ChatFormatting.GREEN);
        COLOR_MAP.put("aqua", ChatFormatting.AQUA);
        COLOR_MAP.put("red", ChatFormatting.RED);
        COLOR_MAP.put("light_purple", ChatFormatting.LIGHT_PURPLE);
        COLOR_MAP.put("yellow", ChatFormatting.YELLOW);
        COLOR_MAP.put("white", ChatFormatting.WHITE);
    }

    static {
        DECORATION_MAP.put("bold", ChatFormatting.BOLD);
        DECORATION_MAP.put("italic", ChatFormatting.ITALIC);
        DECORATION_MAP.put("underlined", ChatFormatting.UNDERLINE);
        DECORATION_MAP.put("strikethrough", ChatFormatting.STRIKETHROUGH);
        DECORATION_MAP.put("obfuscated", ChatFormatting.OBFUSCATED);
    }

    /**
     * 解析 MiniMessage 字符串为 Component
     */
    public static Component parse(String input) {
        MutableComponent root = Component.literal("");
        Stack<StyleContext> styleStack = new Stack<>();
        StyleContext currentStyle = new StyleContext();
        styleStack.push(currentStyle);

        StringBuilder textBuffer = new StringBuilder();
        int pos = 0;
        int length = input.length();

        while (pos < length) {
            char c = input.charAt(pos);

            if (c == '<') {
                // 先处理已积累的文本
                if (textBuffer.length() > 0) {
                    appendText(root, textBuffer.toString(), currentStyle);
                    textBuffer.setLength(0);
                }

                // 查找标签结束位置
                int endTag = findClosingTag(input, pos);
                if (endTag == -1) {
                    // 没有找到闭合标签，当作普通文本处理
                    textBuffer.append(c);
                    pos++;
                    continue;
                }

                String tagContent = input.substring(pos, endTag + 1);
                Matcher matcher = TAG_PATTERN.matcher(tagContent);

                if (matcher.matches()) {
                    boolean isClosing = !matcher.group(1).isEmpty();
                    String tagName = matcher.group(2).toLowerCase(Locale.ROOT);
                    String params = matcher.group(3);
                    String value = matcher.group(4);

                    if (isClosing) {
                        // 关闭标签 - 只关闭对应的开始标签
                        String expectedOpenTag = matcher.group(2).toLowerCase(Locale.ROOT);

                        // 查找对应的开始标签并关闭
                        if (styleStack.size() > 1) {
                            // 从栈顶向下查找匹配的开始标签
                            int index = styleStack.size() - 1;
                            boolean found = false;

                            while (index >= 0) {
                                StyleContext ctx = styleStack.get(index);
                                if (ctx.tagName != null && ctx.tagName.equals(expectedOpenTag)) {
                                    // 找到匹配的开始标签，关闭到这一层
                                    while (styleStack.size() > index + 1) {
                                        styleStack.pop();
                                    }
                                    currentStyle = styleStack.peek();
                                    found = true;
                                    break;
                                }
                                index--;
                            }

                            if (!found) {
                                // 没找到匹配的标签，忽略此关闭标签
                            }
                        }
                    } else {
                        // 处理各种标签
                        StyleContext newStyle = currentStyle.copy();

                        switch (tagName) {
                            case "color":
                                handleColorTag(newStyle, params, value);
                                break;
                            case "shadow":
                                handleShadowTag(newStyle, params, value);
                                break;
                            case "decoration":
                                handleDecorationTag(newStyle, params, value);
                                break;
                            case "reset":
                                newStyle = new StyleContext();
                                break;
                            case "rainbow":
                                handleRainbowTag(newStyle, params, value);
                                break;
                            case "gradient":
                                handleGradientTag(newStyle, params, value);
                                break;
                            case "transition":
                                handleTransitionTag(newStyle, params, value);
                                break;
                            case "font":
                                handleFontTag(newStyle, params, value);
                                break;
                            default:
                                // 未知标签，忽略
                                break;
                        }

                        // 记录标签名称以便关闭时匹配
                        newStyle.tagName = tagName;
                        styleStack.push(newStyle);
                        currentStyle = newStyle;
                    }
                } else {
                    // 无效标签，当作普通文本
                    textBuffer.append(tagContent);
                }

                pos = endTag + 1;
            } else {
                textBuffer.append(c);
                pos++;
            }
        }

        // 处理剩余的文本
        if (textBuffer.length() > 0) {
            appendText(root, textBuffer.toString(), currentStyle);
        }

        return root;
    }

    /**
     * 处理颜色标签
     * 支持格式: <color:red>, <color:#FF0000>, <color(rgb):255,0,0>
     */
    private static void handleColorTag(StyleContext style, String params, String value) {
        if (value != null) {
            // 格式: <color:red> 或 <color:#FF0000>
            TextColor textColor = parseColor(value);
            if (textColor != null) {
                style.color = textColor;
                // 清除渐变、过渡和彩虹效果
                style.gradient = false;
                style.transition = false;
                style.rainbow = false;
            }
        } else if (params != null) {
            // 格式: <color(rgb):255,0,0>
            if (params.equalsIgnoreCase("rgb")) {
                String[] parts = value.split(",");
                if (parts.length == 3) {
                    try {
                        int r = Integer.parseInt(parts[0].trim());
                        int g = Integer.parseInt(parts[1].trim());
                        int b = Integer.parseInt(parts[2].trim());
                        style.color = TextColor.fromRgb((r << 16) | (g << 8) | b);
                        // 清除渐变、过渡和彩虹效果
                        style.gradient = false;
                        style.transition = false;
                        style.rainbow = false;
                    } catch (NumberFormatException e) {
                        // 忽略解析错误
                    }
                }
            }
        }
    }

    /**
     * 处理阴影标签
     * 支持格式: <shadow>, <shadow:true>, <shadow:false>, <shadow:#FF0000>
     */
    private static void handleShadowTag(StyleContext style, String params, String value) {
        if (value == null) {
            style.shadow = true;
        } else if (value.equalsIgnoreCase("true")) {
            style.shadow = true;
        } else if (value.equalsIgnoreCase("false")) {
            style.shadow = false;
        } else {
            // 自定义阴影颜色
            TextColor shadowColor = parseColor(value);
            if (shadowColor != null) {
                style.shadow = true;
                style.shadowColor = shadowColor;
            }
        }
    }

    /**
     * 处理装饰标签
     * 支持格式: <decoration:bold>, <decoration:bold:true>, <decoration:bold:false>
     */
    private static void handleDecorationTag(StyleContext style, String params, String value) {
        if (params == null) return;

        String[] parts = params.split(":");
        String decorationName = parts[0].toLowerCase(Locale.ROOT);
        boolean enabled = true;

        if (parts.length > 1) {
            enabled = Boolean.parseBoolean(parts[1]);
        } else if (value != null) {
            enabled = Boolean.parseBoolean(value);
        }

        ChatFormatting formatting = DECORATION_MAP.get(decorationName);
        if (formatting != null) {
            style.decorations.put(formatting, enabled);
        }
    }

    /**
     * 处理彩虹色标签
     * 支持格式: <rainbow>, <rainbow:!>, <rainbow(saturation):0.8>, <rainbow(brightness):0.5>
     */
    private static void handleRainbowTag(StyleContext style, String params, String value) {
        float saturation = 1.0f;
        float brightness = 1.0f;

        if (params != null) {
            if (params.equalsIgnoreCase("saturation") && value != null) {
                try {
                    saturation = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                }
            } else if (params.equalsIgnoreCase("brightness") && value != null) {
                try {
                    brightness = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                }
            }
        }

        style.rainbow = true;
        style.gradient = false;
        style.transition = false;
        style.rainbowSaturation = saturation;
        style.rainbowBrightness = brightness;
    }

    /**
     * 处理渐变色标签
     * 支持格式: <gradient:#FF0000:#00FF00>, <gradient(rgb):255,0,0:0,255,0>
     * 渐变是逐字符过渡
     */
    private static void handleGradientTag(StyleContext style, String params, String value) {
        if (value == null) return;

        String[] colors = value.split(":");
        if (colors.length == 2) {
            TextColor start = parseColor(colors[0]);
            TextColor end = parseColor(colors[1]);

            if (start != null && end != null) {
                style.gradient = true;
                style.transition = false;
                style.rainbow = false;
                style.gradientStart = start;
                style.gradientEnd = end;
            }
        } else if (params != null && params.equalsIgnoreCase("rgb")) {
            // 处理 RGB 格式
            String[] color1 = colors[0].split(",");
            String[] color2 = colors[1].split(",");
            if (color1.length == 3 && color2.length == 3) {
                try {
                    int r1 = Integer.parseInt(color1[0].trim());
                    int g1 = Integer.parseInt(color1[1].trim());
                    int b1 = Integer.parseInt(color1[2].trim());
                    int r2 = Integer.parseInt(color2[0].trim());
                    int g2 = Integer.parseInt(color2[1].trim());
                    int b2 = Integer.parseInt(color2[2].trim());

                    style.gradient = true;
                    style.transition = false;
                    style.rainbow = false;
                    style.gradientStart = TextColor.fromRgb((r1 << 16) | (g1 << 8) | b1);
                    style.gradientEnd = TextColor.fromRgb((r2 << 16) | (g2 << 8) | b2);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    /**
     * 处理过渡色标签
     * 支持格式: <transition:#FF0000:#00FF00>, <transition(rgb):255,0,0:0,255,0>
     * 过渡是整个文本块的颜色过渡（类似整体渐变效果）
     */
    private static void handleTransitionTag(StyleContext style, String params, String value) {
        if (value == null) return;

        String[] colors = value.split(":");
        if (colors.length == 2) {
            TextColor start = parseColor(colors[0]);
            TextColor end = parseColor(colors[1]);

            if (start != null && end != null) {
                style.transition = true;
                style.gradient = false;
                style.rainbow = false;
                style.transitionStart = start;
                style.transitionEnd = end;
            }
        } else if (params != null && params.equalsIgnoreCase("rgb")) {
            // 处理 RGB 格式
            String[] color1 = colors[0].split(",");
            String[] color2 = colors[1].split(",");
            if (color1.length == 3 && color2.length == 3) {
                try {
                    int r1 = Integer.parseInt(color1[0].trim());
                    int g1 = Integer.parseInt(color1[1].trim());
                    int b1 = Integer.parseInt(color1[2].trim());
                    int r2 = Integer.parseInt(color2[0].trim());
                    int g2 = Integer.parseInt(color2[1].trim());
                    int b2 = Integer.parseInt(color2[2].trim());

                    style.transition = true;
                    style.gradient = false;
                    style.rainbow = false;
                    style.transitionStart = TextColor.fromRgb((r1 << 16) | (g1 << 8) | b1);
                    style.transitionEnd = TextColor.fromRgb((r2 << 16) | (g2 << 8) | b2);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    /**
     * 处理字体标签
     * 支持格式: <font:minecraft:default>, <font:minecraft:uniform>
     */
    private static void handleFontTag(StyleContext style, String params, String value) {
        String fontName = value != null ? value : params;
        if (fontName != null) {
            style.font = fontName;
        }
    }

    /**
     * 解析颜色字符串
     * 支持: 颜色名称, #RRGGBB, #RGB, 十六进制数值
     */
    private static TextColor parseColor(String colorStr) {
        if (colorStr == null) return null;

        // 检查是否为预定义颜色名称
        ChatFormatting formatting = COLOR_MAP.get(colorStr.toLowerCase(Locale.ROOT));
        if (formatting != null) {
            return TextColor.fromLegacyFormat(formatting);
        }

        // 检查十六进制格式
        if (colorStr.startsWith("#")) {
            try {
                if (colorStr.length() == 7) {
                    return TextColor.parseColor(colorStr).getOrThrow();
                } else if (colorStr.length() == 4) {
                    // 扩展 #RGB 到 #RRGGBB
                    String expanded = "#" +
                            colorStr.charAt(1) + colorStr.charAt(1) +
                            colorStr.charAt(2) + colorStr.charAt(2) +
                            colorStr.charAt(3) + colorStr.charAt(3);
                    return TextColor.parseColor(expanded).getOrThrow();
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return null;
    }

    /**
     * 查找标签闭合位置
     */
    private static int findClosingTag(String input, int start) {
        int pos = start + 1;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '>') {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    /**
     * 添加文本到组件
     */
    private static void appendText(MutableComponent root, String text, StyleContext styleContext) {
        if (text.isEmpty()) return;

        // 如果有渐变效果，需要逐字符处理
        if (styleContext.gradient && styleContext.gradientStart != null && styleContext.gradientEnd != null) {
            appendGradientText(root, text, styleContext);
        }
        // 如果有过渡效果，需要逐字符处理
        else if (styleContext.transition && styleContext.transitionStart != null && styleContext.transitionEnd != null) {
            appendTransitionText(root, text, styleContext);
        }
        // 如果有彩虹效果，需要逐字符处理
        else if (styleContext.rainbow) {
            appendRainbowText(root, text, styleContext);
        }
        // 普通文本
        else {
            MutableComponent component = Component.literal(text);
            component.setStyle(styleContext.buildStyle());
            root.append(component);
        }
    }

    /**
     * 带渐变效果的文本追加（逐字符颜色变化）
     */
    private static void appendGradientText(MutableComponent root, String text, StyleContext styleContext) {
        int length = text.length();
        Style baseStyle = styleContext.buildBaseStyle();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            float ratio = (float) i / (length - 1);
            TextColor gradientColor = interpolateColor(styleContext.gradientStart, styleContext.gradientEnd, ratio);

            Style charStyle = baseStyle.withColor(gradientColor);
            // 重新应用阴影
            if (styleContext.shadow != null && styleContext.shadow) {
                if (styleContext.shadowColor != null) {
                    charStyle = charStyle.withShadowColor(styleContext.shadowColor.getValue() | 0xFF000000);
                } else {
                    charStyle = charStyle.withShadowColor(0xFF000000);
                }
            }

            root.append(Component.literal(String.valueOf(c)).withStyle(charStyle));
        }
    }

    /**
     * 带过渡效果的文本追加（整体颜色过渡）
     */
    private static void appendTransitionText(MutableComponent root, String text, StyleContext styleContext) {
        Style baseStyle = styleContext.buildBaseStyle();

        // 过渡效果：为整个文本块创建一个颜色过渡
        MutableComponent transitionComponent = Component.literal(text);

        // 应用过渡样式（这里使用颜色插值，但也可以根据需求调整）
        if (styleContext.transitionStart != null && styleContext.transitionEnd != null) {
            // 可以使用整体渐变或者固定起始色
            transitionComponent.setStyle(baseStyle.withColor(styleContext.transitionStart));
        } else {
            transitionComponent.setStyle(baseStyle);
        }

        root.append(transitionComponent);
    }

    /**
     * 带彩虹效果的文本追加
     */
    private static void appendRainbowText(MutableComponent root, String text, StyleContext styleContext) {
        int length = text.length();
        Style baseStyle = styleContext.buildBaseStyle();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            float hue = (float) i / length;
            int rgb = java.awt.Color.HSBtoRGB(hue, styleContext.rainbowSaturation, styleContext.rainbowBrightness);
            TextColor rainbowColor = TextColor.fromRgb(rgb & 0xFFFFFF);

            Style charStyle = baseStyle.withColor(rainbowColor);
            // 重新应用阴影
            if (styleContext.shadow != null && styleContext.shadow) {
                if (styleContext.shadowColor != null) {
                    charStyle = charStyle.withShadowColor(styleContext.shadowColor.getValue() | 0xFF000000);
                } else {
                    charStyle = charStyle.withShadowColor(0xFF000000);
                }
            }

            root.append(Component.literal(String.valueOf(c)).withStyle(charStyle));
        }
    }

    /**
     * 颜色插值
     */
    private static TextColor interpolateColor(TextColor start, TextColor end, float ratio) {
        int startRgb = start.getValue();
        int endRgb = end.getValue();

        int r1 = (startRgb >> 16) & 0xFF;
        int g1 = (startRgb >> 8) & 0xFF;
        int b1 = startRgb & 0xFF;

        int r2 = (endRgb >> 16) & 0xFF;
        int g2 = (endRgb >> 8) & 0xFF;
        int b2 = endRgb & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return TextColor.fromRgb((r << 16) | (g << 8) | b);
    }

    /**
     * 样式上下文类，用于追踪嵌套样式
     */
    private static class StyleContext {
        String tagName = null; // 记录标签名称，用于关闭匹配
        TextColor color = null;
        Boolean shadow = null;
        TextColor shadowColor = null;
        Map<ChatFormatting, Boolean> decorations = new HashMap<>();
        boolean rainbow = false;
        float rainbowSaturation = 1.0f;
        float rainbowBrightness = 1.0f;
        boolean gradient = false;
        TextColor gradientStart = null;
        TextColor gradientEnd = null;
        boolean transition = false;
        TextColor transitionStart = null;
        TextColor transitionEnd = null;
        String font = null;

        StyleContext copy() {
            StyleContext copy = new StyleContext();
            copy.tagName = this.tagName;
            copy.color = this.color;
            copy.shadow = this.shadow;
            copy.shadowColor = this.shadowColor;
            copy.decorations.putAll(this.decorations);
            copy.rainbow = this.rainbow;
            copy.rainbowSaturation = this.rainbowSaturation;
            copy.rainbowBrightness = this.rainbowBrightness;
            copy.gradient = this.gradient;
            copy.gradientStart = this.gradientStart;
            copy.gradientEnd = this.gradientEnd;
            copy.transition = this.transition;
            copy.transitionStart = this.transitionStart;
            copy.transitionEnd = this.transitionEnd;
            copy.font = this.font;
            return copy;
        }

        /**
         * 构建基础样式（不含颜色）
         */
        Style buildBaseStyle() {
            Style style = Style.EMPTY;

            // 应用装饰器
            for (Map.Entry<ChatFormatting, Boolean> entry : decorations.entrySet()) {
                if (entry.getValue()) {
                    style = style.applyFormat(entry.getKey());
                }
            }

            if (font != null) {
                try {
                    style = style.withFont(new FontDescription.Resource(Identifier.parse(font)));
                } catch (Exception e) {
                    // 忽略字体解析错误
                }
            }

            return style;
        }

        /**
         * 构建完整样式（含颜色）
         */
        Style buildStyle() {
            Style style = buildBaseStyle();

            // 只有在没有渐变、过渡和彩虹效果时才应用普通颜色
            if (!gradient && !transition && !rainbow && color != null) {
                style = style.withColor(color);
            }

            if (shadow != null && shadow) {
                if (shadowColor != null) {
                    style = style.withShadowColor(shadowColor.getValue() | 0xFF000000);
                } else {
                    style = style.withShadowColor(0xFF000000);
                }
            }

            return style;
        }
    }
}