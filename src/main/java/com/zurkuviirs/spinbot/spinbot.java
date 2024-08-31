//seals

package com.zurkuviirs.spinbot;


import com.google.gson.Gson;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class spinbot implements ClientModInitializer {

    public boolean soundEnable;
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

    public Identifier soundId = Identifier.of("minecraft:block.note_block.hat");
    private static spinbot instance;
    private static final String CONFIG_FILE_NAME = "config.json";
    private Path configPath;

    public static spinbot getInstance() {
        return instance;
    }

    public void onInitializeClient() {
        instance = this;

        configPath = FabricLoader.getInstance().getConfigDir().resolve("config.json");

        System.out.println("Config path: " + configPath.toString());

        loadConfig();

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spin")
                .then(ClientCommandManager.argument("Speed", FloatArgumentType.floatArg())
                        .then(literal("vert").executes(context -> {
                            vertMin = 999999999999999999999999999.9f;
                            vertMax = 999999999999999999999999999.9f;
                            spinVertEnable = true;
                            spinAmountVert = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                            currentPitch = context.getSource().getPlayer().getPitch();
                            if (spinbot.getInstance().soundEnable) {
                                context.getSource().getPlayer().playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 1.1f);
                            }
                            if (spinVertEnable && oscSpinVertEnable) {
                                context.getSource().getPlayer().sendMessage(Text.literal("woah"));
                            }
                            return 1;
                })).executes(context -> {
                    spinEnable = true;
                    spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                    context.getSource().getPlayer().playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 1f);
            return 1;
        })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinramp")
                .then(ClientCommandManager.argument("Ramp up Speed (Positive Float)", FloatArgumentType.floatArg())
                        .then(ClientCommandManager.argument("Speed (Positive Float)", FloatArgumentType.floatArg()).then(ClientCommandManager.literal("vert").executes(context -> {
                            stopSpin();
                            vertMin = 999999999999999999999999999.9f;
                            vertMax = 999999999999999999999999999.9f;
                            spinRampVertEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                            currentYaw = context.getSource().getPlayer().getYaw();
                            spinRampAmount = FloatArgumentType.getFloat(context, "Ramp up Speed (Positive Float)") / 20.0f;
                            if (soundEnable) {
                                context.getSource().getPlayer().playSound(SoundEvent.of(soundId), 1f, 1.1f);
                            }
                            return 1;
                        })).executes(context -> {
                            stopSpin();
                            spinRampEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                            currentYaw = context.getSource().getPlayer().getYaw();
                            spinRampAmount = FloatArgumentType.getFloat(context, "Ramp up Speed (Positive Float)") / 20.0f;
                            return 1;
                                }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinsound")
                .then(ClientCommandManager.argument("Enable / Disable", StringArgumentType.word()).suggests((context, builder) -> CommandSource.suggestMatching(new String[] {"true", "false"}, builder)).executes(context -> {
                    soundEnable = Boolean.parseBoolean(StringArgumentType.getString(context, "Enable / Disable"));
                    saveConfig();
                    return 1;
                })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinangle")
                .then(ClientCommandManager.argument("Angle (Degrees)", FloatArgumentType.floatArg())
                        .then(ClientCommandManager.argument("Speed (Positive Float)", FloatArgumentType.floatArg()).executes(context -> {
                            stopSpin();
                    angleSpinEnable = true;
                    spinAmount = FloatArgumentType.getFloat(context, "Speed (Positive Float)") / 20.0f;
                    spinAngle = FloatArgumentType.getFloat(context, "Angle (Degrees)");
                    currentYaw = context.getSource().getPlayer().getYaw();
                    if (soundEnable) {
                        context.getSource().getPlayer().playSound(SoundEvent.of(soundId), 1f, 1f);
                    }
                    return 1;
                }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinpong")
                .then(ClientCommandManager.argument("Angle (Symetric Degrees)", FloatArgumentType.floatArg())
                        .then(ClientCommandManager.argument("Speed", FloatArgumentType.floatArg())
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
                                    currentPitch = context.getSource().getPlayer().getPitch();
                                    if (spinEnable && oscSpinVertEnable) {
                                        context.getSource().getPlayer().sendMessage(Text.literal("woah"));
                                    }
                                    return 1;
                        })).executes(context -> {
                                    stopSpin();
                                    oscSpinEnable = true;
                                    spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                                    spinAngle = Math.abs(FloatArgumentType.getFloat(context, "Angle (Symetric Degrees)"));
                                    currentYaw = context.getSource().getPlayer().getYaw();
                                    return 1;
                        }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(literal("spinstop").executes(context -> {
            stopSpin();
            if (soundEnable) {
                context.getSource().getPlayer().playSound(SoundEvent.of(soundId), .9f, .7f);
            }
            return 1;
        }))));
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
        if (oscSpinVertEnable || spinVertEnable || spinRampVertEnable) {
            MinecraftClient.getInstance().player.setPitch(0);
        }
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
