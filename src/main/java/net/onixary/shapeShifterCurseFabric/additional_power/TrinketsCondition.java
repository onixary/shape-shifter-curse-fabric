package net.onixary.shapeShifterCurseFabric.additional_power;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Optional;
import java.util.function.Consumer;

public class TrinketsCondition {
    public static boolean isEquipped(Entity entity, Identifier trinketID) {
        if (trinketID == null) {
            return false;
        }
        Optional<Item>  trinketItem = Registries.ITEM.getOrEmpty(trinketID);
        if (!trinketItem.isPresent()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
            if (!component.isPresent()) {
                return false;
            }
            boolean IsEquipped = component.get().isEquipped(trinketItem.get());
            return IsEquipped;
        }
        return false;
    }

    public static void registerCondition(Consumer<ConditionFactory<Entity>> registerFunc) {
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("equip_accessory"),  // 为了之后写双端不用改 还是使用equip_accessories吧
                new SerializableData()
                        .add("accessory", SerializableDataTypes.IDENTIFIER, null),
                (data, e) -> isEquipped(e, data.get("accessory"))
        ));
    }
}
