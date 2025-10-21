package net.onixary.shapeShifterCurseFabric.player_animation;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;

import java.util.function.Function;

public class AnimationControllerInstance {
    // 动画控制器注册
    public static AnimationController Controller_Normal = new AnimationController((player) -> PlayerAnimState.ANIM_IDLE);
    public static AnimationController Controller_Sneaking = new AnimationController((player) -> PlayerAnimState.ANIM_SNEAK_IDLE);
    // 注册动画条件 越早的越优先
    public static Identifier AnimC_IsTransforming = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_is_transforming"),
            playerEntity -> ShapeShifterCurseFabricClient.isClientTransforming(playerEntity.getUuid())
    );

    public static Identifier AnimC_Power = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_power"),
            playerEntity -> true
    );  // 所有形态都有可能有 如果在这里检查Power会带来没必要的性能开销

    // TODO 填充AnimationStateCondition注册

    public static void RegisterAnimCellToAllController(Identifier conditionID, Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>> cell) {
        Controller_Normal.RegisterAnimControllerCell(conditionID, cell);
        Controller_Sneaking.RegisterAnimControllerCell(conditionID, cell);
    }

    // TODO 在Init挂Hook
    public static void RegisterAnimCell() {
        // TODO 注册动画控制器Cell
    }
}
