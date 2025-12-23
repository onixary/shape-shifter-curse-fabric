package net.onixary.shapeShifterCurseFabric.mana;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.sync.PlayerSyncPredicate;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 试试这个实验性接口 省的我缓存ModifierList了
public class ManaComponent implements AutoSyncedComponent, PlayerComponent<ManaComponent> {
    private PlayerEntity player;
    public static Identifier LocalManaTypeID = null;  // 仅客户端 怎么想都不会出现在服务器端上 虽然服务器上的数据也会更新 我懒得给readFromNbt写客户端判断了
    public double Mana = 0.0d;
    public Identifier ManaTypeID = null;
    private List<Identifier> ManaTypeArray = new ArrayList<>();
    public ManaUtils.ModifierList MaxManaModifier = new ManaUtils.ModifierList();  // 仅服务器端
    public ManaUtils.ModifierList MaxManaModifierPlayerSide = new ManaUtils.ModifierList();  // 仅服务器端
    private double MaxManaClient = 0.0d;  // 仅客户端
    public ManaUtils.ModifierList ManaRegenModifier = new ManaUtils.ModifierList();  // 仅服务器端
    public ManaUtils.ModifierList ManaRegenModifierPlayerSide = new ManaUtils.ModifierList();  // 仅服务器端
    private double ManaRegenClient = 0.0d;  // 仅客户端
    private boolean Dirty = false;  // 仅服务器端 客户端没用
    private double tempRegan = 0.0d;  // 双端
    private int tempReganTime = 0;  // 仅服务器端

    public ManaComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean isNeedSync() {
        return this.Dirty || this.MaxManaModifier.needSync || this.ManaRegenModifier.needSync;
    }

    public boolean isEnabled() {
        return ManaTypeID != null;
    }

    public Identifier getManaTypeID() {
        return ManaTypeID;
    }

    // 防止出现先加后减的情况

    public void gainManaTypeID(@NotNull Identifier manaTypeID) {
        if (!ManaTypeArray.contains(manaTypeID)) {
            ManaTypeArray.add(manaTypeID);
        }
        this.__setManaTypeID__(manaTypeID);
    }

    public void loseManaTypeID(@NotNull Identifier manaTypeID) {
        if (ManaTypeArray.contains(manaTypeID)) {
            ManaTypeArray.remove(manaTypeID);
            if (Objects.equals(this.ManaTypeID, manaTypeID)) {
                if (!ManaTypeArray.isEmpty()) {
                    this.__setManaTypeID__(ManaTypeArray.get(0));
                } else {
                    this.__setManaTypeID__(null);
                }
            } else {
                return;
            }
            return;
        }
    }

    public void setManaTypeID(@Nullable Identifier manaTypeID) {
        ManaTypeArray.clear();
        this.__setManaTypeID__(manaTypeID);
    }

    private void __setManaTypeID__(@Nullable Identifier manaTypeID) {
        if (Objects.equals(this.ManaTypeID, manaTypeID)) {
            return;
        }
        this.ManaTypeID = manaTypeID;
        LocalManaTypeID = manaTypeID;
        this.MaxManaModifier.clear();
        this.ManaRegenModifier.clear();
        this.MaxManaModifier = ManaRegistries.getMaxManaModifier(manaTypeID);
        this.ManaRegenModifier = ManaRegistries.getManaRegenModifier(manaTypeID);
        this.Dirty = true;
    }

    public double getMaxMana() {
        if (this.player.getWorld().isClient) {
            return MaxManaClient;
        }
        return MaxManaModifier.apply(this.player, 0.0d, this.MaxManaModifierPlayerSide);
    }

    public double getManaRegen() {
        if (this.player.getWorld().isClient) {
            return ManaRegenClient;
        }
        return ManaRegenModifier.apply(this.player, 0.0d, this.ManaRegenModifierPlayerSide);
    }

    public double getMana() {
        return Mana;
    }

    public double setMana(double mana) {
        this.__SetMana__(mana);
        this.Dirty = true;
        return this.Mana;
    }

    public double gainMana(double mana) {
        this.__SetMana__(this.Mana + mana);
        this.Dirty = true;
        return this.Mana;
    }

    public double consumeMana(double mana) {
        this.__SetMana__(this.Mana - mana);
        this.Dirty = true;
        return this.Mana;
    }

    public void gainManaWithTime(double mana, int time) {
        this.mergeTempRegen(mana, time);
        this.Dirty = true;
    }

    public boolean isManaAbove(double mana) {
        return this.Mana > mana;
    }

    private void mergeTempRegen(double newTempRegen, int newTempRegenTime) {
        double remainingRegen = this.tempReganTime * this.tempRegan;
        double totalRegen = remainingRegen + newTempRegen * newTempRegenTime;
        int maxTime = Math.max(this.tempReganTime, newTempRegenTime);
        this.tempRegan = totalRegen / maxTime;
        this.tempReganTime = maxTime;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity otherPlayer) {
        return PlayerSyncPredicate.only(this.player).shouldSyncWith(otherPlayer);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.readFromNbt(nbtCompound, true);
    }

