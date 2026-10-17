package com.safesardines.pilchards.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.safesardines.pilchards.glint.GlintResolvers;
import com.safesardines.pilchards.glint.internal.GlintContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
public class ThrownTridentRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void pilchards$pushGlint(
            ThrownTrident trident,
            float yaw,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            CallbackInfo ci) {
        GlintContext.push(GlintResolvers.resolve(trident.getWeaponItem()));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void pilchards$popGlint(
            ThrownTrident trident,
            float yaw,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            CallbackInfo ci) {
        GlintContext.pop();
    }
}
