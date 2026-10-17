package com.safesardines.pilchards.glint.internal;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.safesardines.pilchards.config.PilchardsConfig;
import com.safesardines.pilchards.glint.GlintShaders;
import com.safesardines.pilchards.glint.GlintStyle;
import com.safesardines.pilchards.mixin.MultiBufferSourceAccessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class GlintLayers {
    private record Key(GlintStyle style, GlintStyle.Feature feature) {}

    private static final Map<Key, RenderType> CACHE = new ConcurrentHashMap<>();
    private static final int BUFFER_SIZE = 1536;

    private GlintLayers() {}

    public static RenderType resolve(RenderType vanilla, GlintStyle.Feature vanillaFeature) {
        GlintStyle style = GlintContext.current();
        if (style == null || style.equals(GlintStyle.DEFAULT)) {
            return vanilla;
        }
        GlintStyle.Feature feature = style.feature().isInherit() ? vanillaFeature : style.feature();
        return CACHE.computeIfAbsent(new Key(style, feature), GlintLayers::build);
    }

    public static RenderType substitute(RenderType type) {
        GlintStyle.Feature feature = featureOf(type);
        if (feature == null || GlintContext.current() == null) {
            return type;
        }
        return resolve(type, feature);
    }

    private static GlintStyle.Feature featureOf(RenderType type) {
        if (type == RenderType.glint()) {
            return GlintStyle.Feature.NORMAL;
        }
        if (type == RenderType.glintTranslucent()) {
            return GlintStyle.Feature.TRANSLUCENT;
        }
        if (type == RenderType.entityGlint()) {
            return GlintStyle.Feature.ENTITY;
        }
        if (type == RenderType.entityGlintDirect()) {
            return GlintStyle.Feature.DIRECT_ENTITY;
        }
        if (type == RenderType.armorEntityGlint()) {
            return GlintStyle.Feature.ARMOR_ENTITY;
        }
        return null;
    }

    private static RenderType build(Key key) {
        GlintStyle style = key.style();
        ResourceLocation texture = style.hasTexture() ? style.texture() : GlintRenderStates.defaultTexture(key.feature());
        float brightness = key.feature() == GlintStyle.Feature.ARMOR_ENTITY
                ? style.brightness() * PilchardsConfig.armorGlintStrength()
                : style.brightness();
        ResourceLocation textured = style.hasTint()
                ? GlintTextures.get(texture, style.tint(), brightness)
                : texture;

        RenderStateShard.ShaderStateShard shader = key.feature() == GlintStyle.Feature.CUSTOM
                ? resolveShader(style)
                : GlintRenderStates.shader(key.feature());

        RenderStateShard.TexturingStateShard texturing = (style.scale() != null || style.speed() != null)
                ? GlintRenderStates.customTexturing(style.scale(), style.speed())
                : GlintRenderStates.texturing(key.feature());

        RenderStateShard.OutputStateShard output = style.output() != null
                ? GlintRenderStates.output(style.output())
                : GlintRenderStates.defaultOutput(key.feature());

        GlintStyle.Layering layering = style.layering();
        if (layering == GlintStyle.Layering.NONE && key.feature() == GlintStyle.Feature.ARMOR_ENTITY) {
            layering = GlintStyle.Layering.VIEW_OFFSET_Z;
        }

        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(shader)
                .setTextureState(new RenderStateShard.TextureStateShard(textured, true, false))
                .setTransparencyState(GlintRenderStates.transparency(style.transparency()))
                .setDepthTestState(GlintRenderStates.depthTest(style.depthTest()))
                .setCullState(GlintRenderStates.cull(style.cull()))
                .setWriteMaskState(GlintRenderStates.writeMask(style.writeMask()))
                .setTexturingState(texturing)
                .setOutputState(output)
                .setLayeringState(GlintRenderStates.layering(layering))
                .createCompositeState(false);

        RenderType layer = RenderType.create(
                "pilchards_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, BUFFER_SIZE, state);
        register(layer);
        return layer;
    }

    private static RenderStateShard.ShaderStateShard resolveShader(GlintStyle style) {
        RenderStateShard.ShaderStateShard shader = GlintShaders.get(style.shader());
        return shader != null ? shader : GlintRenderStates.shader(GlintStyle.Feature.NORMAL);
    }

    private static void register(RenderType layer) {
        MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
        ((MultiBufferSourceAccessor) (Object) source)
                .pilchards$fixedBuffers()
                .put(layer, new ByteBufferBuilder(layer.bufferSize()));
    }

    public static void clear() {
        CACHE.clear();
    }
}
