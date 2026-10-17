package com.safesardines.pilchards.glint.component;

import com.safesardines.pilchards.Pilchards;
import com.safesardines.pilchards.glint.GlintStyle;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class GlintComponent {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Pilchards.MOD_ID, "glint");

    private static final DeferredRegister<DataComponentType<?>> REGISTRY =
            DeferredRegister.create(Pilchards.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<GlintStyle>> TYPE = REGISTRY.register(ID, () ->
            DataComponentType.<GlintStyle>builder()
                    .persistent(GlintStyle.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(GlintStyle.CODEC))
                    .build());

    private GlintComponent() {}

    public static void register() {
        REGISTRY.register();
    }

    public static GlintStyle get(ItemStack stack) {
        return stack.get(TYPE.get());
    }
}
