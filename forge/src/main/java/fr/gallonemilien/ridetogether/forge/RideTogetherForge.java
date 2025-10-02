package fr.gallonemilien.ridetogether.forge;

import dev.architectury.platform.forge.EventBuses;
import fr.gallonemilien.ridetogether.RideTogether;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RideTogether.MOD_ID)
public final class RideTogetherForge {
    public RideTogetherForge() {
        EventBuses.registerModEventBus(RideTogether.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        RideTogether.init();
    }
}
