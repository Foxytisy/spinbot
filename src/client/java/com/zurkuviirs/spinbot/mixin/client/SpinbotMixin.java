package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.client.SpinbotClient;
import net.fabricmc.api.EnvType;import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class SpinbotMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Unique
    private long lastTime = System.currentTimeMillis();

    @Inject(method = "runTick", at = @At("TAIL"))
    public void renderInjected(CallbackInfo ci) {
        final var deltaTime = System.currentTimeMillis() - lastTime;
        final var p = this.player;
        if (p != null && SpinbotClient.getInstance().spinToggle) {
            var increment = (deltaTime / 1000f) * SpinbotClient.getInstance().spinAmount;
            var incrementVert = (deltaTime / 1000f) * SpinbotClient.getInstance().spinAmountVert;
            if (SpinbotClient.getInstance().spinEnable) {
                p.setYRot(p.getYRot() + increment);
            }
            if (SpinbotClient.getInstance().spinVertEnable) {
                p.setXRot(p.getXRot() - incrementVert);
            }
            if (SpinbotClient.getInstance().spinRampEnable) {
                var incrementRamp = (deltaTime / 1000f) * SpinbotClient.getInstance().currentRampSpeed;
                if (SpinbotClient.getInstance().spinAmount > 0 && !SpinbotClient.getInstance().spinRampFinish) {
                    if (SpinbotClient.getInstance().currentRampSpeed <= SpinbotClient.getInstance().spinAmount) {
                        SpinbotClient.getInstance().currentRampSpeed = (SpinbotClient.getInstance().currentRampSpeed + (SpinbotClient.getInstance().spinRampAmount));
                        player.setYRot(player.getYRot() + incrementRamp);
                    } else {
                        SpinbotClient.getInstance().spinRampFinish = true;
                        player.setYRot(player.getYRot() + increment);
                    }
                    //if(spinbot.getInstance().soundEnable) {
                    //    if (incrementRamp % 1 == 0) {
                    //        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), .9f, incrementRamp/5);
                    //    }
                    //}
                } else {
                    if (SpinbotClient.getInstance().currentRampSpeed >= SpinbotClient.getInstance().spinAmount) {
                        SpinbotClient.getInstance().currentRampSpeed = (SpinbotClient.getInstance().currentRampSpeed - (SpinbotClient.getInstance().spinRampAmount));
                        player.setYRot(player.getYRot() + incrementRamp);
                    } else {
                        SpinbotClient.getInstance().spinRampFinish = true;
                        player.setYRot(player.getYRot() + increment);
                    }
                }
                //player.sendMessage(Text.literal(incrementRamp + " expected: " + increment));
            }
            if (SpinbotClient.getInstance().spinRampVertEnable) {
                var incrementRamp = (deltaTime / 1000f) * SpinbotClient.getInstance().currentRampSpeed;
                if (SpinbotClient.getInstance().spinAmount > 0 && !SpinbotClient.getInstance().spinRampFinish) {
                    if (SpinbotClient.getInstance().currentRampSpeed <= SpinbotClient.getInstance().spinAmount) {
                        SpinbotClient.getInstance().currentRampSpeed = (SpinbotClient.getInstance().currentRampSpeed + (SpinbotClient.getInstance().spinRampAmount));
                        player.setXRot(player.getXRot() - incrementRamp);
                    } else {
                        SpinbotClient.getInstance().spinRampFinish = true;
                        player.setXRot(player.getXRot() + increment);
                    }
                } else {
                    if (SpinbotClient.getInstance().currentRampSpeed >= SpinbotClient.getInstance().spinAmount) {
                        SpinbotClient.getInstance().currentRampSpeed = (SpinbotClient.getInstance().currentRampSpeed - (SpinbotClient.getInstance().spinRampAmount));
                        player.setXRot(player.getXRot() - incrementRamp);
                    } else {
                        SpinbotClient.getInstance().spinRampFinish = true;
                        player.setXRot(player.getXRot() - increment);
                    }
                }
            }
            if (SpinbotClient.getInstance().angleSpinEnable) {
                var spinAngle = SpinbotClient.getInstance().spinAngle;
                var currentYaw = SpinbotClient.getInstance().currentYaw;

                if (spinAngle < 0) {
                    if ((currentYaw + spinAngle < p.getYRot())) {
                        p.setYRot(p.getYRot() - increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        SpinbotClient.getInstance().angleSpinEnable = false;
                        if (SpinbotClient.getInstance().soundEnable) {
                            player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                } else {
                    if ((currentYaw + spinAngle > p.getYRot())) {
                        p.setYRot(p.getYRot() + increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        SpinbotClient.getInstance().angleSpinEnable = false;
                        if (SpinbotClient.getInstance().soundEnable) {
                            player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                }
            }
            if (SpinbotClient.getInstance().oscSpinEnable) {
                var spinAngle = SpinbotClient.getInstance().spinAngle;
                var currentYaw = SpinbotClient.getInstance().currentYaw;

                if ((currentYaw + spinAngle) > player.getYRot() && !SpinbotClient.getInstance().spinBack) {
                    player.setYRot(player.getYRot() + increment);
                } else {
                    SpinbotClient.getInstance().spinBack = true;
                    player.setYRot(player.getYRot() - increment);
                }
                if (SpinbotClient.getInstance().spinBack && (currentYaw - spinAngle) > player.getYRot()) {
                    SpinbotClient.getInstance().spinBack = false;
                }
                if (SpinbotClient.getInstance().soundEnable) {
                    if (SpinbotClient.getInstance().spinBack && !SpinbotClient.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, 1f);
                        SpinbotClient.getInstance().oscSwitch = true;
                    } else if (!SpinbotClient.getInstance().spinBack && SpinbotClient.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, .8f);
                        SpinbotClient.getInstance().oscSwitch = false;
                    }
                }
            }
            if (SpinbotClient.getInstance().oscSpinVertEnable) {
                var spinAngleVert = SpinbotClient.getInstance().spinAngleVert;
                var currentPitch = SpinbotClient.getInstance().currentPitch;

                if ((currentPitch + spinAngleVert) > player.getXRot() && !SpinbotClient.getInstance().spinBack) {
                    player.setXRot(player.getXRot() + incrementVert);
                } else {
                    SpinbotClient.getInstance().spinBack = true;
                    player.setXRot(player.getXRot() - incrementVert);
                }
                if (SpinbotClient.getInstance().spinBack && (currentPitch - spinAngleVert) > player.getXRot()) {
                    SpinbotClient.getInstance().spinBack = false;
                }
                if (SpinbotClient.getInstance().soundEnable) {
                    if (SpinbotClient.getInstance().spinBack && !SpinbotClient.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, 1.1f);
                        SpinbotClient.getInstance().oscSwitch = true;
                    } else if (!SpinbotClient.getInstance().spinBack && SpinbotClient.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(SpinbotClient.getInstance().soundId), 1f, .9f);
                        SpinbotClient.getInstance().oscSwitch = false;
                    }
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }
}
