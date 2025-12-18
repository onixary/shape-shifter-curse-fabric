package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimFSM {
    // 不推荐覆写此方法
    public Pair<@Nullable Identifier, @NotNull Identifier> update(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        Identifier NextFSMID = getNextFSM(player, animSystemData);
        if (NextFSMID == null) {
            return new Pair<>(null, getStateID(player, animSystemData));
        } else {
            AbstractAnimFSM NextFSM = AnimRegistry.getAnimFSM(NextFSMID);
            if (NextFSM == null) {
                ShapeShifterCurseFabric.LOGGER.error("找不到动画控制状态机: {}", NextFSMID);
                return new Pair<>(null, getStateID(player, animSystemData));
            }
            Pair<@Nullable Identifier, @NotNull Identifier> NextFSMResult = NextFSM.update(player, animSystemData);
            if (NextFSMResult.getLeft() != null) {  // 多次跳转
                return NextFSMResult;
            }
            else {  // 单次跳转
                return new Pair<>(NextFSMID, NextFSMResult.getRight());
            }
        }
    }

    // 状态机需要覆写的方法
    public abstract @Nullable Identifier getNextFSM(PlayerEntity player, AnimSystem.AnimSystemData animSystemData);  // null为不切换

    public abstract @NotNull Identifier getStateID(PlayerEntity player, AnimSystem.AnimSystemData animSystemData);
}
