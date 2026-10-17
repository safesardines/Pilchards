package com.safesardines.pilchards.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.safesardines.pilchards.glint.GlintResolvers;
import com.safesardines.pilchards.glint.internal.GlintContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void pilchards$pushGlint(
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            int overlay,
            BakedModel model,
            CallbackInfo ci) {
        GlintContext.push(GlintResolvers.resolve(stack));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void pilchards$popGlint(
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            int overlay,
            BakedModel model,
            CallbackInfo ci) {
        GlintContext.pop();
    }
}
