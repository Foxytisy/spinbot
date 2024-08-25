package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class SpinbotMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Unique
    private long lastTime = System.currentTimeMillis();

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInjected(CallbackInfo ci) {
        final var deltaTime = System.currentTimeMillis() - lastTime;
            final var p = this.player;
            if (p != null) {
                var increment = (deltaTime / 1000f) * spinbot.getInstance().spinAmount;
                if (spinbot.getInstance().spinEnable) {
                    p.setYaw(p.getYaw() + increment);
                }
                if(spinbot.getInstance().angleSpinEnable){
                    var spinAngle = spinbot.getInstance().spinAngle;
                    var currentYaw = spinbot.getInstance().currentYaw;
                    
                    if (spinAngle < 0){
                        if ((currentYaw + spinAngle < p.getYaw())) {
                            p.setYaw(p.getYaw() - increment);
                            p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                        } else {
                            spinbot.getInstance().angleSpinEnable = false;
                        }
                    } else {
                        if ((currentYaw + spinAngle > p.getYaw())) {
                            p.setYaw(p.getYaw() + increment);
                            p.sendMessage(Text.literal(String.valueOf(currentYaw)));

                        } else {
                            spinbot.getInstance().angleSpinEnable = false;
                        }
                    }
                }
            }
        lastTime = System.currentTimeMillis();
    }

}
