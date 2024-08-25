package com.zurkuviirs.spinbot;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimeD implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("timewarp").executes(context -> {
			context.getSource().sendFeedback(() -> Text.literal("Please specify end-time morning/noon/evening/night"), true);
			return 1;

		})
						.then(CommandManager.literal("Morning").executes(TimeD::TimeWarp))
		));
	}

	private static int TimeWarp(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();
		int currentTime = Math.toIntExact(world.getTimeOfDay());
		int ticksToNextMorning = (24000 - currentTime) % 24000;

		try {
			executeSprint(source, ticksToNextMorning);
			context.getSource().sendFeedback(() -> Text.literal("Warp success!"), false);
		} catch (Exception e) {
			context.getSource().sendFeedback(() -> Text.literal("Error during warp: " + e.getMessage()), false);
		}

		//executeSprint(source, (24000-currentTime)); //works! But fix bug where you cant time warp right after the first time warp finishes.

		return 1;
	}

	private static int executeSprint(ServerCommandSource source, int tick) {
		//if statement for when I add more times
		source.getServer().getTickManager().startSprint(tick);
		return 0;
	}

}

//Morning 0t
//Noon 4000t
//Evening 13000t
//Night 16000t

//meow