package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;

public interface IDataSegment {
    int getType();

    int getVersion();

    // 获取时调用(替换时会调用新的)
    default void onGain(PlayerEntity player) {};

    // 失去时调用(替换时不调用)
    default void onLost(PlayerEntity player) {};

    // 替换时调用(被替换方)
    default void onUpdate_Old(PlayerEntity player, IDataSegment newDataSegment) {};

    // 替换时调用(替换方)
    default void onUpdate_New(PlayerEntity player, IDataSegment newDataSegment) {};

    default boolean isTypeEqual(IDataSegment newDataSegment) {
        return getType() == newDataSegment.getType() && getVersion() == newDataSegment.getVersion();
    }

    default boolean isSameSlot(IDataSegment newDataSegment) {
        return isTypeEqual(newDataSegment);
    }
}
