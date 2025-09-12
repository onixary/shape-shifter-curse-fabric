package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.nbt.NbtCompound;

public class InstinctEffect {
    public final String ID;
    private final float value;
    private int remain_duration;
    private final boolean sustained;

    public InstinctEffect(String ID, float value, int duration, boolean sustained) {
        this.ID = ID;
        this.value = value;
        this.remain_duration = duration;
        this.sustained = sustained;
    }

    public InstinctEffect(String ID,float value, int duration) {
        this(ID, value, duration, true);
    }

    public InstinctEffect(String ID,float value) {
        this(ID, value, 0, false);
    }

    public boolean isSustained() {
        return sustained;
    }

    public float getValue() {
        return isSustained() ? 0.0f : value;
    }

    public float getRateModifier() {
        return isSustained() ? value : 0f;
    }

    public boolean IsEffectExist() {
        remain_duration--;
        return remain_duration >= 0;
    }

    public NbtCompound ToNBT() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "instinct");
        nbt.putFloat("value", value);
        nbt.putInt("remain_duration", remain_duration);
        nbt.putBoolean("sustained", sustained);
        return nbt;
    }

    public static InstinctEffect FromNBT(NbtCompound nbt) {
        return new InstinctEffect(nbt.getString("id"), nbt.getFloat("value"), nbt.getInt("remain_duration"), nbt.getBoolean("sustained"));
    }
}
