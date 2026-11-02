package com.coloryr.allmusic.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * NeoForge 的 /music_client 指令壳
 * <p>
 * 只负责注册指令，具体逻辑都在通用的 {@link CommandMusicClient} 里
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
                .then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(context -> CommandMusicClient.run(
                                split(StringArgumentType.getString(context, "args"))))));
    }

    private static String[] split(String args) {
        String text = args == null ? "" : args.trim();
        if (text.isEmpty()) {
            return new String[0];
        }
        return text.split("\\s+");
    }
}
