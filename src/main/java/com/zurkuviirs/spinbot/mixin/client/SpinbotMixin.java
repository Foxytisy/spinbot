package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
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

// TODO(Ravel): can not resolve target class MinecraftClient
// TODO(Ravel): can not resolve target class MinecraftClient
@Mixin(Minecraft.class)
public abstract class SpinbotMixin {

    // TODO(Ravel): Could not determine a single target
// TODO(Ravel): Could not determine a single target
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Unique
    private long lastTime = System.currentTimeMillis();

    // TODO(Ravel): no target class
// TODO(Ravel): no target class
    @Inject(method = "runTick", at = @At("TAIL"))
    public void renderInjected(CallbackInfo ci) {
        final var deltaTime = System.currentTimeMillis() - lastTime;
        final var p = this.player;
        if (p != null && spinbot.getInstance().spinToggle) {
            var increment = (deltaTime / 1000f) * spinbot.getInstance().spinAmount;
            var incrementVert = (deltaTime / 1000f) * spinbot.getInstance().spinAmountVert;
            if (spinbot.getInstance().spinEnable) {
                p.setYRot(p.getYRot() + increment);
            }
            if (spinbot.getInstance().spinVertEnable) {
                p.setXRot(p.getXRot() - incrementVert);
            }
            if (spinbot.getInstance().spinRampEnable) {
                var incrementRamp = (deltaTime / 1000f) * spinbot.getInstance().currentRampSpeed;
                if (spinbot.getInstance().spinAmount > 0 && !spinbot.getInstance().spinRampFinish) {
                    if (spinbot.getInstance().currentRampSpeed <= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed + (spinbot.getInstance().spinRampAmount));
                        player.setYRot(player.getYRot() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setYRot(player.getYRot() + increment);
                    }
                    //if(spinbot.getInstance().soundEnable) {
                    //    if (incrementRamp % 1 == 0) {
                    //        player.playSound(SoundEvent.of(spinbot.getInstance().soundId), .9f, incrementRamp/5);
                    //    }
                    //}
                } else {
                    if (spinbot.getInstance().currentRampSpeed >= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed - (spinbot.getInstance().spinRampAmount));
                        player.setYRot(player.getYRot() + incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setYRot(player.getYRot() + increment);
                    }
                }
                //player.sendMessage(Text.literal(incrementRamp + " expected: " + increment));
            }
            if (spinbot.getInstance().spinRampVertEnable) {
                var incrementRamp = (deltaTime / 1000f) * spinbot.getInstance().currentRampSpeed;
                if (spinbot.getInstance().spinAmount > 0 && !spinbot.getInstance().spinRampFinish) {
                    if (spinbot.getInstance().currentRampSpeed <= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed + (spinbot.getInstance().spinRampAmount));
                        player.setXRot(player.getXRot() - incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setXRot(player.getXRot() + increment);
                    }
                } else {
                    if (spinbot.getInstance().currentRampSpeed >= spinbot.getInstance().spinAmount) {
                        spinbot.getInstance().currentRampSpeed = (spinbot.getInstance().currentRampSpeed - (spinbot.getInstance().spinRampAmount));
                        player.setXRot(player.getXRot() - incrementRamp);
                    } else {
                        spinbot.getInstance().spinRampFinish = true;
                        player.setXRot(player.getXRot() - increment);
                    }
                }
            }
            if (spinbot.getInstance().angleSpinEnable) {
                var spinAngle = spinbot.getInstance().spinAngle;
                var currentYaw = spinbot.getInstance().currentYaw;

                if (spinAngle < 0) {
                    if ((currentYaw + spinAngle < p.getYRot())) {
                        p.setYRot(p.getYRot() - increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        spinbot.getInstance().angleSpinEnable = false;
                        if (spinbot.getInstance().soundEnable) {
                            player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                } else {
                    if ((currentYaw + spinAngle > p.getYRot())) {
                        p.setYRot(p.getYRot() + increment);
                        //p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                    } else {
                        spinbot.getInstance().angleSpinEnable = false;
                        if (spinbot.getInstance().soundEnable) {
                            player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, 0.8f);
                        }
                    }
                }
            }
            if (spinbot.getInstance().oscSpinEnable) {
                var spinAngle = spinbot.getInstance().spinAngle;
                var currentYaw = spinbot.getInstance().currentYaw;

                if ((currentYaw + spinAngle) > player.getYRot() && !spinbot.getInstance().spinBack) {
                    player.setYRot(player.getYRot() + increment);
                } else {
                    spinbot.getInstance().spinBack = true;
                    player.setYRot(player.getYRot() - increment);
                }
                if (spinbot.getInstance().spinBack && (currentYaw - spinAngle) > player.getYRot()) {
                    spinbot.getInstance().spinBack = false;
                }
                if (spinbot.getInstance().soundEnable) {
                    if (spinbot.getInstance().spinBack && !spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, 1f);
                        spinbot.getInstance().oscSwitch = true;
                    } else if (!spinbot.getInstance().spinBack && spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, .8f);
                        spinbot.getInstance().oscSwitch = false;
                    }
                }
            }
            if (spinbot.getInstance().oscSpinVertEnable) {
                var spinAngleVert = spinbot.getInstance().spinAngleVert;
                var currentPitch = spinbot.getInstance().currentPitch;

                if ((currentPitch + spinAngleVert) > player.getXRot() && !spinbot.getInstance().spinBack) {
                    player.setXRot(player.getXRot() + incrementVert);
                } else {
                    spinbot.getInstance().spinBack = true;
                    player.setXRot(player.getXRot() - incrementVert);
                }
                if (spinbot.getInstance().spinBack && (currentPitch - spinAngleVert) > player.getXRot()) {
                    spinbot.getInstance().spinBack = false;
                }
                if (spinbot.getInstance().soundEnable) {
                    if (spinbot.getInstance().spinBack && !spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, 1.1f);
                        spinbot.getInstance().oscSwitch = true;
                    } else if (!spinbot.getInstance().spinBack && spinbot.getInstance().oscSwitch) {
                        player.playSound(SoundEvent.createVariableRangeEvent(spinbot.getInstance().soundId), 1f, .9f);
                        spinbot.getInstance().oscSwitch = false;
                    }
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }
}
