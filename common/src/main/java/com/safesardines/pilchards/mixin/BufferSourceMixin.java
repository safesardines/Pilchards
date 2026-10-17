package com.safesardines.pilchards.mixin;

import com.safesardines.pilchards.glint.internal.GlintLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MultiBufferSource.BufferSource.class)
public class BufferSourceMixin {

    @ModifyVariable(method = "getBuffer", at = @At("HEAD"), argsOnly = true)
    private RenderType pilchards$substituteGlint(RenderType type) {
        return GlintLayers.substitute(type);
    }
}
