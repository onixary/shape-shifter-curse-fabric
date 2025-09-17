package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.util.UUID;

public class PlayerFormDataNetworking {
    public PlayerFormDataNetworking(UUID playerId, PlayerFormBase currentForm) {
        this.playerId = playerId;
        this.currentForm = currentForm;
    }

    private final UUID playerId;
    private final PlayerFormBase currentForm;

    // 序列化到 NBT
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("PlayerId", playerId);
        nbt.putString("CurrentForm", currentForm.name());
        return nbt;
    }

    // 从 NBT 反序列化
    public static PlayerFormDataNetworking fromNbt(NbtCompound nbt) {
        return new PlayerFormDataNetworking(
                nbt.getUuid("PlayerId"),
                RegPlayerForms.getPlayerForm(nbt.getString("CurrentForm"))
        );
    }
}
