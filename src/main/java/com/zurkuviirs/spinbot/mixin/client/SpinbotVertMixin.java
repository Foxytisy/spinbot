package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


    @Mixin(Entity.class)
    public abstract class SpinbotVertMixin {
        @Shadow public abstract float getPitch();

        @Shadow public abstract void setPitch(float pitch);

        @ModifyArg(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"), index = 1)
        public float unclampAnglesMin(float pitch, float min, float max) {
            //if (spinbot.getInstance().vertEnable == true) {
                float adjustedMin = min - spinbot.getInstance().vertMin; //:D
                //float adjustedMax = max + 999999999999999999999999999.9F;
                //System.out.println(getPitch());
                return MathHelper.clamp(pitch, adjustedMin, max);
            //}
        }
        @ModifyArg(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"), index = 2)
        public float unclampAnglesMax(float pitch, float min, float max) {
            float adjustedMax = max + spinbot.getInstance().vertMax;
            //System.out.println(getPitch());
            return MathHelper.clamp(pitch, min, adjustedMax);
        }
    }


