package com.zurkuviirs.spinbot.mixin.client;

import com.zurkuviirs.spinbot.spinbot;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


    // TODO(Ravel): can not resolve target class Entity
// TODO(Ravel): can not resolve target class Entity
    @Mixin(Entity.class)
    public abstract class SpinbotVertMixin {
        // TODO(Ravel): Could not determine a single target
// TODO(Ravel): Could not determine a single target
        @Shadow public abstract float getXRot();

        // TODO(Ravel): Could not determine a single target
// TODO(Ravel): Could not determine a single target
        @Shadow public abstract void setXRot(float pitch);

        // TODO(Ravel): no target class
// TODO(Ravel): no target class
        @ModifyArg(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 1)
        public float unclampAnglesMin(float pitch, float min, float max) {
                float adjustedMin = min - spinbot.getInstance().vertMin; //:D
                return Mth.clamp(pitch, adjustedMin, max);
        }
        // TODO(Ravel): no target class
// TODO(Ravel): no target class
        @ModifyArg(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 2)
        public float unclampAnglesMax(float pitch, float min, float max) {
            float adjustedMax = max + spinbot.getInstance().vertMax;
            //System.out.println(getPitch());
            return Mth.clamp(pitch, min, adjustedMax);
        }
    }


