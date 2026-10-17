package com.safesardines.pilchards.fabric;

import com.safesardines.pilchards.Pilchards;
import net.fabricmc.api.ModInitializer;

public final class PilchardsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Pilchards.init();
    }
}
