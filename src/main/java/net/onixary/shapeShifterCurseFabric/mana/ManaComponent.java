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
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// 试试这个实验性接口 省的我缓存ModifierList了
// 所有的公开Field尽量使用Class里的Method修改 直接修改Field可能会导致一些奇怪的同步Bug
public class ManaComponent implements AutoSyncedComponent, PlayerComponent<ManaComponent> {

    // 更新等级介绍 此更新指的是根据其他变量重新赋值 会清空之前的修改:

    // 仅初始化更新 -> 在ManaComponent初始化时更新一次 之后逻辑上不会修改 同步时不传输
    // 常更新 平均每tick都更新 -> 如果是Object类型尽量不要修改(改了也会刷新) 看具体Field区分是否同步时传输
    // 仅切换ManaTypeID时更新 -> 切换ManaTypeID时更新 加载时不会更新 一般是持久化存储 同步时不传输
    // 不会更新 -> 持久化存储 不会更新 同步时不传输
    // 同步时更新 -> 不会持久化存储 在触发同步时更新 看具体Field区分是否同步时传输

    // 仅客户端 同步时更新
    public static Identifier LocalManaTypeID = null;  // 仅客户端 怎么想都不会出现在服务器端上 虽然服务器上的数据也会更新 我懒得给readFromNbt写客户端判断了 同步时更新

    public int rid1;
    public int rid2;

    public final @NotNull PlayerEntity player;
    public final boolean isClient;
    // 双端 常更新
    private double ManaBeforeRegen = 0.0d;
    // 双端 常更新
    public double Mana = 0.0d;
    // 双端 常更新
    public @Nullable Identifier ManaTypeID = null;
    // 双端 常更新
    public @NotNull HashMap<Identifier, List<Identifier>> ManaTypeSourceMap = new HashMap<>();
    // 仅服务器端 仅切换ManaTypeID时更新
    public @NotNull ManaUtils.ModifierList MaxManaModifier = new ManaUtils.ModifierList();
    // 仅服务器端 不会更新
    public @NotNull ManaUtils.ModifierList MaxManaModifierPlayerSide = new ManaUtils.ModifierList();
    // 双端 常更新 客户端->获取对应值 服务器端->获取对应值缓存
    public double MaxManaClient = 0.0d;
    // 仅服务器端 仅切换ManaTypeID时更新
    public @NotNull ManaUtils.ModifierList ManaRegenModifier = new ManaUtils.ModifierList();
    // 仅服务器端 不会更新
    public @NotNull ManaUtils.ModifierList ManaRegenModifierPlayerSide = new ManaUtils.ModifierList();
    // 双端 常更新 客户端->获取对应值 服务器端->获取对应值缓存
    public double ManaRegenClient = 0.0d;
    // 仅服务器端 常更新 客户端没用
    public boolean Dirty = false;
    // 双端 常更新
    public double tempRegen = 0.0d;
    // 双端 常更新
    public int tempRegenTime = 0;

    // 双端 同步时更新
    public @NotNull ManaHandler manaHandler = ManaRegistries.EMPTY_MANA_HANDLER;
    // 双端 不会更新 这个Field应该仅ManaComponent内部调用
    private boolean ManaHandler_IsManaFull = false;
    // 双端 不会更新 这个Field应该仅ManaComponent内部调用
    private boolean ManaHandler_IsManaEmpty = false;

    public ManaComponent() {
        ShapeShifterCurseFabric.LOGGER.error("ManaComponent: You should not create a ManaComponent without a player entity!");
        this.player = null;
        this.isClient = false;
        this.rid1 = -1;
        this.rid2 = -1;
    }

