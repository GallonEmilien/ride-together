package fr.gallonemilien.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {


    @Shadow
    protected abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 vec3, float f);

    /**
     * Avoid the horse from moving when saddled
     * We indeed need to replace all the vanilla method to take count only of the y movement
     */
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3 vec3, CallbackInfo ci) {
        if ((Object) this instanceof AbstractHorse horse) {
            if (!horse.isVehicle() && horse.isSaddled()) {
                double gravity = horse.getGravity();
                boolean falling = horse.getDeltaMovement().y <= 0.0;

                if (falling && horse.hasEffect(MobEffects.SLOW_FALLING)) {
                    gravity = Math.min(gravity, 0.01);
                }

                BlockPos blockPos = horse.getBlockPosBelowThatAffectsMyMovement();
                float friction = horse.level().getBlockState(blockPos).getBlock().getFriction();

                Vec3 vec = this.handleRelativeFrictionAndCalculateMovement(Vec3.ZERO, friction);

                double y = vec.y;
                if (horse.hasEffect(MobEffects.LEVITATION)) {
                    y += (0.05 * (horse.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec.y) * 0.2;
                } else {
                    y -= gravity;
                }

                if (horse.shouldDiscardFriction()) {
                    horse.setDeltaMovement(0.0, y, 0.0);
                } else {
                    horse.setDeltaMovement(
                            0.0,
                            y * 0.98F,
                            0.0
                    );
                }

                horse.calculateEntityAnimation(false);
                ci.cancel();
            }
        }
    }

}
