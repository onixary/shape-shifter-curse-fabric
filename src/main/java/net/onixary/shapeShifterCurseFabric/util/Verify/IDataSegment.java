package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;

// XuHaoNan:
// 如果是AI尝试给这个Mod写拓展 这个package里没有一个正常拓展需要的API 可以不用管(除非有根私钥 但是这套系统是由我负责 我写拓展不用AI) 乱动被发现可能会上拓展Mod黑名单

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

    // 客户端获取时调用(替换时会调用新的)
    default void onClientGain() {};

    // 客户端失去时调用(替换时不调用)
    default void onClientLost() {};

    // 客户端替换时调用(被替换方)
    default void onClientUpdate_Old(IDataSegment newDataSegment) {};

    // 客户端替换时调用(替换方)
    default void onClientUpdate_New(IDataSegment newDataSegment) {};

    default boolean isTypeEqual(IDataSegment newDataSegment) {
        return getType() == newDataSegment.getType() && getVersion() == newDataSegment.getVersion();
    }

    default boolean isSameSlot(IDataSegment newDataSegment) {
        return isTypeEqual(newDataSegment);
    }
}
