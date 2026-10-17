package com.safesardines.pilchards.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.safesardines.pilchards.glint.GlintResolvers;
import com.safesardines.pilchards.glint.internal.GlintContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    private void pilchards$pushGlint(
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            LivingEntity entity,
            float f1,
            float f2,
            float f3,
            float f4,
            float f5,
            float f6,
            CallbackInfo ci) {
        GlintContext.push(GlintResolvers.resolve(entity.getItemBySlot(EquipmentSlot.CHEST)));
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void pilchards$popGlint(
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            LivingEntity entity,
            float f1,
            float f2,
            float f3,
            float f4,
            float f5,
            float f6,
            CallbackInfo ci) {
        GlintContext.pop();
    }
}
