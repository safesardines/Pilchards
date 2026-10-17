package com.safesardines.pilchards.glint;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface GlintResolver {
    GlintStyle resolve(ItemStack stack);
}
