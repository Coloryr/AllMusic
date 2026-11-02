package com.coloryr.allmusic.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.commands.Commands;

/**
 * Fabric（1.19.3+）的 /music_client 指令壳
 * <p>
 * 只负责注册指令，具体逻辑都在通用的 {@link CommandMusicClient} 里
 */
public final class CommandMusicClientFabric {
    private CommandMusicClientFabric() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(Commands.literal(CommandMusicClient.NAME)
                        .executes(context -> CommandMusicClient.run(new String[0]))
                        .then(Commands.argument("args", StringArgumentType.greedyString())
                                .executes(context -> CommandMusicClient.run(
                                        split(StringArgumentType.getString(context, "args")))))));
    }

    private static String[] split(String args) {
        String text = args == null ? "" : args.trim();
        if (text.isEmpty()) {
            return new String[0];
        }
        return text.split("\\s+");
    }
}
