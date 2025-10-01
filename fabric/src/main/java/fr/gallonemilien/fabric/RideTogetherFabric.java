package fr.gallonemilien.fabric;

import net.fabricmc.api.ModInitializer;

import fr.gallonemilien.RideTogether;

public final class RideTogetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RideTogether.init();
    }
}
