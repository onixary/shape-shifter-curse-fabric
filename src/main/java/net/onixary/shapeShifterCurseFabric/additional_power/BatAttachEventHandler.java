package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class BatAttachEventHandler {

    public static void register() {
        // 处理右键点击方块
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if (world.isClient()) {
                return ActionResult.PASS;
            }

            if (hand != Hand.MAIN_HAND || !player.getMainHandStack().isEmpty()) {
                return ActionResult.PASS;
            }

            // 获取玩家的 BatBlockAttachPower
            BatBlockAttachPower attachPower = getBatAttachPower(player);
            if (attachPower == null) {
                return ActionResult.PASS;
            }

            // 如果已经吸附，取消吸附
            if (attachPower.isAttached()) {
                attachPower.handleRightClick(player);
                return ActionResult.SUCCESS;
            }

            // 尝试吸附
            if (attachPower.tryAttach(player, hitResult)) {
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        // 处理方块破坏事件
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) {
                return;
            }
            BatBlockAttachPower attachPower = getBatAttachPower(player);
            if (attachPower != null && attachPower.isAttached()) {
                BlockPos attachedPos = attachPower.getAttachedBlockPos();
                if (attachedPos != null && attachedPos.equals(pos)) {
                    attachPower.detach(player, false);

                }
            }
        });
    }

    static BatBlockAttachPower getBatAttachPower(PlayerEntity player) {
        return PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