    public void readFromNbt(NbtCompound nbtCompound, Boolean SaveMode) {
        Mana = nbtCompound.getDouble("Mana");
        if (nbtCompound.contains("ManaTypeID")) {
            this.ManaTypeID = new Identifier(nbtCompound.getString("ManaTypeID"));
        } else {
            this.ManaTypeID = null;
        }
        LocalManaTypeID = this.ManaTypeID;
        MaxManaClient = nbtCompound.getDouble("MaxMana");
        ManaRegenClient = nbtCompound.getDouble("ManaRegen");
        tempRegan = nbtCompound.getDouble("tempRegan");
        tempReganTime = nbtCompound.getInt("tempReganTime");

        if (SaveMode) {
            if (nbtCompound.contains("MaxManaModifier")) {
                NbtCompound maxManaCompound = nbtCompound.getCompound("MaxManaModifier");
                this.MaxManaModifier.readFromNbt(maxManaCompound);
            }
            if (nbtCompound.contains("MaxManaModifierPlayerSide")) {
                NbtCompound maxManaPlayerSideCompound = nbtCompound.getCompound("MaxManaModifierPlayerSide");
                this.MaxManaModifierPlayerSide.readFromNbt(maxManaPlayerSideCompound);
            }
            if (nbtCompound.contains("ManaRegenModifier")) {
                NbtCompound manaRegenCompound = nbtCompound.getCompound("ManaRegenModifier");
                this.ManaRegenModifier.readFromNbt(manaRegenCompound);
            }
            if (nbtCompound.contains("ManaRegenModifierPlayerSide")) {
                NbtCompound manaRegenPlayerSideCompound = nbtCompound.getCompound("ManaRegenModifierPlayerSide");
                this.ManaRegenModifierPlayerSide.readFromNbt(manaRegenPlayerSideCompound);
            }
            if (nbtCompound.contains("ManaTypeArray")) {
                NbtList manaTypeArray = nbtCompound.getList("ManaTypeArray", NbtElement.STRING_TYPE);
                for (NbtElement nbtElement : manaTypeArray) {
                    ManaTypeArray.add(new Identifier(nbtElement.asString()));
                }
            }
        }

        this.Dirty = true;
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        this.writeToNbt(nbtCompound, true);
    }

    public void writeToNbt(NbtCompound nbtCompound, Boolean SaveMode) {
        nbtCompound.putDouble("Mana", Mana);
        if (this.ManaTypeID != null) {
            nbtCompound.putString("ManaTypeID", this.ManaTypeID.toString());
        }
        this.Dirty = false;
        nbtCompound.putDouble("MaxMana", MaxManaClient);
        this.MaxManaModifier.needSync = false;
        nbtCompound.putDouble("ManaRegen", ManaRegenClient);
        this.ManaRegenModifier.needSync = false;
        nbtCompound.putDouble("tempRegan", tempRegan);
        nbtCompound.putInt("tempReganTime", tempReganTime);
        if (SaveMode) {
            NbtCompound maxManaCompound = new NbtCompound();
            this.MaxManaModifier.writeToNbt(maxManaCompound);
            nbtCompound.put("MaxManaModifier", maxManaCompound);
            NbtCompound maxManaPlayerSideCompound = new NbtCompound();
            this.MaxManaModifierPlayerSide.writeToNbt(maxManaPlayerSideCompound);
            nbtCompound.put("MaxManaModifierPlayerSide", maxManaPlayerSideCompound);
            NbtCompound manaRegenCompound = new NbtCompound();
            this.ManaRegenModifier.writeToNbt(manaRegenCompound);
            nbtCompound.put("ManaRegenModifier", manaRegenCompound);
            NbtCompound manaRegenPlayerSideCompound = new NbtCompound();
            this.ManaRegenModifierPlayerSide.writeToNbt(manaRegenPlayerSideCompound);
            nbtCompound.put("ManaRegenModifierPlayerSide", manaRegenPlayerSideCompound);
            NbtList manaTypeArray = new NbtList();
            for (Identifier manaType : this.ManaTypeArray) {
                manaTypeArray.add(NbtString.of(manaType.toString()));
            }
            nbtCompound.put("ManaTypeArray", manaTypeArray);
        }
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        NbtCompound tag = new NbtCompound();
        this.writeToNbt(tag, false);
        buf.writeNbt(tag);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        NbtCompound tag = buf.readNbt();
        if (tag != null) {
            this.readFromNbt(tag, false);
        }

    }

    @Override
    public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
        return true;
    }

    @Override
    public void copyForRespawn(ManaComponent original, boolean lossless, boolean keepInventory, boolean sameCharacter) {
        this.copyFrom(original);
    }

    @Override
    public void copyFrom(ManaComponent other) {
        this.Mana = other.Mana;
        this.ManaTypeID = other.ManaTypeID;
        this.ManaTypeArray = other.ManaTypeArray;
        this.MaxManaModifier = other.MaxManaModifier;
        this.MaxManaClient = other.MaxManaClient;
        this.ManaRegenModifier = other.ManaRegenModifier;
        this.ManaRegenClient = other.ManaRegenClient;
        this.tempRegan = other.tempRegan;
        this.tempReganTime = other.tempReganTime;
        this.Dirty = other.Dirty;
    }

    private void __SetMana__(double mana) {
        this.Mana = Math.max(Math.min(mana, this.getMaxMana()), 0.0d);
    }

    private void regenMana() {
        this.__SetMana__(this.Mana + this.getManaRegen() + this.tempRegan);
    }

    public void tick() {
        this.MaxManaClient = this.getMaxMana();
        this.ManaRegenClient = this.getManaRegen();
        this.regenMana();
        if (this.tempRegan != 0) {
            this.tempReganTime--;
            if (this.tempReganTime <= 0) {
                this.tempRegan = 0;
                this.tempReganTime = 0;
                this.Dirty = true;
            }
        }
    }
}
