package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class CocoonLootChancePower extends Power {
    private final int chance;
    public CocoonLootChancePower(PowerType<?> type, LivingEntity entity, int chance) {
        super(type, entity);
        this.chance = Math.max(0, Math.min(100, chance));
    }
    public static int getChance(LivingEntity entity) {
        return PowerHolderComponent.getPowers(entity, CocoonLootChancePower.class).stream()
                .mapToInt(p -> p.chance).max().orElse(40);
    }
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("cocoon_loot_chance"),
                new SerializableData().add("chance", SerializableDataTypes.INT, 40),
                data -> (type, entity) -> new CocoonLootChancePower(type, entity, data.getInt("chance"))).allowCondition();
    }
}
