package com.safesardines.pilchards.glint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record GlintStyle(
        ResourceLocation texture,
        Integer tint,
        Feature feature,
        ResourceLocation shader,
        Float scale,
        Float speed,
        Transparency transparency,
        DepthTest depthTest,
        Cull cull,
        WriteMask writeMask,
        Output output,
        Layering layering,
        float brightness) {

    public static final GlintStyle DEFAULT = new GlintStyle(
            null, null, Feature.INHERIT, null, null, null,
            Transparency.GLINT, DepthTest.EQUAL, Cull.NONE, WriteMask.COLOR, null, Layering.NONE, 1.0F);

    public GlintStyle {
        feature = feature == null ? Feature.INHERIT : feature;
        transparency = transparency == null ? Transparency.GLINT : transparency;
        depthTest = depthTest == null ? DepthTest.EQUAL : depthTest;
        cull = cull == null ? Cull.NONE : cull;
        writeMask = writeMask == null ? WriteMask.COLOR : writeMask;
        layering = layering == null ? Layering.NONE : layering;
        brightness = brightness <= 0.0F ? 1.0F : brightness;
    }

    public boolean hasTint() {
        return this.tint != null;
    }

    public boolean hasTexture() {
        return this.texture != null;
    }

    public static GlintStyle ofColor(int rgb) {
        return DEFAULT.toBuilder().rgb(rgb).build();
    }

    public static GlintStyle ofTexture(ResourceLocation texture) {
        return DEFAULT.toBuilder().texture(texture).build();
    }

    public static GlintStyle of(ResourceLocation texture, int rgb) {
        return DEFAULT.toBuilder().texture(texture).rgb(rgb).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.texture = this.texture;
        builder.tint = this.tint;
        builder.feature = this.feature;
        builder.shader = this.shader;
        builder.scale = this.scale;
        builder.speed = this.speed;
        builder.transparency = this.transparency;
        builder.depthTest = this.depthTest;
        builder.cull = this.cull;
        builder.writeMask = this.writeMask;
        builder.output = this.output;
        builder.layering = this.layering;
        builder.brightness = this.brightness;
        return builder;
    }

    public static final class Builder {
        private ResourceLocation texture;
        private Integer tint;
        private Feature feature = Feature.INHERIT;
        private ResourceLocation shader;
        private Float scale;
        private Float speed;
        private Transparency transparency = Transparency.GLINT;
        private DepthTest depthTest = DepthTest.EQUAL;
        private Cull cull = Cull.NONE;
        private WriteMask writeMask = WriteMask.COLOR;
        private Output output;
        private Layering layering = Layering.NONE;
        private float brightness = 1.0F;

        private Builder() {}

        public Builder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public Builder tint(int argb) {
            this.tint = argb;
            return this;
        }

        public Builder rgb(int rgb) {
            this.tint = 0xFF000000 | (rgb & 0xFFFFFF);
            return this;
        }

        public Builder feature(Feature feature) {
            this.feature = feature;
            return this;
        }

        public Builder shader(ResourceLocation shader) {
            this.shader = shader;
            this.feature = Feature.CUSTOM;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder transparency(Transparency transparency) {
            this.transparency = transparency;
            return this;
        }

        public Builder depthTest(DepthTest depthTest) {
            this.depthTest = depthTest;
            return this;
        }

        public Builder cull(Cull cull) {
            this.cull = cull;
            return this;
        }

        public Builder writeMask(WriteMask writeMask) {
            this.writeMask = writeMask;
            return this;
        }

        public Builder output(Output output) {
            this.output = output;
            return this;
        }

        public Builder layering(Layering layering) {
            this.layering = layering;
            return this;
        }

        public Builder brightness(float brightness) {
            this.brightness = brightness;
            return this;
        }

        public GlintStyle build() {
            return new GlintStyle(texture, tint, feature, shader, scale, speed,
                    transparency, depthTest, cull, writeMask, output, layering, brightness);
        }
    }

    public static final Codec<GlintStyle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(s -> Optional.ofNullable(s.texture)),
            Codec.INT.optionalFieldOf("tint").forGetter(s -> Optional.ofNullable(s.tint)),
            enumCodec(Feature.class).optionalFieldOf("feature", Feature.INHERIT).forGetter(GlintStyle::feature),
            ResourceLocation.CODEC.optionalFieldOf("shader").forGetter(s -> Optional.ofNullable(s.shader)),
            Codec.FLOAT.optionalFieldOf("scale").forGetter(s -> Optional.ofNullable(s.scale)),
            Codec.FLOAT.optionalFieldOf("speed").forGetter(s -> Optional.ofNullable(s.speed)),
            enumCodec(Transparency.class).optionalFieldOf("transparency", Transparency.GLINT).forGetter(GlintStyle::transparency),
            enumCodec(DepthTest.class).optionalFieldOf("depth_test", DepthTest.EQUAL).forGetter(GlintStyle::depthTest),
            enumCodec(Cull.class).optionalFieldOf("cull", Cull.NONE).forGetter(GlintStyle::cull),
            enumCodec(WriteMask.class).optionalFieldOf("write_mask", WriteMask.COLOR).forGetter(GlintStyle::writeMask),
            enumCodec(Output.class).optionalFieldOf("output").forGetter(s -> Optional.ofNullable(s.output)),
            enumCodec(Layering.class).optionalFieldOf("layering", Layering.NONE).forGetter(GlintStyle::layering),
            Codec.FLOAT.optionalFieldOf("brightness", 1.0F).forGetter(GlintStyle::brightness)
    ).apply(instance, (texture, tint, feature, shader, scale, speed, transparency, depthTest, cull, writeMask, output, layering, brightness) ->
            new GlintStyle(texture.orElse(null), tint.orElse(null), feature, shader.orElse(null), scale.orElse(null),
                    speed.orElse(null), transparency, depthTest, cull, writeMask, output.orElse(null), layering, brightness)));

    private static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type) {
        return Codec.STRING.xmap(
                name -> Enum.valueOf(type, name.toUpperCase(Locale.ROOT)),
                value -> value.name().toLowerCase(Locale.ROOT));
    }

    public enum Feature {
        INHERIT, NORMAL, TRANSLUCENT, ENTITY, DIRECT_ENTITY, ARMOR_ENTITY, CUSTOM;

        public boolean isInherit() {
            return this == INHERIT;
        }
    }

    public enum Transparency { GLINT, NONE, TRANSLUCENT, ADDITIVE, LIGHTNING }

    public enum DepthTest { EQUAL, NONE, LEQUAL, GREATER }

    public enum Cull { NONE, CULL }

    public enum WriteMask { COLOR, COLOR_DEPTH, DEPTH }

    public enum Output { MAIN, ITEM_ENTITY, TRANSLUCENT, OUTLINE, PARTICLES, WEATHER, CLOUDS }

    public enum Layering { NONE, POLYGON_OFFSET, VIEW_OFFSET_Z }
}
