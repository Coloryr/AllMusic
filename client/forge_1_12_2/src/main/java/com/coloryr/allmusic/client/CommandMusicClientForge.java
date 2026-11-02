package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.CommandMusicClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

/**
 * 1.12.2 的 /music_client 指令壳
 * <p>
 * 只负责注册指令，具体逻辑都在通用的 {@link CommandMusicClient} 里
 */
public class CommandMusicClientForge extends CommandBase {
    @Override
    public String getName() {
        return CommandMusicClient.NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return CommandMusicClient.usage();
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        CommandMusicClient.run(args);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length <= 1) {
            return getListOfStringsMatchingLastWord(args, CommandMusicClient.SUB_COMMANDS);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
