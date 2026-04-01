package net.onixary.shapeShifterCurseFabric.additional_power;

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
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

public class ChargePower extends Power implements Active {
    public static class ChargeTier {
        public int tier;
        public boolean enable;
        public int chargeTime;
        public Predicate<Entity> condition;
        public ActionFactory<Entity>.Instance useAction;
        public ActionFactory<Entity>.Instance tickAction;
        public ActionFactory<Entity>.Instance chargeTickAction;
        public ActionFactory<Entity>.Instance chargeCompleteAction;
        public ActionFactory<Entity>.Instance chargeCompleteUseAction;
        public ActionFactory<Entity>.Instance chargeCompleteTickAction;
        public int cooldown;

        public ChargeTier(SerializableData.Instance data, int index) {
            this.tier = index;
            this.enable = data.getBoolean(String.format("tier{%d}_enable", index));
            this.chargeTime = data.getInt(String.format("tier{%d}_charge_time", index));
            this.condition = data.get(String.format("tier{%d}_condition", index));
            this.useAction = data.get(String.format("tier{%d}_use_action", index));
            this.tickAction = data.get(String.format("tier{%d}_tick_action", index));
            this.chargeTickAction = data.get(String.format("tier{%d}_charge_tick_action", index));
            this.chargeCompleteAction = data.get(String.format("tier{%d}_charge_complete_action", index));
            this.chargeCompleteUseAction = data.get(String.format("tier{%d}_charge_complete_use_action", index));
            this.chargeCompleteTickAction = data.get(String.format("tier{%d}_charge_complete_tick_action", index));
            this.cooldown = data.getInt(String.format("tier{%d}_cooldown", index));
        }

        public void tick(ChargePower power) {
            // TODO
        }

        public void use(ChargePower power) {
            // TODO
        }
    }

    public static final int TierCount = 5;

    public @Nullable Identifier chargePowerID;
    public int nowTier = 0;
    public Key ActiveKey;
    public int ChargeTime = 0;
    public ArrayList<ChargeTier> ChargeTierList;
    public int nowCooldown = 0;

    public ChargePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.setTicking();
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
    }

    @Override
    public void tick() {
        if (nowCooldown > 0) {
            nowCooldown--;
        } else {
            nowCooldown = 0;
        }
        ChargeTime++;
        for (ChargeTier chargeTier : ChargeTierList) {
            chargeTier.tick(this);
        }
    }

    @Override
    public void onUse() {
        if (nowCooldown > 0) {
            return;
        }
        for (ChargeTier chargeTier : ChargeTierList) {
            chargeTier.use(this);
        }
        ChargeTime = 0;
    }

    @Override
    public Key getKey() {
        return ActiveKey;
    }

    @Override
    public void setKey(Key key) {
        this.ActiveKey = key;
    }

    public static PowerFactory<?> createFactory() {
        SerializableData factoryJson = new SerializableData()
                .add("charge_power_id", SerializableDataTypes.IDENTIFIER, null)
                .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key());
        for (int index = 0; index < TierCount; index++) {
            factoryJson
                    .add(String.format("tier{%d}_enable", index), SerializableDataTypes.BOOLEAN, index == 0)  // 是否启用这个阶段
                    .add(String.format("tier{%d}_charge_time", index), SerializableDataTypes.INT, -1)  // 这个阶段需要充能的时间
                    .add(String.format("tier{%d}_condition", index), ApoliDataTypes.ENTITY_CONDITION, null)  // 是否可以到达这个阶段
                    .add(String.format("tier{%d}_use_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 到达这个阶段后松下按键时的动作
                    .add(String.format("tier{%d}_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段每 Tick 执行的动作
                    .add(String.format("tier{%d}_charge_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 给这个阶段充能时每 Tick 执行的动作
                    .add(String.format("tier{%d}_charge_complete_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成时执行的动作
                    .add(String.format("tier{%d}_charge_complete_use_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成后松下按键时的动作(会叠加)
                    .add(String.format("tier{%d}_charge_complete_tick_action", index), ApoliDataTypes.ENTITY_ACTION, null)  // 这个阶段充能完成时每 Tick 执行的动作(会叠加)
                    .add(String.format("tier{%d}_cooldown", index), SerializableDataTypes.INT, 0);  // 到达这个阶段触发后的冷却时间
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
