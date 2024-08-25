package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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
                if (spinbot.getInstance().spinEnable) {
                    p.setYaw(p.getYaw() + (deltaTime / 1000f) * spinbot.getInstance().spinAmount);
                }
            }
        lastTime = System.currentTimeMillis();
    }

}
