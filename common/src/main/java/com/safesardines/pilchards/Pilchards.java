package com.safesardines.pilchards;

import com.safesardines.pilchards.client.PilchardsClient;
import com.safesardines.pilchards.command.PilchardsCommand;
import com.safesardines.pilchards.config.PilchardsConfig;
import com.safesardines.pilchards.glint.GlintResolvers;
import com.safesardines.pilchards.glint.component.GlintComponent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

public final class Pilchards {
    public static final String MOD_ID = "pilchards";

    private Pilchards() {}

    public static void init() {
        PilchardsConfig.load();
        GlintComponent.register();
        GlintResolvers.bootstrap();
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) ->
                PilchardsCommand.register(dispatcher));
        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> PilchardsClient.init());
        }
    }
}