    public ManaComponent(PlayerEntity player) {
        this.player = Objects.requireNonNull(player);
        this.isClient = this.player.getWorld().isClient;
        this.rid1 = this.player.getRandom().nextInt();
        this.rid2 = this.player.getRandom().nextInt();
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

    public void gainManaTypeID(@NotNull Identifier manaTypeID, @NotNull Identifier manaSourceID) {
        if (!ManaTypeSourceMap.computeIfAbsent(manaTypeID, k -> new ArrayList<>()).contains(manaSourceID)) {
            ManaTypeSourceMap.get(manaTypeID).add(manaSourceID);
        }
        this.__setManaTypeID__(manaTypeID);
    }

    public void loseManaTypeID(@NotNull Identifier manaTypeID, @NotNull Identifier manaSourceID) {
        if (ManaTypeSourceMap.computeIfAbsent(manaTypeID, k -> new ArrayList<>()).contains(manaSourceID)) {
            ManaTypeSourceMap.get(manaTypeID).remove(manaSourceID);
        }
        if (ManaTypeSourceMap.get(manaTypeID).isEmpty()) {
            ManaTypeSourceMap.remove(manaTypeID);
        }
        // 从Map里加载一个
        for (Identifier id : ManaTypeSourceMap.keySet()) {
            if (!ManaTypeSourceMap.get(id).isEmpty()) {
                this.__setManaTypeID__(id);
                return;
            } else {
                ManaTypeSourceMap.remove(id);
            }
        }
        this.__setManaTypeID__(null);
    }

    public void setManaTypeID(@Nullable Identifier manaTypeID) {
        ManaTypeSourceMap.clear();
        this.__setManaTypeID__(manaTypeID);
    }

    public boolean isManaTypeExists(@NotNull Identifier manaTypeID, @Nullable Identifier source) {
        if (ManaTypeSourceMap.containsKey(manaTypeID)) {
            if (source == null) {
                return true;
            } else {
                return ManaTypeSourceMap.get(manaTypeID).contains(source);
            }
        }
        return false;
    }

    private void __reloadManaHandler__(@Nullable Identifier manaTypeID) {
        this.manaHandler = ManaRegistries.getManaHandlerOrDefault(manaTypeID);
    }

    private void __setManaTypeID__(@Nullable Identifier manaTypeID) {
        if (Objects.equals(this.ManaTypeID, manaTypeID)) {
            return;
        }
        this.ManaTypeID = manaTypeID;
        if (ClientUtils.IsNowPlayingPlayer(this.player)) {
            LocalManaTypeID = this.ManaTypeID;
        }
        this.MaxManaModifier.clear();
        this.ManaRegenModifier.clear();
        this.MaxManaModifier = ManaRegistries.getMaxManaModifier(manaTypeID);
        this.ManaRegenModifier = ManaRegistries.getManaRegenModifier(manaTypeID);
        this.__reloadManaHandler__(manaTypeID);
        this.onCommonManaBarChange();
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
        double remainingRegen = this.tempRegenTime * this.tempRegen;
        double totalRegen = remainingRegen + newTempRegen * newTempRegenTime;
        int maxTime = Math.max(this.tempRegenTime, newTempRegenTime);
        // 除0问题
        if (maxTime == 0) {
            this.tempRegen = 0.0d;
            this.tempRegenTime = 0;
            return;
        }
        this.tempRegen = totalRegen / maxTime;
        if (this.tempRegen == 0.0d) {
            this.tempRegenTime = 0;
        } else {
            this.tempRegenTime = maxTime;
        }
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
        ManaBeforeRegen = nbtCompound.getDouble("ManaBeforeRegen");
        Mana = nbtCompound.getDouble("Mana");
        this.checkManaHook();
        if (nbtCompound.contains("ManaTypeID")) {
            // this.__setManaTypeID__(Identifier.tryParse(nbtCompound.getString("ManaTypeID")));
            this.ManaTypeID = Identifier.tryParse(nbtCompound.getString("ManaTypeID"));
        } else {
            // this.__setManaTypeID__(null);
            this.ManaTypeID = null;
        }
        this.__reloadManaHandler__(this.ManaTypeID);
        if (ClientUtils.IsNowPlayingPlayer(this.player)) {
            LocalManaTypeID = this.ManaTypeID;
        }
        MaxManaClient = nbtCompound.getDouble("MaxMana");
        ManaRegenClient = nbtCompound.getDouble("ManaRegen");
        tempRegen = nbtCompound.getDouble("tempRegen");
        tempRegenTime = nbtCompound.getInt("tempRegenTime");
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
            if (nbtCompound.contains("ManaTypeSourceMap")) {
                ManaTypeSourceMap.clear();
                NbtCompound manaTypeMap = nbtCompound.getCompound("ManaTypeSourceMap");
                for (String manaTypeID : manaTypeMap.getKeys()) {
                    NbtList manaTypeSourceID = manaTypeMap.getList(manaTypeID, NbtElement.STRING_TYPE);
                    ManaTypeSourceMap.computeIfAbsent(Identifier.tryParse(manaTypeID), k -> new ArrayList<>()).addAll(manaTypeSourceID.stream().map(NbtElement::asString).map(Identifier::tryParse).toList());
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
        nbtCompound.putDouble("ManaBeforeRegen", this.ManaBeforeRegen);
        nbtCompound.putDouble("Mana", Mana);
        if (this.ManaTypeID != null) {
            nbtCompound.putString("ManaTypeID", this.ManaTypeID.toString());
        }
        this.Dirty = false;
        nbtCompound.putDouble("MaxMana", MaxManaClient);
        this.MaxManaModifier.needSync = false;
        nbtCompound.putDouble("ManaRegen", ManaRegenClient);
        this.ManaRegenModifier.needSync = false;
        nbtCompound.putDouble("tempRegen", tempRegen);
        nbtCompound.putInt("tempRegenTime", tempRegenTime);
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
            NbtCompound manaTypeMap = new NbtCompound();
            for (Identifier manaType : this.ManaTypeSourceMap.keySet()) {
                NbtList manaTypeSourceID = new NbtList();
                manaTypeSourceID.addAll(this.ManaTypeSourceMap.get(manaType).stream().map(identifier -> NbtString.of(identifier.toString())).toList());
                manaTypeMap.put(manaType.toString(), manaTypeSourceID);
            }
            nbtCompound.put("ManaTypeSourceMap", manaTypeMap);
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
        this.rid2 = other.rid2;
        this.Mana = other.Mana;
        this.ManaTypeID = other.ManaTypeID;
        this.ManaTypeSourceMap = other.ManaTypeSourceMap;
        this.MaxManaModifier = other.MaxManaModifier;
        this.MaxManaModifierPlayerSide = other.MaxManaModifierPlayerSide;
        this.MaxManaClient = other.MaxManaClient;
        this.ManaRegenModifier = other.ManaRegenModifier;
        this.ManaRegenModifierPlayerSide = other.ManaRegenModifierPlayerSide;
        this.ManaRegenClient = other.ManaRegenClient;
        this.tempRegen = other.tempRegen;
        this.tempRegenTime = other.tempRegenTime;
        this.Dirty = other.Dirty;
    }

    private void __SetMana__(double mana) {
        this.Mana = Math.max(Math.min(mana, this.getMaxMana()), 0.0d);
        this.onCommonManaChange();
    }

    private void regenMana() {
        this.ManaBeforeRegen = this.Mana;
        this.__SetMana__(this.Mana + this.getManaRegen() + this.tempRegen);
    }

    public void tick() {
        // 更新属性
        this.MaxManaClient = this.getMaxMana();
        this.ManaRegenClient = this.getManaRegen();
        // 每Tick调用一次 onCommonTick 如果有其他钩子可以使用MixinInterface解决
        this.onCommonTick();
        // 回复魔力逻辑
        this.regenMana();  // 除发魔力变更 就算为0也会触发
        if (this.tempRegen != 0) {
            this.tempRegenTime--;
            if (this.tempRegenTime <= 0) {
                this.tempRegen = 0;
                this.tempRegenTime = 0;
                this.Dirty = true;
            }
        }
    }

    public void onCommonManaBarChange() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        if (this.isClient) {
            this.onClientManaBarChange(this.player);
        } else {
            this.onServerManaBarChange(this.player);
        }
    }

    public void onClientManaBarChange(PlayerEntity clientPlayerEntity) {
        manaHandler.getOnClientInit().accept(this, clientPlayerEntity);
    }

    public void onServerManaBarChange(PlayerEntity serverPlayerEntity) {
        manaHandler.getOnServerInit().accept(this, serverPlayerEntity);
    }

    public void onCommonManaChange() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        if (this.isClient) {
            this.onClientManaChange(this.player);
        } else {
            this.onServerManaChange(this.player);
        }
        this.checkManaHook();
    }

    public void checkManaHook() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        double CheckManaMax = Math.max(this.Mana, this.ManaBeforeRegen);
        if (CheckManaMax >= this.MaxManaClient) {
            if (!this.ManaHandler_IsManaFull) {
                this.ManaHandler_IsManaFull = true;
                this.onCommonManaFull();
            }
        } else {
            this.ManaHandler_IsManaFull = false;
        }
        double CheckManaMin = Math.min(this.Mana, this.ManaBeforeRegen);
        if (CheckManaMin <= 0) {
            if (!this.ManaHandler_IsManaEmpty) {
                this.ManaHandler_IsManaEmpty = true;
                this.onCommonManaEmpty();
            }
        } else {
            this.ManaHandler_IsManaEmpty = false;
        }
    }

