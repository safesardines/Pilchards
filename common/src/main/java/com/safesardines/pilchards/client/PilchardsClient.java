package com.safesardines.pilchards.client;

import com.safesardines.pilchards.glint.client.PilchardsGlintClient;
import com.safesardines.pilchards.sprite.client.RotSpriteTextures;

public final class PilchardsClient {
    private PilchardsClient() {}

    public static void init() {
        PilchardsGlintClient.init();
        RotSpriteTextures.init();
    }
}
