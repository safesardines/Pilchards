package com.safesardines.pilchards.glint.internal;

import com.mojang.blaze3d.platform.NativeImage;
import com.safesardines.pilchards.Pilchards;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public final class GlintTextures {
    private record Key(ResourceLocation source, int tint, float brightness) {}

    private static final Map<Key, ResourceLocation> CACHE = new ConcurrentHashMap<>();

    private GlintTextures() {}

    public static ResourceLocation get(ResourceLocation source, int tint, float brightness) {
        return CACHE.computeIfAbsent(new Key(source, tint, brightness), GlintTextures::create);
    }

    private static ResourceLocation create(Key key) {
        Minecraft minecraft = Minecraft.getInstance();
        NativeImage src = readSource(minecraft, key.source());
        if (src == null) {
            return key.source();
        }

        boolean owned = !(minecraft.getTextureManager().getTexture(key.source()) instanceof DynamicTexture);
        NativeImage tinted = src.mappedCopy(pixel -> tintPixel(pixel, key.tint(), key.brightness()));
        if (owned) {
            src.close();
        }
        DynamicTexture texture = new DynamicTexture(tinted);
        return minecraft.getTextureManager().register(Pilchards.MOD_ID, texture);
    }

    private static NativeImage readSource(Minecraft minecraft, ResourceLocation source) {
        try {
            Resource resource = minecraft.getResourceManager().getResource(source).orElse(null);
            if (resource != null) {
                try (InputStream in = resource.open()) {
                    return NativeImage.read(in);
                }
            }
        } catch (IOException e) {
            return null;
        }
        if (minecraft.getTextureManager().getTexture(source) instanceof DynamicTexture dynamic) {
            return dynamic.getPixels();
        }
        return null;
    }

    private static int tintPixel(int pixel, int tint, float gain) {
        int a = (pixel >>> 24) & 0xFF;
        int b = (pixel >>> 16) & 0xFF;
        int g = (pixel >>> 8) & 0xFF;
        int r = pixel & 0xFF;
        int level = Math.max(r, Math.max(g, b));
        int mask = (int) Math.min(255.0, Math.round(Math.sqrt(level / 255.0) * gain * 255.0));
        r = ((tint >> 16) & 0xFF) * mask / 255;
        g = ((tint >> 8) & 0xFF) * mask / 255;
        b = (tint & 0xFF) * mask / 255;
        a = a * ((tint >>> 24) & 0xFF) / 255;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static void clear() {
        if (CACHE.isEmpty()) {
            return;
        }
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        for (ResourceLocation id : CACHE.values()) {
            textureManager.release(id);
        }
        CACHE.clear();
    }
}
