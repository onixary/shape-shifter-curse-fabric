package net.onixary.shapeShifterCurseFabric.player_animation;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.client.ClientPlayerStateManager;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class AnimationControllerInstance {
    // 动画控制器注册
    public static AnimationController Controller_Normal = new AnimationController((player, animDataHolder) -> PlayerAnimState.ANIM_IDLE);
    public static AnimationController Controller_Sneaking = new AnimationController((player, animDataHolder) -> PlayerAnimState.ANIM_SNEAK_IDLE);
    // 注册动画条件 越早的越优先
    public static Identifier AnimC_IsTransforming = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_is_transforming"),
            (playerEntity, animDataHolder) -> ShapeShifterCurseFabricClient.isClientTransforming(playerEntity.getUuid())
    );

    public static Identifier AnimC_Power = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_power"),
            (playerEntity, animDataHolder) -> true
    );  // 所有形态都有可能有 如果在这里检查Power会带来没必要的性能开销

    // TODO 填充AnimationStateCondition注册

    public static void RegisterAnimCellToAllController(Identifier conditionID, BiFunction<PlayerEntity, AnimationController.PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> cell) {
        Controller_Normal.RegisterAnimControllerCell(conditionID, cell);
        Controller_Sneaking.RegisterAnimControllerCell(conditionID, cell);
    }

    // TODO 在Init挂Hook
    public static void RegisterAnimCell() {
        RegisterAnimCellToAllController(AnimC_IsTransforming, (player, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_ON_TRANSFORM));
        RegisterAnimCellToAllController(AnimC_Power,
                (player, animDataHolder) -> {
                    AtomicReference<PlayerAnimState> currentState = new AtomicReference<>(PlayerAnimState.NONE);
                    AtomicBoolean foundLocalPower = new AtomicBoolean(false);
                    // 首先检查本地玩家的Power状态
                    PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                            .stream()
                            .filter(BatBlockAttachPower::isAttached)
                            .findFirst()
                            .ifPresent(attachPower -> {
                                if (attachPower.getAttachType() == BatBlockAttachPower.AttachType.SIDE) {
                                    currentState.set(PlayerAnimState.ANIM_ATTACH_SIDE);
                                } else if (attachPower.getAttachType() == BatBlockAttachPower.AttachType.BOTTOM) {
                                    currentState.set(PlayerAnimState.ANIM_ATTACH_BOTTOM);
                                }
                                foundLocalPower.set(true);
                            });

                    // 如果没有找到本地Power状态，检查客户端状态管理器（用于其他玩家）
                    if (!foundLocalPower.get()) {
                        ClientPlayerStateManager.PlayerAttachState otherPlayerState =
                                ClientPlayerStateManager.getPlayerAttachState(player.getUuid());
                        if (otherPlayerState != null && otherPlayerState.isAttached) {
                            if (otherPlayerState.attachType == BatBlockAttachPower.AttachType.SIDE) {
                                currentState.set(PlayerAnimState.ANIM_ATTACH_SIDE);
                            } else if (otherPlayerState.attachType == BatBlockAttachPower.AttachType.BOTTOM) {
                                currentState.set(PlayerAnimState.ANIM_ATTACH_BOTTOM);
                            }
                        }
                    }
                    if (currentState.get() == PlayerAnimState.NONE) {
                        return new Pair<>(AnimationControllerCellResult.NOT_MATCH, PlayerAnimState.NONE);
                    } else {
                        return new Pair<>(AnimationControllerCellResult.MATCH, currentState.get());
                    }
                }
        );  // 从PlayerEntityAnimOverrideMixin 中复制过来的


    }
}
