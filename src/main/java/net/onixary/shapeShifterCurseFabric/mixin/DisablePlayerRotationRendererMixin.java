package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.onixary.shapeShifterCurseFabric.additional_power.DisablePlayerRotationPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = PlayerEntityRenderer.class, priority = 100)
public class DisablePlayerRotationRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void lockRotationToNorth(
            AbstractClientPlayerEntity player,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON) {
            return;
        }

        if (PowerHolderComponent.hasPower(player, DisablePlayerRotationPower.class)) {
            // Lock body and head facing north (yaw = 180 in Minecraft coordinate system)
            player.prevBodyYaw = 180.0F;
            player.bodyYaw = 180.0F;
            player.prevHeadYaw = 180.0F;
            player.headYaw = 180.0F;
        }
    }
}
