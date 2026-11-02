package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicCore;
import com.coloryr.allmusic.client.core.CommandMusicClient;import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * NeoForge 的 /music_client 指令壳
 * <p>
 * 只负责注册指令（包括 Tab 补全用的子指令节点），具体逻辑都在通用的 {@link CommandMusicClient} 里
 */
@EventBusSubscriber(modid = AllMusicClient.MODID, value = Dist.CLIENT)
public final class CommandMusicClientNeoForge {
    private CommandMusicClientNeoForge() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal(CommandMusicClient.NAME)
                .executes(context -> CommandMusicClient.run(new String[0]))
                .then(Commands.literal(CommandMusicClient.ARG_HELP)
                        .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_HELP})))
                .then(Commands.literal(CommandMusicClient.ARG_INFO)
                        .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_INFO})))
                .then(Commands.literal(CommandMusicClient.ARG_TEST)
                        .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_TEST})))
                .then(Commands.literal(CommandMusicClient.ARG_IMAGE_SIZE)
                        .executes(context -> CommandMusicClient.run(new String[]{CommandMusicClient.ARG_IMAGE_SIZE}))
                        .then(Commands.argument("size", IntegerArgumentType.integer(AllMusicCore.MIN_PIC_SIZE, AllMusicCore.MAX_PIC_SIZE))
                                .executes(context -> CommandMusicClient.run(new String[]{
                                        CommandMusicClient.ARG_IMAGE_SIZE,
                                        Integer.toString(IntegerArgumentType.getInteger(context, "size"))})))));
    }
}
