package com.safesardines.pilchards.glint.client;

import com.safesardines.pilchards.Pilchards;
import com.safesardines.pilchards.glint.internal.GlintLayers;
import com.safesardines.pilchards.glint.internal.GlintTextures;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class PilchardsGlintClient {
    private PilchardsGlintClient() {}

    public static void init() {
        ReloadListenerRegistry.register(
                PackType.CLIENT_RESOURCES,
                new ReloadListener(),
                ResourceLocation.fromNamespaceAndPath(Pilchards.MOD_ID, "glint"));
    }

    private static final class ReloadListener extends SimplePreparableReloadListener<Void> {
        @Override
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            return null;
        }

        @Override
        protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profiler) {
            GlintTextures.clear();
            GlintLayers.clear();
        }
    }
}
