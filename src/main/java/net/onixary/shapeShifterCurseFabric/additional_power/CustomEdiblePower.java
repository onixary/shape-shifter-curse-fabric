package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;

import java.util.List;

public class CustomEdiblePower extends Power {

    private final FoodComponent foodComponent;
    private final List<Identifier> ItemIdList;

    public CustomEdiblePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.ItemIdList = data.get("item_id_list");
        FoodComponent.Builder foodComponentBuilder = new FoodComponent.Builder()
                .hunger(data.getInt("hunger"))
                .saturationModifier(data.getFloat("saturation_modifier"));
        if (data.getBoolean("meat")) {
            foodComponentBuilder.meat();
        }
        if (data.getBoolean("always_edible")) {
            foodComponentBuilder.alwaysEdible();
        }
        if (data.getBoolean("snack")) {
            foodComponentBuilder.snack();
        }
        List<StatusEffectInstance> effects = data.get("status_effects");
        if (effects != null) {
            for (StatusEffectInstance effect : effects) {
                // 应该使用这个功能的都是需求100%触发效果的 所以这里直接1.0f
                foodComponentBuilder.statusEffect(effect, 1.0f);
            }
        }
        this.foodComponent = foodComponentBuilder.build();
    }

    public List<Identifier> getItemIdList() {
        return this.ItemIdList;
    }

    public FoodComponent getFoodComponent() {
        return this.foodComponent;
    }

    // TODO 挂载能力
    public void OnAddedPowerBothSide() {
        ShapeShifterCurseFabric.LOGGER.info("Added power custom_edible to {}. IsClient: {}", this.entity.getName(), this.entity.getWorld().isClient);
        if (this.entity instanceof PlayerEntity playerEntity) {
            CustomEdibleUtils.addCustomEdibleWithList(playerEntity, this.getItemIdList(), this.getFoodComponent());
        }
    }

    // TODO 移除能力
    public void onRemovedPowerBothSide() {
        ShapeShifterCurseFabric.LOGGER.info("Removed power custom_edible from {}. IsClient: {}", this.entity.getName(), this.entity.getWorld().isClient);
        if (this.entity instanceof PlayerEntity playerEntity) {
            CustomEdibleUtils.clearCustomEdibleWithList(playerEntity, this.getItemIdList());
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("custom_edible"),
                new SerializableData()
                        .add("item_id_list", SerializableDataTypes.IDENTIFIERS, null)
                        .add("hunger", SerializableDataTypes.INT, 0)
                        .add("saturation_modifier", SerializableDataTypes.FLOAT, 0.0f)
                        .add("meat", SerializableDataTypes.BOOLEAN, false)
                        .add("always_edible", SerializableDataTypes.BOOLEAN, false)
                        .add("snack", SerializableDataTypes.BOOLEAN, false)
                        .add("status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
                data -> (type, entity) -> new CustomEdiblePower(type, entity, data)
        );
    }
}