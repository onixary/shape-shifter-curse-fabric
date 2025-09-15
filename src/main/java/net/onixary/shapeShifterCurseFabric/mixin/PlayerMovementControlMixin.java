package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.additional_power.KeepSneakingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.SprintingStateTracker;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerMovementControlMixin {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void preventTravelWhenAttached(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {
            // 完全取消移动，类似蜂蜜块的效果
            player.setVelocity(0, 0, 0);
            ci.cancel();
        }
    }

    @Inject(method = "getMovementSpeed()F", at = @At("RETURN"), cancellable = true)
    private void zeroMovementSpeedWhenAttached(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {
            cir.setReturnValue(0.0f);
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void handleJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {
            // 处理跳跃取消吸附
            if (player.getWorld().isClient()) {
                PacketByteBuf buf = PacketByteBufs.create();
                ClientPlayNetworking.send(ModPackets.JUMP_DETACH_REQUEST_ID, buf);
            }
            ci.cancel();
        }

        // handle jump_event condition
        JumpEventCondition.setJumping(player, true);

        // 发送网络包到服务器
        if (player.getWorld().isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(player.getUuid());
            ClientPlayNetworking.send(ModPackets.JUMP_EVENT_ID, buf);
        }
    }

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void preventElytraCheckWhenAttached(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {

            if (player.getWorld().isClient()) {
                PacketByteBuf buf = PacketByteBufs.create();
                ClientPlayNetworking.send(ModPackets.JUMP_DETACH_REQUEST_ID, buf);
            }

            // 重置鞘翅相关标志
            player.stopFallFlying();
            // 强制设置为在地面上，这样空格键就不会触发鞘翅
            player.setOnGround(true);
            // 取消鞘翅检测
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void trackSprintingState(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        boolean wasSprintingLastTick = SprintingStateTracker.wasSprintingLastTick(player);
        boolean isCurrentlySprinting = player.isSprinting();
        boolean isCurrentlySneaking = player.isSneaking();

        // 先更新疾跑状态（这会在开始疾跑时重置触发标志）
        SprintingStateTracker.updateSprintingState(player, isCurrentlySprinting);

        // 检查从疾跑转为潜行的条件
        if (wasSprintingLastTick  && isCurrentlySneaking && SprintingStateTracker.canTrigger(player)) {
            ShapeShifterCurseFabric.LOGGER.info("Triggering sprint-to-sneak action for player: {}", player.getName().getString());

            // 设置已触发标志
            SprintingStateTracker.setTriggered(player);

            // 发送网络包到服务器
            if (player.getWorld().isClient()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(player.getUuid());
                ClientPlayNetworking.send(ModPackets.SPRINTING_TO_SNEAKING_EVENT_ID, buf);
            }
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void cleanupSprintingState(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        SprintingStateTracker.removePlayer(player);
    }
}