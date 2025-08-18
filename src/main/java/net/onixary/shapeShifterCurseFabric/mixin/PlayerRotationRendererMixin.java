package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntityRenderer.class, priority = 100)
public class PlayerRotationRendererMixin {

    @Unique
    private boolean isAttachedToBlock(PlayerEntity player) {
        return PowerHolderComponent.hasPower(player, BatBlockAttachPower.class, power ->
                power.isActive() && power.isAttached() && power.getAttachType() == BatBlockAttachPower.AttachType.SIDE
        );
    }

    @Unique
    private boolean isFirstPersonView() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.options.getPerspective() == Perspective.FIRST_PERSON;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void lockRotationWhenAttached(
            AbstractClientPlayerEntity player,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (isFirstPersonView()) {
            return;
        }

        if (isAttachedToBlock(player)) {
            PowerHolderComponent.getPowers(player, BatBlockAttachPower.class).forEach(power -> {
                //player.setYaw(power.getTargetYaw());
                player.setBodyYaw(power.getTargetYaw());
                //player.prevYaw = power.getTargetYaw();
                player.prevBodyYaw = power.getTargetYaw();
            });
        }
    }
}
