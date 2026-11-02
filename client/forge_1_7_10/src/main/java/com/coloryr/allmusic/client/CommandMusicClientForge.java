package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.CommandMusicClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

/**
 * 1.7.10 的 /music_client 指令壳
 * <p>
 * 只负责把 {@link CommandMusicClient#execute(String[])} 的结果发到聊天栏，
 * 具体逻辑都在通用的 {@link CommandMusicClient} 里
 */
public class CommandMusicClientForge implements ICommand {
    @Override
    public String getCommandName() {
        return CommandMusicClient.NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return CommandMusicClient.usage();
    }

    @Override
    public List getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        CommandMusicClient.Result result = CommandMusicClient.execute(args);

        if (result.title != null) {
            send(sender, result.title, EnumChatFormatting.YELLOW);
        }
        for (String line : result.lines) {
            send(sender, line, color(CommandMusicClient.styleOf(line)));
        }
    }

    private static EnumChatFormatting color(int style) {
        switch (style) {
            case CommandMusicClient.STYLE_OK:
                return EnumChatFormatting.GREEN;
            case CommandMusicClient.STYLE_ERROR:
                return EnumChatFormatting.RED;
            default:
                return null;
        }
    }

    private static void send(ICommandSender sender, String message, EnumChatFormatting color) {
        ChatComponentText text = new ChatComponentText("[AllMusic Client] " + message);
        if (color != null) {
            text.getChatStyle().setColor(color);
        }
        sender.addChatMessage(text);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandMusicClient.SUB_COMMANDS);
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
