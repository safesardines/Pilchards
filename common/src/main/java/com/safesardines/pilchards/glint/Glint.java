package com.safesardines.pilchards.glint;

import com.safesardines.pilchards.glint.component.GlintComponent;
import com.safesardines.pilchards.glint.internal.GlintContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class Glint {
    private Glint() {}

    public static void color(ItemStack stack, int rgb) {
        custom(stack, GlintStyle.ofColor(rgb));
    }

    public static void texture(ItemStack stack, ResourceLocation texture) {
        custom(stack, GlintStyle.ofTexture(texture));
    }

    public static void custom(ItemStack stack, GlintStyle style) {
        stack.set(GlintComponent.TYPE.get(), style);
    }

    public static GlintStyle get(ItemStack stack) {
        return stack.get(GlintComponent.TYPE.get());
    }

    public static void clear(ItemStack stack) {
        stack.remove(GlintComponent.TYPE.get());
    }

    public static void push(ItemStack stack) {
        GlintContext.push(GlintResolvers.resolve(stack));
    }

    public static void push(GlintStyle style) {
        GlintContext.push(style);
    }

    public static void pop() {
        GlintContext.pop();
    }

    public static void render(ItemStack stack, Runnable action) {
        push(stack);
        try {
            action.run();
        } finally {
            pop();
        }
    }
}