    public void onServerManaChange(PlayerEntity serverPlayerEntity) {
        manaHandler.getOnServerManaChange().accept(this, serverPlayerEntity);
    }

    public void onClientManaChange(PlayerEntity clientPlayerEntity) {
        manaHandler.getOnClientManaChange().accept(this, clientPlayerEntity);
    }

    public void onCommonTick() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        if (this.isClient) {
            this.onClientTick(this.player);
        } else {
            this.onServerTick(this.player);
        }
    }

    public void onServerTick(PlayerEntity serverPlayerEntity) {
        manaHandler.getOnServerManaTick().accept(this, serverPlayerEntity);
    }

    public void onClientTick(PlayerEntity clientPlayerEntity) {
        manaHandler.getOnClientManaTick().accept(this, clientPlayerEntity);
    }

    public void onCommonManaFull() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        if (this.isClient) {
            this.onClientManaFull(this.player);
        } else {
            this.onServerManaFull(this.player);
        }
    }

    public void onServerManaFull(PlayerEntity serverPlayerEntity) {
        manaHandler.getOnServerManaFull().accept(this, serverPlayerEntity);
    }

    public void onClientManaFull(PlayerEntity clientPlayerEntity) {
        manaHandler.getOnClientManaFull().accept(this, clientPlayerEntity);
    }

    public void onCommonManaEmpty() {
        if (this.manaHandler == ManaRegistries.EMPTY_MANA_HANDLER) return;
        if (this.isClient) {
            this.onClientManaEmpty(this.player);
        } else {
            this.onServerManaEmpty(this.player);
        }
    }

    public void onServerManaEmpty(PlayerEntity serverPlayerEntity) {
        manaHandler.getOnServerManaEmpty().accept(this, serverPlayerEntity);
    }

    public void onClientManaEmpty(PlayerEntity clientPlayerEntity) {
        manaHandler.getOnClientManaEmpty().accept(this, clientPlayerEntity);
    }
}
