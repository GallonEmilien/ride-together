package fr.gallonemilien.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(at = @At("HEAD"), method = "canAddPassenger", cancellable = true)
    protected void canAddPassenger(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof AbstractHorse abstractHorse) {
            cir.setReturnValue(abstractHorse.getPassengers().size() < 2);
        }
    }


    /**
     * Reposition the second player on the horse otherwise the player will be in the other one
     */
    @Inject(at = @At("HEAD"), method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", cancellable = true)
    @Environment(EnvType.CLIENT)
    protected void positionRider(Entity entity, Entity.MoveFunction moveFunction, CallbackInfo ci) {
        if((Object) this instanceof AbstractHorse abstractHorse) {
            if(abstractHorse.getPassengers().indexOf(entity) == 1) {
                Vec3 vec3 = abstractHorse.getPassengerRidingPosition(entity);
                float yawRad = (float) Math.toRadians(-abstractHorse.getYRot());
                double backX = Math.sin(yawRad) * 0.5;
                double backZ = Math.cos(yawRad) * 0.5;
                Vec3 riderPos = vec3.add(-backX, 0.0, -backZ);
                Vec3 vec32 = entity.getVehicleAttachmentPoint(abstractHorse);
                moveFunction.accept(entity, riderPos.x - vec32.x, riderPos.y - vec32.y, riderPos.z - vec32.z);
                ci.cancel();
            }
        }
    }
}
