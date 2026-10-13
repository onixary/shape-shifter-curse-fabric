package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;

public interface IMagic {
    public Identifier getId();

    public ISprite getSprite();

    // 这几个个不准备在这个版本实现 留给拓展实现了 等拓展验证完成后再加入主线
    public default void onInvokeCommon(@NotNull PlayerEntity player) {

    }

    public default void onInvokeServer(@NotNull PlayerEntity player) {
        this.onInvokeCommon(player);
    }

    public default void onInvokeClient(@NotNull PlayerEntity player) {
        this.onInvokeCommon(player);
    }

    public default boolean canInvoke(@NotNull PlayerEntity player) {
        return true;
    }

    public default int getCooldown() {
        return 0;
    }
}
