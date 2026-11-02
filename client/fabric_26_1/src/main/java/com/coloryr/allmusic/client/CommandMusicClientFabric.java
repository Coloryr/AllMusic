package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.CommandMusicClient;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

/**
 * Fabric（1.21.11+ / 26.x）的 /music_client 指令壳
 * <p>
 * 新版本 fabric-api 用 {@link ClientCommands} 取代了 ClientCommandManager。
 * 只负责注册指令（包括 Tab 补全用的子指令节点），具体逻辑都在通用的 {@link CommandMusicClient} 里
 */
public final class CommandMusicClientFabric {
    private CommandMusicClientFabric() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommands.literal(CommandMusicClient.NAME)
                        .executes(context -> CommandMusicClient.run(new String[0]))
                        .then(ClientCommands.literal(CommandMusicClient.ARG_HELP)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_HELP})))
                        .then(ClientCommands.literal(CommandMusicClient.ARG_INFO)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_INFO})))
                        .then(ClientCommands.literal(CommandMusicClient.ARG_TEST)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_TEST})))
                        .then(ClientCommands.literal(CommandMusicClient.ARG_IMAGE_SIZE)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_IMAGE_SIZE}))
                                .then(ClientCommands.argument("size", IntegerArgumentType.integer(AllMusicCore.MIN_PIC_SIZE, AllMusicCore.MAX_PIC_SIZE))
                                        .executes(context -> CommandMusicClient.run(new String[]{
                                                CommandMusicClient.ARG_IMAGE_SIZE,
                                                Integer.toString(IntegerArgumentType.getInteger(context, "size"))}))))));
    }
}
