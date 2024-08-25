package com.zurkuviirs.spinbot;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.NotNull;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

public class PingPong implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(ClientCommandManager.literal("pingpong").executes(context -> {
				if (MinecraftClient.getInstance().player != null)
					MinecraftClient.getInstance().player.sendMessage(Text.literal("Ping Pong! 🏓"), false);
				return 1;
			}).then(ClientCommandManager.argument("pongablemessage", StringArgumentType.greedyString()).executes(context -> {
				String message = StringArgumentType.getString(context, "pongablemessage");
				final String pongedMessage = getPongedMessage(message);

				//MinecraftClient client = MinecraftClient.getInstance();
				//if (client.player != null) {
				//	client.inGameHud.getChatHud().addMessage(Text.literal((pongedMessage + " :ping_pong:")));
				//}

				if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
					((ChatScreen)MinecraftClient.getInstance().currentScreen).sendMessage((pongedMessage + " 🏓"),true);
				return 1;
			}))
        ));
	}

	private static @NotNull String getPongedMessage(String message) {
		StringBuilder pongedMessageBuilder = new StringBuilder();

		int pongLength = message.length();
		for (int i = 0; i < pongLength / 2; i++) {
			char pongStart = message.charAt(i);
			pongedMessageBuilder.append(pongStart);
			char pongEnd = message.charAt((pongLength - 1) - i);
			pongedMessageBuilder.append(pongEnd);
		}
		if (pongLength % 2 != 0) {
			char middleChar = message.charAt(pongLength / 2);
			pongedMessageBuilder.append(middleChar);
		}

        return pongedMessageBuilder.toString();
	}

}