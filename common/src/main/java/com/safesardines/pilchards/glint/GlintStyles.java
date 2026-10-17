package com.safesardines.pilchards.glint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

public final class GlintStyles {
    private static final Map<ResourceLocation, GlintStyle> STYLES = new ConcurrentHashMap<>();

    private GlintStyles() {}

    public static void register(ResourceLocation id, GlintStyle style) {
        STYLES.put(id, style);
    }

    public static GlintStyle get(ResourceLocation id) {
        return STYLES.get(id);
    }

    public static boolean contains(ResourceLocation id) {
        return STYLES.containsKey(id);
    }
}
