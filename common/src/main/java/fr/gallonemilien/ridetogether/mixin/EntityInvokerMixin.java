package fr.gallonemilien.ridetogether.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvokerMixin {
    @Invoker("getBlockPosBelowThatAffectsMyMovement")
    BlockPos invokeGetBlockPosBelowThatAffectsMyMovement();
}