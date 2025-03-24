package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.UUID;

public class PlayerFormDataNetworking {
    public PlayerFormDataNetworking(UUID playerId, PlayerForms currentForm) {
        this.playerId = playerId;
        this.currentForm = currentForm;
    }

    private final UUID playerId;
    private final PlayerForms currentForm;

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
                PlayerForms.valueOf(nbt.getString("CurrentForm"))
        );
    }
}
