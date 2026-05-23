package com.zurkuviirs.spinbot.client;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.zurkuviirs.spinbot.Spinbot;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.lwjgl.glfw.GLFW;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class SpinbotClient implements ClientModInitializer {
    public boolean soundEnable;
    public boolean spinToggle = true;
    public boolean spinEnable = false;
    public boolean spinVertEnable = false;
    public boolean angleSpinEnable = false;
    public boolean oscSpinEnable = false;
    public boolean oscSpinVertEnable = false;
    public boolean spinRampEnable = false;
    public boolean spinRampVertEnable = false;
    public boolean spinRampFinish = false;
    public boolean spinBack = false;
    public boolean oscSwitch = false;

    public float spinAmount = 0;
    public float spinAmountVert = 0;
    public float spinAngle = 0;
    public float spinAngleVert = 0;
    public float spinRampAmount = 0;
    public float vertMin = 0f;
    public float vertMax = 0f;
    public float currentYaw;
    public float currentPitch;
    public float currentRampSpeed = 0;

    public Identifier soundId = Identifier.parse("minecraft:block.note_block.hat");
    private static KeyMapping spinToggleKeybind;
    private static SpinbotClient instance;
    private static final String CONFIG_FILE_NAME = "config.json";
    private Path configPath;

    public static SpinbotClient getInstance() {
        return instance;
    }

    public void onInitializeClient() {
        instance = this;

        configPath = FabricLoader.getInstance().getConfigDir().resolve("config.json");

        System.out.println("Config path: " + configPath);

        loadConfig();

        spinToggleKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Start/Stop",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                new KeyMapping.Category(Identifier.parse("spinbot"))
        ));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spin")
                .then(argument("Speed", FloatArgumentType.floatArg())
                        .then(literal("vert").executes(context -> {
                            stopSpin();
                            vertMin = 999999999999999999999999999.9f;
                            vertMax = 999999999999999999999999999.9f;
                            spinVertEnable = true;
                            spinAmountVert = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                            currentPitch = context.getSource().getPlayer().getXRot();
                            if (soundEnable) {
                                context.getSource().getPlayer().playSound(SoundEvent.createVariableRangeEvent(soundId), 1f, 1.1f);
                            }
                            if (spinVertEnable && oscSpinVertEnable) {
                                context.getSource().getPlayer().sendSystemMessage(Component.literal("woah"));
                            }
                            return 1;
                        })).executes(context -> {
                            spinEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                            context.getSource().getPlayer().playSound(SoundEvent.createVariableRangeEvent(soundId), 1f, 1f);
                            return 1;
                        })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinramp")
                .then(argument("Ramp up Speed (Positive Float)", FloatArgumentType.floatArg())
                        .then(argument("Speed (Positive Float)", FloatArgumentType.floatArg()).then(literal("vert").executes(context -> {
                            stopSpin();
                            vertMin = 999999999999999999999999999.9f;
                            vertMax = 999999999999999999999999999.9f;
                            spinRampVertEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                            currentYaw = context.getSource().getPlayer().getYRot();
                            spinRampAmount = FloatArgumentType.getFloat(context, "Ramp up Speed (Positive Float)") / 20.0f;
                            if (soundEnable) {
                                context.getSource().getPlayer().playSound(SoundEvent.createVariableRangeEvent(soundId), 1f, 1.1f);
                            }
                            return 1;
                        })).executes(context -> {
                            stopSpin();
                            spinRampEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                            currentYaw = context.getSource().getPlayer().getYRot();
                            spinRampAmount = FloatArgumentType.getFloat(context, "Ramp up Speed (Positive Float)") / 20.0f;
                            return 1;
                        }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinsound")
                .then(argument("Enable / Disable", StringArgumentType.word()).suggests((context, builder) -> SharedSuggestionProvider.suggest(new String[] {"true", "false"}, builder)).executes(context -> {
                    soundEnable = Boolean.parseBoolean(StringArgumentType.getString(context, "Enable / Disable"));
                    saveConfig();
                    return 1;
                })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinangle")
                .then(argument("Angle (Degrees)", FloatArgumentType.floatArg())
                        .then(argument("Speed (Positive Float)", FloatArgumentType.floatArg()).executes(context -> {
                            stopSpin();
                            angleSpinEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                            spinAngle = FloatArgumentType.getFloat(context, "Angle (Degrees)");
                            currentYaw = context.getSource().getPlayer().getYRot();
                            if (soundEnable) {
                                context.getSource().getPlayer().playSound(SoundEvent.createVariableRangeEvent(soundId), 1f, 1f);
                            }
                            return 1;
                        }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinpong")
                .then(argument("Angle (Symetric Degrees)", FloatArgumentType.floatArg())
                        .then(argument("Speed", FloatArgumentType.floatArg())
                                .then(literal("vert")
                                        .executes(context -> {
                                            if(!spinEnable) {
                                                stopSpin();
                                            }
                                            vertMin = 999999999999999999999999999.9f;
                                            vertMax = 999999999999999999999999999.9f;
                                            oscSpinVertEnable = true;
                                            spinAmountVert = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                                            spinAngleVert = Math.abs(FloatArgumentType.getFloat(context, "Angle (Symetric Degrees)"));
                                            currentPitch = context.getSource().getPlayer().getXRot();
                                            if (spinEnable && oscSpinVertEnable) {
                                                context.getSource().getPlayer().sendSystemMessage(Component.literal("woah"));
                                            }
                                            return 1;
                                        })).executes(context -> {
                                    stopSpin();
                                    oscSpinEnable = true;
                                    spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                                    spinAngle = Math.abs(FloatArgumentType.getFloat(context, "Angle (Symetric Degrees)"));
                                    currentYaw = context.getSource().getPlayer().getYRot();
                                    return 1;
                                }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(literal("spinstop").executes(context -> {
            stopSpin();
            if (soundEnable) {
                context.getSource().getPlayer().playSound(SoundEvent.createVariableRangeEvent(soundId), .9f, .7f);
            }
            return 1;
        }))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (spinToggleKeybind.consumeClick()) {
                if(spinEnable || oscSpinEnable || spinRampEnable || spinVertEnable || spinRampVertEnable || angleSpinEnable || oscSpinVertEnable) {
                    if (spinToggle) {
                        spinToggle = false;
                        client.player.sendSystemMessage(Component.literal("Stopped Spinbot!"));
                        if (soundEnable) {
                            client.player.playSound(SoundEvent.createVariableRangeEvent(soundId), .9f, .7f);
                        }
                    } else {
                        spinToggle = true;
                        client.player.sendSystemMessage(Component.literal("Resumed Spinbot!"));
                        if (soundEnable) {
                            client.player.playSound(SoundEvent.createVariableRangeEvent(soundId), .9f, 1f);
                        }
                    }
                }
            }
        });
    }

    private void loadConfig() {
        Gson gson = new Gson();

        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                Config config = gson.fromJson(reader, Config.class);
                if (config != null) {
                    this.soundEnable = config.soundEnable;
                    System.out.println("Config loaded successfully: soundEnable = " + this.soundEnable);
                }
            } catch (IOException e) {
                System.err.println("Failed to load config file.");
                e.printStackTrace();
            }
        } else {
            System.err.println("Config file not found, creating a new one with default values.");
            saveConfig();
        }
    }

    private void saveConfig() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            Config config = new Config();
            config.soundEnable = this.soundEnable;
            gson.toJson(config, writer);
            System.out.println("Config file saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save config file.");
            e.printStackTrace();
        }
    }

    private static class Config {
        boolean soundEnable;
        Config() {
        }
    }

    void stopSpin() {
        if (Minecraft.getInstance().player == null) return;
        if (oscSpinVertEnable || spinVertEnable || spinRampVertEnable) {
            Minecraft.getInstance().player.setXRot(0);
        }
        spinToggle = true;
        vertMin = 0f;
        vertMax = 0f;
        spinAngle = 0;
        spinEnable = false;
        spinVertEnable = false;
        angleSpinEnable = false;
        oscSpinEnable = false;
        oscSpinVertEnable = false;
        spinRampEnable = false;
        spinRampVertEnable = false;
        currentRampSpeed = 0;
        spinRampFinish = false;
        spinRampAmount = 0;
    }

}