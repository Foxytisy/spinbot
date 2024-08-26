//seals

package com.zurkuviirs.spinbot;


import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.connect.Connector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl.get;

public class spinbot implements ClientModInitializer {

    public boolean soundEnable = false;
    public boolean spinEnable = false;
    public boolean angleSpinEnable = false;
    public boolean oscSpinEnable = false;
    public boolean spinBack = false;
    public float spinAmount = 0;
    public float spinAngle = 0;
    public float currentYaw;
    public Identifier soundId = Identifier.of("minecraft:block.note_block.hat");

    private static spinbot instance;

    public static spinbot getInstance() {
        return instance;
    }

    public void onInitializeClient() {
        instance = this;

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spin")
                .then(ClientCommandManager.argument("Speed", FloatArgumentType.floatArg()).executes(context -> {
                    spinEnable = true;
                    spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                        //ClientPlayerEntity player = context.getSource().getPlayer();
                        //assert player != null;
                        //player.setYaw(1);
                        //System.out.println(player.getYaw());

            return 1;
        })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinsound")
                .then(ClientCommandManager.argument("Enable / Disable", StringArgumentType.word()).suggests((context, builder) -> {
                    // Suggest true and false options
                    return CommandSource.suggestMatching(new String[] {"true", "false"}, builder);
                }).executes(context -> {
                    soundEnable = Boolean.parseBoolean(StringArgumentType.getString(context, "Enable / Disable"));

                    return 1;
                })))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinangle")
                .then(ClientCommandManager.argument("Angle (Degrees)", FloatArgumentType.floatArg())
                        .then(ClientCommandManager.argument("Speed (Positive Float)", FloatArgumentType.floatArg()).executes(context -> {
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
                        .then(ClientCommandManager.argument("Speed", FloatArgumentType.floatArg()).executes(context -> {
                            oscSpinEnable = true;
                            spinAmount = FloatArgumentType.getFloat(context, "Speed") / 20.0f;
                            spinAngle = Math.abs(FloatArgumentType.getFloat(context, "Angle (Symetric Degrees)"));
                            currentYaw = context.getSource().getPlayer().getYaw();

                            return 1;
                        }))))));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(literal("spinstop").executes(context -> {
            spinEnable = false;
            angleSpinEnable = false;
            oscSpinEnable = false;
            return 1;
        }))));

        //ClientTickEvents.END_CLIENT_TICK.register(client -> {
        //    if (spinEnable && client.player != null) {
        //        // Calculate the amount to rotate each tick
        //        float increment = spinAmount / 20.0f; // Spread the rotation over multiple ticks
        //        client.player.setYaw(client.player.getYaw() + increment);
        //    }
        //});

        //For dev currently, will change to mixin stuff later
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (oscSpinEnable && client.player != null) {
                float increment = spinAmount / 20.0f;

                if ((currentYaw + spinAngle) > client.player.getYaw() && !spinBack){
                    client.player.setYaw(client.player.getYaw() + increment);
                } else {
                    spinBack = true;
                    client.player.setYaw(client.player.getYaw() - increment);
                }
                if (spinBack && (currentYaw - spinAngle) > client.player.getYaw()) {
                    spinBack = false;
                }

                //client.player.playSound(SoundEvent.of(soundId), 1f, 1f);

                //client.player.playSound(SoundEvent.of(soundId), 1f, 0.8f);


                //REMOVE FOR RELEASE
                client.player.sendMessage(Text.literal(String.valueOf(client.player.getYaw())));
            }
        });

        //ClientTickEvents.END_CLIENT_TICK.register(client -> {
        //    if (angleSpinEnable && client.player != null) {
        //        float increment = Math.abs(spinAmount) / 20.0f; // Spread the rotation over multiple ticks
        //        if (spinAngle < 0){
        //            if ((currentYaw + spinAngle < client.player.getYaw())) {
        //                client.player.setYaw(client.player.getYaw() - increment);
        //                client.player.sendMessage(Text.literal(String.valueOf(currentYaw)));
//
        //            } else {
        //                angleSpinEnable = false;
        //            }
        //        } else {
        //            if ((currentYaw + spinAngle > client.player.getYaw())) {
        //                client.player.setYaw(client.player.getYaw() + increment);
        //                client.player.sendMessage(Text.literal(String.valueOf(currentYaw)));
//
        //            } else {
        //                angleSpinEnable = false;
        //            }
        //        }
        //    }
        //});
    }

}
