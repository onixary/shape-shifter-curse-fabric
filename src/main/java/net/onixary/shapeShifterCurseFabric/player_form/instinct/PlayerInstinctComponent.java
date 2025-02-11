package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class PlayerInstinctComponent implements AutoSyncedComponent {
    public float instinctValue = 0f;
    public Queue<InstinctEffectType> immediateEffects = new ArrayDeque<>();
    public Set<InstinctEffectType> sustainedEffects = new HashSet<>();

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.immediateEffects.clear();
        this.sustainedEffects.clear();

        // 读取 immediateEffects
        var immediateEffectsList = nbtCompound.getList("immediateEffects", 8); // 8 是 NBT 字符串的 ID
        for (int i = 0; i < immediateEffectsList.size(); i++) {
            this.immediateEffects.add(InstinctEffectType.valueOf(immediateEffectsList.getString(i)));
        }

        // 读取 sustainedEffects
        var sustainedEffectsList = nbtCompound.getList("sustainedEffects", 8);
        for (int i = 0; i < sustainedEffectsList.size(); i++) {
            this.sustainedEffects.add(InstinctEffectType.valueOf(sustainedEffectsList.getString(i)));
        }

        // 读取 instinctValue
        this.instinctValue = nbtCompound.getFloat("instinctValue");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        // 写入 immediateEffects
        var immediateEffectsList = new NbtList();
        for (InstinctEffectType effect : this.immediateEffects) {
            immediateEffectsList.add(NbtString.of(effect.name()));
        }
        nbtCompound.put("immediateEffects", immediateEffectsList);

        // 写入 sustainedEffects
        var sustainedEffectsList = new NbtList();
        for (InstinctEffectType effect : this.sustainedEffects) {
            sustainedEffectsList.add(NbtString.of(effect.name()));
        }
        nbtCompound.put("sustainedEffects", sustainedEffectsList);

        // 写入 instinctValue
        nbtCompound.putFloat("instinctValue", this.instinctValue);
    }
}
