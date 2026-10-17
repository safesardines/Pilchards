package com.safesardines.pilchards.sprite.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.safesardines.pilchards.Pilchards;
import com.safesardines.pilchards.sprite.PixelScaler;
import com.safesardines.pilchards.sprite.RotSprite;
import dev.architectury.registry.ReloadListenerRegistry;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class RotSpriteTextures {
    private record Spec(
            ResourceLocation source,
            int frameSize,
            int steps,
            double stepDegrees,
            double fitDegrees,
            PixelScaler scaler,
            int upscalePasses) {}

    private static final Map<ResourceLocation, Spec> SPECS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, RotatedSprite> CACHE = new ConcurrentHashMap<>();
    private static final Set<ResourceLocation> FAILED = ConcurrentHashMap.newKeySet();

    private RotSpriteTextures() {}

    public static void init() {
        ReloadListenerRegistry.register(
                PackType.CLIENT_RESOURCES,
                new ReloadListener(),
                ResourceLocation.fromNamespaceAndPath(Pilchards.MOD_ID, "rot_sprite"));
    }

    public static RotatedSprite spin(ResourceLocation source, int frameSize) {
        return spin(source, frameSize, RotSprite.STEPS);
    }

    public static RotatedSprite spin(ResourceLocation source, int frameSize, int steps) {
        return get(
                key(source, frameSize, steps),
                source,
                frameSize,
                steps,
                360.0 / steps,
                360.0,
                RotSprite.SCALER,
                RotSprite.UPSCALE_PASSES);
    }

    public static RotatedSprite get(
            ResourceLocation key,
            ResourceLocation source,
            int frameSize,
            int steps,
            double stepDegrees,
            double fitDegrees,
            PixelScaler scaler,
            int upscalePasses) {
        SPECS.put(key, new Spec(source, frameSize, steps, stepDegrees, fitDegrees, scaler, upscalePasses));
        RotatedSprite existing = CACHE.get(key);
        if (existing != null) {
            return existing;
        }
        if (FAILED.contains(key)) {
            return null;
        }
        RotatedSprite built = build(key, SPECS.get(key));
        if (built == null) {
            FAILED.add(key);
            return null;
        }
        CACHE.put(key, built);
        return built;
    }

    public static RotatedSprite existing(ResourceLocation key) {
        return CACHE.get(key);
    }

    public static void release(ResourceLocation key) {
        SPECS.remove(key);
        FAILED.remove(key);
        RotatedSprite removed = CACHE.remove(key);
        if (removed != null) {
            Minecraft.getInstance().getTextureManager().release(removed.id());
        }
    }

    private static RotatedSprite build(ResourceLocation key, Spec spec) {
        NativeImage source = read(spec.source());
        if (source == null) {
            return null;
        }
        try {
            int size = spec.frameSize();
            int fit = RotSprite.fitSize(size, spec.fitDegrees());
            int offset = (size - fit) / 2;
            int[] base = new int[size * size];
            int sourceWidth = Math.max(1, source.getWidth());
            int sourceHeight = Math.max(1, source.getHeight());
            for (int y = 0; y < fit; y++) {
                for (int x = 0; x < fit; x++) {
                    base[(y + offset) * size + (x + offset)] =
                            source.getPixelRGBA(x * sourceWidth / fit, y * sourceHeight / fit);
                }
            }
            int[] frames = RotSprite.strip(base, size, spec.steps(), spec.stepDegrees(), spec.scaler(), spec.upscalePasses());
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, size, size * spec.steps(), false);
            for (int i = 0; i < frames.length; i++) {
                image.setPixelRGBA(i % size, i / size, frames[i]);
            }
            DynamicTexture texture = new DynamicTexture(image);
            Minecraft.getInstance().getTextureManager().register(key, texture);
            return new RotatedSprite(key, size, spec.steps());
        } finally {
            source.close();
        }
    }

    private static NativeImage read(ResourceLocation source) {
        try (InputStream in = Minecraft.getInstance().getResourceManager().open(source)) {
            return NativeImage.read(NativeImage.Format.RGBA, in);
        } catch (Exception e) {
            return null;
        }
    }

    private static ResourceLocation key(ResourceLocation source, int frameSize, int steps) {
        return ResourceLocation.fromNamespaceAndPath(
                Pilchards.MOD_ID,
                "rotsprite/" + source.getNamespace() + "/" + source.getPath() + "/" + frameSize + "x" + steps);
    }

    private static final class ReloadListener extends SimplePreparableReloadListener<Void> {
        @Override
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            return null;
        }

        @Override
        protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profiler) {
            CACHE.clear();
            FAILED.clear();
            for (Map.Entry<ResourceLocation, Spec> entry : SPECS.entrySet()) {
                RotatedSprite sprite = build(entry.getKey(), entry.getValue());
                if (sprite != null) {
                    CACHE.put(entry.getKey(), sprite);
                }
            }
        }
    }
}
