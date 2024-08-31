package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class SpinbotMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Unique
    private long lastTime = System.currentTimeMillis();

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInjected(CallbackInfo ci) {
        final var deltaTime = System.currentTimeMillis() - lastTime;
        final var p = this.player;
        if (p != null) {
            var increment = (deltaTime / 1000f) * spinbot.getInstance().spinAmount;
            var incrementVert = (deltaTime / 1000f) * spinbot.getInstance().spinAmountVert;
            if (spinbot.getInstance().spinEnable) {
                p.setYaw(p.getYaw() + increment);
            }
            if (spinbot.getInstance().spinVertEnable) {
                p.setPitch(p.getPitch() - incrementVert);
            }
            if (spinbot.getInstance().spinRampEnable) {
                var incrementRamp = (deltaTime / 1000f) * spinbot.getInstance().currentRampSpeed;
                if (spinbot.getInstance().spinAmount > 0 && !spinbot.getInstance().spinRampFinish) {
                    if (spinbot.getInstance().currentRampSpeed <= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed + (spinbot.getInstance().spinRampAmount));
                        player.setYaw(player.getYaw() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setYaw(player.getYaw() + increment);
                    }
                    //if(spinbot.getInstance().soundEnable) {
                    //    if (incrementRamp % 1 == 0) {
                    //        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), .9f, incrementRamp/5);
                    //    }
                    //}
                } else {
                    if (spinbot.getInstance().currentRampSpeed >= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed - (spinbot.getInstance().spinRampAmount));
                        player.setYaw(player.getYaw() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setYaw(player.getYaw() + increment);
                    }
                }
                //player.sendMessage(Text.literal(incrementRamp + " expected: " + increment));
            }
            if (spinbot.getInstance().spinRampVertEnable) {
                var incrementRamp = (deltaTime / 1000f) * spinbot.getInstance().currentRampSpeed;
                if (spinbot.getInstance().spinAmount > 0 && !spinbot.getInstance().spinRampFinish) {
                    if (spinbot.getInstance().currentRampSpeed <= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed + (spinbot.getInstance().spinRampAmount));
                        player.setPitch(player.getPitch() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setPitch(player.getPitch() + increment);
                    }
                    //if(spinbot.getInstance().soundEnable) {
                    //    if (incrementRamp % 1 == 0) {
                    //        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), .9f, incrementRamp/5);
                    //    }
                    //}
                } else {
                    if (spinbot.getInstance().currentRampSpeed >= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed - (spinbot.getInstance().spinRampAmount));
                        player.setPitch(player.getPitch() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setPitch(player.getPitch() + increment);
                    }
                }
                //player.sendMessage(Text.literal(incrementRamp + " expected: " + increment));
            }
            if (spinbot.getInstance().angleSpinEnable) {
                var spinAngle = spinbot.getInstance().spinAngle;
                var currentYaw = spinbot.getInstance().currentYaw;

                if (spinAngle < 0) {
                    if ((currentYaw + spinAngle < p.getYaw())) {
                        p.setYaw(p.getYaw() - increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        spinbot.getInstance().angleSpinEnable = false;
                        if (spinbot.getInstance().soundEnable) {
                            player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                } else {
                    if ((currentYaw + spinAngle > p.getYaw())) {
                        p.setYaw(p.getYaw() + increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        spinbot.getInstance().angleSpinEnable = false;
                        if (spinbot.getInstance().soundEnable) {
                            player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                }
            }
            if (spinbot.getInstance().oscSpinEnable) {
                var spinAngle = spinbot.getInstance().spinAngle;
                var currentYaw = spinbot.getInstance().currentYaw;
                //var spinBack = spinbot.getInstance().spinBack;

                if ((currentYaw + spinAngle) > player.getYaw() && !spinbot.getInstance().spinBack) {
                    player.setYaw(player.getYaw() + increment);
                } else {
                    spinbot.getInstance().spinBack = true;
                    player.setYaw(player.getYaw() - increment);
                }
                if (spinbot.getInstance().spinBack && (currentYaw - spinAngle) > player.getYaw()) {
                    spinbot.getInstance().spinBack = false;
                }
                if (spinbot.getInstance().soundEnable) {
                    if (spinbot.getInstance().spinBack && !spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 1f);
                        spinbot.getInstance().oscSwitch = true;
                    } else if (!spinbot.getInstance().spinBack && spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, .8f);
                        spinbot.getInstance().oscSwitch = false;
                    }
                }
            }
            if (spinbot.getInstance().oscSpinVertEnable) {
                var spinAngleVert = spinbot.getInstance().spinAngleVert;
                var currentPitch = spinbot.getInstance().currentPitch;

                if ((currentPitch + spinAngleVert) > player.getPitch() && !spinbot.getInstance().spinBack) {
                    player.setPitch(player.getPitch() + incrementVert);
                } else {
                    spinbot.getInstance().spinBack = true;
                    player.setPitch(player.getPitch() - incrementVert);
                }
                if (spinbot.getInstance().spinBack && (currentPitch - spinAngleVert) > player.getPitch()) {
                    spinbot.getInstance().spinBack = false;
                }
                if (spinbot.getInstance().soundEnable) {
                    if (spinbot.getInstance().spinBack && !spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, 1.1f);
                        spinbot.getInstance().oscSwitch = true;
                    } else if (!spinbot.getInstance().spinBack && spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), 1f, .9f);
                        spinbot.getInstance().oscSwitch = false;
                    }
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }
}
