package com.safesardines.pilchards.glint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

public final class GlintShaders {
    private static final Map<ResourceLocation, RenderStateShard.ShaderStateShard> SHADERS = new ConcurrentHashMap<>();

    private GlintShaders() {}

    public static void register(ResourceLocation id, RenderStateShard.ShaderStateShard shader) {
        SHADERS.put(id, shader);
    }

    public static RenderStateShard.ShaderStateShard get(ResourceLocation id) {
        return id == null ? null : SHADERS.get(id);
    }
}
