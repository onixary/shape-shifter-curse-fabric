package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.ArrayList;
import java.util.function.Predicate;

// 蓄力Power API写的最全的一个 如果想用请测试

public class ChargePower extends Power implements Active {
    public static class ChargeTier {
        public int tier;
        public boolean enable;
        public int chargeTime;
        public Predicate<Entity> condition;
        public Predicate<Entity> canChargeCondition;
        public Predicate<Entity> autoFireCondition;
        public ActionFactory<Entity>.Instance useAction;
        public ActionFactory<Entity>.Instance tickAction;
        public ActionFactory<Entity>.Instance chargeTickAction;
        public ActionFactory<Entity>.Instance chargeCompleteAction;
        public ActionFactory<Entity>.Instance chargeCompleteUseAction;
        public ActionFactory<Entity>.Instance chargeCompleteTickAction;
        public int cooldown;

        public ChargeTier(SerializableData.Instance data, int index) {
            this.tier = index;
            this.enable = data.getBoolean(String.format("tier%d_enable", index));
            this.chargeTime = data.getInt(String.format("tier%d_charge_time", index));
            this.condition = data.get(String.format("tier%d_condition", index));
            this.canChargeCondition = data.get(String.format("tier%d_can_charge_condition", index));
            this.autoFireCondition = data.get(String.format("tier%d_auto_fire_condition", index));
            this.useAction = data.get(String.format("tier%d_use_action", index));
            this.tickAction = data.get(String.format("tier%d_tick_action", index));
            this.chargeTickAction = data.get(String.format("tier%d_charge_tick_action", index));
            this.chargeCompleteAction = data.get(String.format("tier%d_charge_complete_action", index));
            this.chargeCompleteUseAction = data.get(String.format("tier%d_charge_complete_use_action", index));
            this.chargeCompleteTickAction = data.get(String.format("tier%d_charge_complete_tick_action", index));
            this.cooldown = data.getInt(String.format("tier%d_cooldown", index));
        }

        public void tick(ChargePower power) {
            boolean checkAutoFire = false;
            if (!power.isCharging) {
                return;
            }
            if (power.nowTier + 1 == this.tier) {
                if (this.canChargeCondition != null && !this.canChargeCondition.test(power.entity)) {
                    power.ChargeTime = power.ChargeTime - 1;
                } else {
                    if (this.chargeTickAction != null) {
                        this.chargeTickAction.accept(power.entity);
                    }
                    if (power.ChargeTime >= this.chargeTime) {
                        if (this.condition != null && !this.condition.test(power.entity)) {
                            power.ChargeTime = this.chargeTime - 1;
                        } else {
                            if (this.chargeCompleteAction != null) {
                                this.chargeCompleteAction.accept(power.entity);
                            }
                            power.nowTier = this.tier;
                            power.updateTier();
                            checkAutoFire = true;
                        }
                    }
                }
            }
            if (power.nowTier == this.tier) {
                if (this.tickAction != null) {
                    this.tickAction.accept(power.entity);
                }
            }
            if (power.nowTier >= this.tier) {
                if (this.chargeCompleteTickAction != null) {
                    this.chargeCompleteTickAction.accept(power.entity);
                }
            }
            if (checkAutoFire && this.autoFireCondition != null && this.autoFireCondition.test(power.entity)) {
                power.fire(false);
            }
        }

        public void use(ChargePower power) {
            if (power.nowTier == this.tier) {
                if (this.useAction != null) {
                    this.useAction.accept(power.entity);
                }
                power.nowCooldown = this.cooldown;
            }
            if (power.nowTier >= this.tier) {
                if (this.chargeCompleteUseAction != null) {
                    this.chargeCompleteUseAction.accept(power.entity);
                }
            }
        }
    }

    public static final int TierCount = 10;

