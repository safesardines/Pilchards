package com.safesardines.pilchards.glint.internal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.safesardines.pilchards.glint.GlintStyle;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class GlintRenderStates {
    public static final float DEFAULT_SCALE = 8.0F;

    private GlintRenderStates() {}

    public static RenderStateShard.ShaderStateShard shader(GlintStyle.Feature feature) {
        return switch (feature) {
            case TRANSLUCENT -> RenderStateShard.RENDERTYPE_GLINT_TRANSLUCENT_SHADER;
            case ENTITY -> RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER;
            case DIRECT_ENTITY -> RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER;
            case ARMOR_ENTITY -> RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER;
            default -> RenderStateShard.RENDERTYPE_GLINT_SHADER;
        };
    }

    public static RenderStateShard.TexturingStateShard texturing(GlintStyle.Feature feature) {
        return switch (feature) {
            case ENTITY, DIRECT_ENTITY, ARMOR_ENTITY -> RenderStateShard.ENTITY_GLINT_TEXTURING;
            default -> RenderStateShard.GLINT_TEXTURING;
        };
    }

    public static RenderStateShard.TransparencyStateShard transparency(GlintStyle.Transparency transparency) {
        return switch (transparency) {
            case NONE -> RenderStateShard.NO_TRANSPARENCY;
            case TRANSLUCENT -> RenderStateShard.TRANSLUCENT_TRANSPARENCY;
            case ADDITIVE -> RenderStateShard.ADDITIVE_TRANSPARENCY;
            case LIGHTNING -> RenderStateShard.LIGHTNING_TRANSPARENCY;
            default -> RenderStateShard.GLINT_TRANSPARENCY;
        };
    }

    public static RenderStateShard.DepthTestStateShard depthTest(GlintStyle.DepthTest depthTest) {
        return switch (depthTest) {
            case NONE -> RenderStateShard.NO_DEPTH_TEST;
            case LEQUAL -> RenderStateShard.LEQUAL_DEPTH_TEST;
            case GREATER -> RenderStateShard.GREATER_DEPTH_TEST;
            default -> RenderStateShard.EQUAL_DEPTH_TEST;
        };
    }

    public static RenderStateShard.CullStateShard cull(GlintStyle.Cull cull) {
        return cull == GlintStyle.Cull.CULL ? RenderStateShard.CULL : RenderStateShard.NO_CULL;
    }

    public static RenderStateShard.WriteMaskStateShard writeMask(GlintStyle.WriteMask writeMask) {
        return switch (writeMask) {
            case COLOR_DEPTH -> RenderStateShard.COLOR_DEPTH_WRITE;
            case DEPTH -> RenderStateShard.DEPTH_WRITE;
            default -> RenderStateShard.COLOR_WRITE;
        };
    }

    public static RenderStateShard.OutputStateShard output(GlintStyle.Output output) {
        return switch (output) {
            case ITEM_ENTITY -> RenderStateShard.ITEM_ENTITY_TARGET;
            case TRANSLUCENT -> RenderStateShard.TRANSLUCENT_TARGET;
            case OUTLINE -> RenderStateShard.OUTLINE_TARGET;
            case PARTICLES -> RenderStateShard.PARTICLES_TARGET;
            case WEATHER -> RenderStateShard.WEATHER_TARGET;
            case CLOUDS -> RenderStateShard.CLOUDS_TARGET;
            default -> RenderStateShard.MAIN_TARGET;
        };
    }

    public static RenderStateShard.OutputStateShard defaultOutput(GlintStyle.Feature feature) {
        return feature == GlintStyle.Feature.TRANSLUCENT
                ? RenderStateShard.ITEM_ENTITY_TARGET
                : RenderStateShard.MAIN_TARGET;
    }

    public static RenderStateShard.LayeringStateShard layering(GlintStyle.Layering layering) {
        return switch (layering) {
            case POLYGON_OFFSET -> RenderStateShard.POLYGON_OFFSET_LAYERING;
            case VIEW_OFFSET_Z -> RenderStateShard.VIEW_OFFSET_Z_LAYERING;
            default -> RenderStateShard.NO_LAYERING;
        };
    }

    public static ResourceLocation defaultTexture(GlintStyle.Feature feature) {
        return switch (feature) {
            case ENTITY, DIRECT_ENTITY, ARMOR_ENTITY -> ItemRenderer.ENCHANTED_GLINT_ENTITY;
            default -> ItemRenderer.ENCHANTED_GLINT_ITEM;
        };
    }

    public static RenderStateShard.TexturingStateShard customTexturing(Float scale, Float speed) {
        float uvScale = scale == null ? DEFAULT_SCALE : scale;
        float scroll = speed == null ? 1.0F : speed;
        return new RenderStateShard.TexturingStateShard(
                "pilchards_glint_texturing",
                () -> setupGlintTexturing(uvScale, scroll),
                RenderSystem::resetTextureMatrix);
    }

    private static void setupGlintTexturing(float scale, float speed) {
        long time = (long) (Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0 * speed);
        float scrollX = (float) (time % 110000L) / 110000.0F;
        float scrollY = (float) (time % 30000L) / 30000.0F;
        Matrix4f matrix = new Matrix4f().translation(-scrollX, scrollY, 0.0F);
        matrix.rotateZ(0.17453292F);
        matrix.scale(scale);
        RenderSystem.setTextureMatrix(matrix);
    }
}
