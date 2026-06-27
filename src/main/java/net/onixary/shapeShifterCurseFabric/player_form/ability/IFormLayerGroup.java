package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IFormLayerGroup {
    boolean isExists(@NotNull PlayerEntity player);

    @NotNull Identifier getGroupID();

    void __setGroupID(@NotNull Identifier groupID);

    @NotNull List<Identifier> getLayers();

    void __setLayers(@NotNull List<Identifier> layers);

    default @NotNull Identifier transformLayerID(@NotNull PlayerEntity player, @Nullable Identifier layerID) {
        if (layerID == null) {
            // 需要重载 可以实现一些特殊操作
            throw new NullPointerException("layerID is null");
        }
        return layerID;
    }

    default void beforeApply(@NotNull PlayerEntity player, @NotNull Identifier layerID) {};

    default void afterApply(@NotNull PlayerEntity player, @NotNull Identifier layerID) {};

    // 给后续留的拓展接口 说不定未来客户端需要知道部分数据 反正目前不需要
    default void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeIdentifier(getGroupID());
        packetByteBuf.writeCollection(getLayers(), PacketByteBuf::writeIdentifier);
    }

    default void read(@NotNull PacketByteBuf packetByteBuf) {
        __setGroupID(packetByteBuf.readIdentifier());
        __setLayers(packetByteBuf.readCollection(ArrayList::new, PacketByteBuf::readIdentifier));
    }
}