    public @Nullable Identifier chargePowerID = null;
    public int nowTier = 0;
    public int renderTier = 0;
    public Key ActiveKey;
    public int ChargeTime = 0;
    public ArrayList<ChargeTier> ChargeTierList = new ArrayList<>();
    public int nowCooldown = 0;

    private boolean isCharging = false;
    private long nowTick = 0;
    private long lastTick = 0;

    public ChargePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        for (int index = 0; index < TierCount; index++) {
            ChargeTier chargeTier = new ChargeTier(data, index);
            if (chargeTier.enable) {
                ChargeTierList.add(chargeTier);
            } else {
                break;
            }
        }
        this.chargePowerID = data.get("charge_power_id");
        this.setKey(data.get("key"));
        this.setTicking(true);
    }

    public void fire(boolean AddTick) {
        this.isCharging = false;
        if (this.ChargeTime > 0) {
            for (ChargeTier chargeTier : ChargeTierList) {
                chargeTier.use(this);
            }
        }
        if (AddTick) {
            this.nowTick += 2;
        }
        this.nowTier = 0;
        this.ChargeTime = 0;
        this.updateTier();
    }

    @Override
    public void tick() {
        if (nowCooldown > 0) {
            nowCooldown--;
        } else {
            nowCooldown = 0;
        }
        if (this.nowTick - this.lastTick > 2) {
            this.fire(false);
        }
        nowTick++;
        for (ChargeTier chargeTier : ChargeTierList) {
            chargeTier.tick(this);
        }
    }

    @Override
    public void onUse() {
        if (nowCooldown > 0) {
            return;
        }
        this.lastTick = nowTick;
        this.isCharging = true;
        this.ChargeTime++;
    }

    @Override
    public Key getKey() {
        return ActiveKey;
    }

    @Override
    public void setKey(Key key) {
        this.ActiveKey = key;
    }

    public void updateTier() {
        this.renderTier = this.nowTier;
        PowerHolderComponent.syncPower(this.entity, this.getType());
    }

    public NbtElement toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("renderTier", this.renderTier);
        return tag;
    }

    public void fromTag(NbtElement tag) {
        this.renderTier = ((NbtCompound) tag).getInt("renderTier");
    }

    public static PowerFactory<?> createFactory() {
        SerializableData factoryJson = new SerializableData()
                .add("charge_power_id", SerializableDataTypes.IDENTIFIER, null)
                .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key());
        for (int index = 0; index < TierCount; index++) {
            factoryJson
                    .add(String.format("tier%d_enable", index), SerializableDataTypes.BOOLEAN, index == 0)  // 是否启用这个阶段
                    .add(String.format("tier%d_charge_time", index), SerializableDataTypes.INT, index == 0 ? 0 : -1)  // 这个阶段需要充能的时间
                    .add(String.format("tier%d_condition", index), ApoliDataTypes.ENTITY_CONDITION, null)  // 是否可以到达这个阶段
                    .add(String.format("tier%d_can_charge_condition", index), ApoliDataTypes.ENTITY_CONDITION, null)
                    .add(String.format("tier%d_auto_fire_condition", index), ApoliDataTypes.ENTITY_CONDITION, null)
                    .add(String.format("tier%d_use_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 到达这个阶段后松下按键时的动作
                    .add(String.format("tier%d_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段每 Tick 执行的动作
                    .add(String.format("tier%d_charge_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 给这个阶段充能时每 Tick 执行的动作
                    .add(String.format("tier%d_charge_complete_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成时执行的动作
                    .add(String.format("tier%d_charge_complete_use_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成后松下按键时的动作(会叠加)
                    .add(String.format("tier%d_charge_complete_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成时每 Tick 执行的动作(会叠加)
                    .add(String.format("tier%d_cooldown", index), SerializableDataTypes.INT, 0);  // 到达这个阶段触发后的冷却时间
        }
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("charge_action"),
                factoryJson,
                data -> (powerType, livingEntity) -> new ChargePower(
                        powerType,
                        livingEntity,
                        data
                ));
    }

}
