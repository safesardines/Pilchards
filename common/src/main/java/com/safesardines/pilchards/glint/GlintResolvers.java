package com.safesardines.pilchards.glint;

import com.safesardines.pilchards.glint.component.GlintComponent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.item.ItemStack;

public final class GlintResolvers {
    private static final List<GlintResolver> RESOLVERS = new CopyOnWriteArrayList<>();
    private static volatile boolean bootstrapped;

    private GlintResolvers() {}

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
            RESOLVERS.add(GlintComponent::get);
        }
    }

    public static void add(GlintResolver resolver) {
        RESOLVERS.add(resolver);
    }

    public static void addFirst(GlintResolver resolver) {
        RESOLVERS.add(0, resolver);
    }

    public static boolean remove(GlintResolver resolver) {
        return RESOLVERS.remove(resolver);
    }

    public static GlintStyle resolve(ItemStack stack) {
        for (GlintResolver resolver : RESOLVERS) {
            GlintStyle style = resolver.resolve(stack);
            if (style != null) {
                return style;
            }
        }
        return null;
    }
}
