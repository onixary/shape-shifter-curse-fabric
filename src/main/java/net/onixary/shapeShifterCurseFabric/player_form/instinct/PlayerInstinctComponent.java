package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class PlayerInstinctComponent implements AutoSyncedComponent {
    public float instinctValue = 0f;
    public float currentInstinctRate = 0f;
    public boolean isInstinctIncreasing = false;
    public boolean isInstinctDecreasing = false;
    public Queue<InstinctEffect> immediateEffects = new ArrayDeque<>();
    public Set<InstinctEffect> sustainedEffects = new HashSet<>();

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.immediateEffects.clear();
        this.sustainedEffects.clear();

        // 读取 immediateEffects
        var immediateEffectsList = nbtCompound.getList("immediateEffects", 10);
        for (int i = 0; i < immediateEffectsList.size(); i++) {
            this.immediateEffects.add(InstinctEffect.FromNBT(immediateEffectsList.getCompound(i)));
        }

        // 读取 sustainedEffects
        var sustainedEffectsList = nbtCompound.getList("sustainedEffects", 10);
        for (int i = 0; i < sustainedEffectsList.size(); i++) {
            this.sustainedEffects.add(InstinctEffect.FromNBT(sustainedEffectsList.getCompound(i)));
        }

        // 读取 instinctValue
        //ShapeShifterCurseFabric.LOGGER.info("loading instinctValue: " + nbtCompound.getFloat("instinctValue"));
        this.instinctValue = nbtCompound.getFloat("instinctValue");
        this.currentInstinctRate = nbtCompound.getFloat("currentInstinctRate");
        this.isInstinctIncreasing = nbtCompound.getBoolean("isInstinctIncreasing");
        this.isInstinctDecreasing = nbtCompound.getBoolean("isInstinctDecreasing");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        // 写入 immediateEffects
        var immediateEffectsList = new NbtList();
        for (InstinctEffect effect : this.immediateEffects) {
            immediateEffectsList.add(effect.ToNBT());
        }
        nbtCompound.put("immediateEffects", immediateEffectsList);

        // 写入 sustainedEffects
        var sustainedEffectsList = new NbtList();
        for (InstinctEffect effect : this.sustainedEffects) {
            sustainedEffectsList.add(effect.ToNBT());
        }
        nbtCompound.put("sustainedEffects", sustainedEffectsList);

        // 写入 instinctValue
        //ShapeShifterCurseFabric.LOGGER.info("saving instinctValue: " + this.instinctValue);
        nbtCompound.putFloat("instinctValue", this.instinctValue);
        nbtCompound.putFloat("currentInstinctRate", this.currentInstinctRate);
        nbtCompound.putBoolean("isInstinctIncreasing", this.isInstinctIncreasing);
        nbtCompound.putBoolean("isInstinctDecreasing", this.isInstinctDecreasing);
    }
}
