package com.safesardines.pilchards.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.safesardines.pilchards.glint.GlintResolvers;
import com.safesardines.pilchards.glint.internal.GlintContext;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("HEAD"), require = 0)
    private void pilchards$pushGlint(
            PoseStack pose,
            MultiBufferSource buffers,
            LivingEntity entity,
            EquipmentSlot slot,
            int light,
            HumanoidModel<?> model,
            CallbackInfo ci) {
        ItemStack stack = entity.getItemBySlot(slot);
        GlintContext.push(GlintResolvers.resolve(stack));
    }

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("RETURN"), require = 0)
    private void pilchards$popGlint(
            PoseStack pose,
            MultiBufferSource buffers,
            LivingEntity entity,
            EquipmentSlot slot,
            int light,
            HumanoidModel<?> model,
            CallbackInfo ci) {
        GlintContext.pop();
    }

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At("HEAD"), require = 0)
    private void pilchards$pushGlintAnimated(
            PoseStack pose,
            MultiBufferSource buffers,
            LivingEntity entity,
            EquipmentSlot slot,
            int light,
            HumanoidModel<?> model,
            float f1,
            float f2,
            float f3,
            float f4,
            float f5,
            float f6,
            CallbackInfo ci) {
        ItemStack stack = entity.getItemBySlot(slot);
        GlintContext.push(GlintResolvers.resolve(stack));
    }

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At("RETURN"), require = 0)
    private void pilchards$popGlintAnimated(
            PoseStack pose,
            MultiBufferSource buffers,
            LivingEntity entity,
            EquipmentSlot slot,
            int light,
            HumanoidModel<?> model,
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
