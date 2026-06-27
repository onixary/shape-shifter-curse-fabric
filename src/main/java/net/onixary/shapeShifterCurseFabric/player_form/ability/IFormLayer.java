package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IFormLayer {
    @NotNull Identifier getID();

    void __setID(@NotNull Identifier id);  // 禁止外部调用

    // 可以实现一些特殊玩法 比如我拓展中的 使魔(主角) 形态 需要这套系统来实现动态Power 而不用加一堆LayerGroup
    @NotNull List<Identifier> getPowerID(@Nullable PlayerEntity player);

    void __setPowerID(@NotNull List<Identifier> powerIDList);  // 禁止外部调用

    default void beforeApply(@NotNull PlayerEntity player) {};

    default void afterApply(@NotNull PlayerEntity player) {};

    // 给后续留的拓展接口 说不定未来客户端需要知道部分数据 反正目前不需要
    default void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeIdentifier(getID());
        packetByteBuf.writeCollection(getPowerID(null), PacketByteBuf::writeIdentifier);
    }

    default void read(@NotNull PacketByteBuf packetByteBuf) {
        __setID(packetByteBuf.readIdentifier());
        __setPowerID(packetByteBuf.readCollection(ArrayList::new, PacketByteBuf::readIdentifier));
    }
}
