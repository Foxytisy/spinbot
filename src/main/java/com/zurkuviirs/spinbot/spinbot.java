//seals

package com.zurkuviirs.spinbot;


import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.*;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class spinbot implements ClientModInitializer {

    public boolean spinEnable = false;
    //public boolean angleSpinEnable = false;
    public float spinAmount = 0;
    //public float spinAngle = 0;

    public void onInitializeClient(){
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spin")
                .then(ClientCommandManager.argument("How much spin?", FloatArgumentType.floatArg()).executes(context -> {
                    spinEnable = true;
                    spinAmount = FloatArgumentType.getFloat(context, "How much spin?") / 20.0f;
                        //ClientPlayerEntity player = context.getSource().getPlayer();
                        //assert player != null;
                        //player.setYaw(1);
                        //System.out.println(player.getYaw());

            return 1;
        })))))
        ;

        //ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(literal("spinangle")
        //        .then(ClientCommandManager.argument("Angle?", FloatArgumentType.floatArg())
        //                .then(ClientCommandManager.argument("How much spin?", FloatArgumentType.floatArg())).executes(context -> {
        //            angleSpinEnable = true;
        //            spinAmount = FloatArgumentType.getFloat(context, "How much spin?") / 20.0f;
        //            spinAngle = FloatArgumentType.getFloat(context, "Angle?");
        //            //ClientPlayerEntity player = context.getSource().getPlayer();
        //            //assert player != null;
        //            //player.setYaw(1);
        //            //System.out.println(player.getYaw());
//
        //            return 1;
        //        })))))
        //;

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(literal("spinstop").executes(context -> {
            spinEnable = false;
            //angleSpinEnable = false;
            return 1;
        }))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (spinEnable && client.player != null) {
                // Calculate the amount to rotate each tick
                float increment = spinAmount / 20.0f; // Spread the rotation over multiple ticks
                client.player.setYaw(client.player.getYaw() + increment);
                //client.player.sendMessage(Text.literal(String.valueOf(client.player.getYaw())));
            }
        });

       //ClientTickEvents.END_CLIENT_TICK.register(client -> {
       //     if (angleSpinEnable && client.player != null) {
       //         // Calculate the amount to rotate each tick
       //         float increment = spinAmount / 20.0f; // Spread the rotation over multiple ticks
        //        client.player.setYaw(client.player.getYaw() + increment);
           // }
        //});
    }

}
