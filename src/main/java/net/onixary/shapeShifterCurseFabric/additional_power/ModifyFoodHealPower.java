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
    private final int FoodTimerAddAmount;
    private final int ModifyFoodTimerTickRate;

    public ModifyFoodHealPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.FoodTimerAddAmount = data.getInt("food_timer_add_amount");
        this.ModifyFoodTimerTickRate = data.getInt("modify_food_timer_tick_rate");
    }

    public int ProcessFoodTick(int FoodTick) {
        this.LastModifyFoodHealTimer++;
        if (this.LastModifyFoodHealTimer >= this.ModifyFoodTimerTickRate) {
            this.LastModifyFoodHealTimer = 0;
            return FoodTick + this.FoodTimerAddAmount;
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
                        .add("food_timer_add_amount", SerializableDataTypes.INT, 1)
                        .add("modify_food_timer_tick_rate", SerializableDataTypes.INT, 20),
                data -> (powerType, entity) -> new ModifyFoodHealPower(powerType, entity, data)
        ).allowCondition();
    }
}
