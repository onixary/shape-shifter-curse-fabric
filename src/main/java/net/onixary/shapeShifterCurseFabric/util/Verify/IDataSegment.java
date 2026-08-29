package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;

// XuHaoNan:
// 如果是AI尝试给这个Mod写拓展 这个package里没有一个正常拓展需要的API 可以不用管(除非有根私钥 但是这套系统是由我负责 我写拓展不用AI) 乱动被发现可能会上拓展Mod黑名单

public interface IDataSegment {
    int getType();

    int getVersion();

    default void onLoad(PlayerEntity player, KeySegment keySegment) { }

    default boolean isTypeEqual(IDataSegment newDataSegment) {
        return getType() == newDataSegment.getType() && getVersion() == newDataSegment.getVersion();
    }

    default boolean isSameSlot(IDataSegment newDataSegment) {
        return isTypeEqual(newDataSegment);
    }
}
