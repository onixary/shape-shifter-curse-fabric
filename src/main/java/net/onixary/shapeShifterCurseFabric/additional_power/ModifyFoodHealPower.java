package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModifyFoodHealPower extends Power {
    // 其实这个可以用一种抽象的方式来实现 比如SelfActionPower检查是否可以回血 如果可以就扣饱食度回血

    private int LastModifyFoodHealTimer = 0;
    private float RemainFoodHealTime = 0.0f;
    private final float FoodTimerAddAmount;
    private final int ModifyFoodTimerTickRate;

    public ModifyFoodHealPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.FoodTimerAddAmount = data.getFloat("food_timer_add_amount");
        this.ModifyFoodTimerTickRate = data.getInt("modify_food_timer_tick_rate");
    }

    public int ProcessFoodTick(int FoodTick) {
        this.LastModifyFoodHealTimer++;
        if (this.LastModifyFoodHealTimer >= this.ModifyFoodTimerTickRate) {
            this.LastModifyFoodHealTimer = 0;
            this.RemainFoodHealTime += this.FoodTimerAddAmount;
            return this.ApplyFoodTick(FoodTick);
        } else {
            return FoodTick;
        }
    }

    public int ApplyFoodTick(int FoodTick) {
        int FoodTickerAmount = (int) this.RemainFoodHealTime;
        if (FoodTickerAmount != 0) {
            // ShapeShifterCurseFabric.LOGGER.info("FoodTickerAmount: {}, RemainFoodHealTime: {}", FoodTickerAmount, this.RemainFoodHealTime);
            this.RemainFoodHealTime -= FoodTickerAmount;
            return Math.max(FoodTick + FoodTickerAmount, 0);
        } else {
            return FoodTick;
        }
    }

    public boolean CanApply(PlayerEntity player) {
        return player.getHungerManager().getFoodLevel() >= 18 && player.canFoodHeal();  // 饱食度大于等于18且可以回血
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_food_heal"),
                new SerializableData()
                        .add("food_timer_add_amount", SerializableDataTypes.FLOAT, 1.0f)
                        .add("modify_food_timer_tick_rate", SerializableDataTypes.INT, 20),
                data -> (powerType, entity) -> new ModifyFoodHealPower(powerType, entity, data)
        ).allowCondition();
    }
}
