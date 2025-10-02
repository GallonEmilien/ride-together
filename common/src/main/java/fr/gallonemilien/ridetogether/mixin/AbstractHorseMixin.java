package fr.gallonemilien.ridetogether.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
    @Shadow
    protected abstract void doPlayerRide(Player player);

    @Inject(method ="mobInteract", at=@At("HEAD"), cancellable = true)
    private void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        final AbstractHorse horse = (AbstractHorse)(Object) this;
        /* Instant tame the horse in creative mode */
        if(!horse.isTamed() && player.isCreative()) {
            horse.tameWithName(player);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        /* Make the second player ride the horse */
        if(horse.isVehicle()) {
            doPlayerRide(player);
            player.startRiding(horse, true);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Shadow
    private float standAnimO;


    /**
     * Reposition the second player on the horse otherwise the player will be in the other one
     */
    @Inject(at = @At("HEAD"), method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", cancellable = true)
    @Environment(EnvType.CLIENT)
    protected void positionRider(Entity entity, Entity.MoveFunction moveFunction, CallbackInfo ci) {
        if((Object) this instanceof AbstractHorse abstractHorse) {
            if(abstractHorse.hasPassenger(entity) && abstractHorse.getPassengers().indexOf(entity) == 1) {
                double baseY = abstractHorse.getY() + abstractHorse.getPassengersRidingOffset() + entity.getMyRidingOffset();
                float yawRad = (float) Math.toRadians(-abstractHorse.getYRot());

                double backX = Math.sin(yawRad) * 0.5;
                double backZ = Math.cos(yawRad) * 0.5;
                double finalX = abstractHorse.getX() - backX;
                double finalZ = abstractHorse.getZ() - backZ;

                if (this.standAnimO > 0.0F) {
                    float bodyYawRad = abstractHorse.yBodyRot * ((float)Math.PI / 180F);
                    float sinYaw = Mth.sin(bodyYawRad);
                    float cosYaw = Mth.cos(bodyYawRad);
                    float rearOffset = 0.7F * this.standAnimO;
                    float heightOffset = 0.15F * this.standAnimO;
                    finalX = abstractHorse.getX() + (sinYaw * rearOffset) - backX;
                    finalZ = abstractHorse.getZ() - (cosYaw * rearOffset) - backZ;
                    baseY += heightOffset;
                }

                moveFunction.accept(entity, finalX, baseY, finalZ);

                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.yBodyRot = abstractHorse.yBodyRot;
                }
                ci.cancel();
            }
        }
    }
}
