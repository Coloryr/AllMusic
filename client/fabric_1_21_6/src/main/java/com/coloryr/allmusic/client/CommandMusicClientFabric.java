package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.CommandMusicClient;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

/**
 * Fabric（1.19.3+）的 /music_client 指令壳
 * <p>
 * 只负责注册指令（包括 Tab 补全用的子指令节点），具体逻辑都在通用的 {@link CommandMusicClient} 里。
 * 注意这里必须用 ClientCommandManager 建节点，客户端指令的 source 是 FabricClientCommandSource
 */
public final class CommandMusicClientFabric {
    private CommandMusicClientFabric() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal(CommandMusicClient.NAME)
                        .executes(context -> CommandMusicClient.run(new String[0]))
                        .then(ClientCommandManager.literal(CommandMusicClient.ARG_HELP)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_HELP})))
                        .then(ClientCommandManager.literal(CommandMusicClient.ARG_INFO)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_INFO})))
                        .then(ClientCommandManager.literal(CommandMusicClient.ARG_TEST)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_TEST})))
                        .then(ClientCommandManager.literal(CommandMusicClient.ARG_IMAGE_SIZE)
                                .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_IMAGE_SIZE}))
                                .then(ClientCommandManager.argument("size", IntegerArgumentType.integer(AllMusicCore.MIN_PIC_SIZE, AllMusicCore.MAX_PIC_SIZE))
                                        .executes(context -> CommandMusicClient.run(new String[]{
                                                CommandMusicClient.ARG_IMAGE_SIZE,
                                                Integer.toString(IntegerArgumentType.getInteger(context, "size"))}))))));
    }
}
