package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.client.SpinbotClient;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;



@Mixin(Entity.class)
public abstract class SpinbotVertMixin {
    @Shadow public abstract float getXRot();
    @Shadow public abstract void setXRot(float pitch);

    @ModifyArg(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 1)
    public float unclampAnglesMin(float pitch, float min, float max) {
        float adjustedMin = min - SpinbotClient.getInstance().vertMin; //:D
        return Mth.clamp(pitch, adjustedMin, max);
    }
    @ModifyArg(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 2)
    public float unclampAnglesMax(float pitch, float min, float max) {
        float adjustedMax = max + SpinbotClient.getInstance().vertMax;
        //System.out.println(getPitch());
        return Mth.clamp(pitch, min, adjustedMax);
    }
}


