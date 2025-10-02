package fr.gallonemilien.ridetogether.fabric;

import net.fabricmc.api.ModInitializer;

import fr.gallonemilien.ridetogether.RideTogether;

public final class RideTogetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RideTogether.init();
    }
}
