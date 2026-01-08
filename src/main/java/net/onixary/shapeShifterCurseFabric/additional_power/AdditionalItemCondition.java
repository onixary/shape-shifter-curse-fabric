package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Collection;

public class AdditionalItemCondition {
    public static void register() {
        register(IsMorphScaleItemCondition.getFactory());
        register(new ConditionFactory<ItemStack>(
                ShapeShifterCurseFabric.identifier("is_weapon"),
                new SerializableData(),
                (data, itemstack) -> {
                    Collection<EntityAttributeModifier> modifiers = itemstack.getItem().getAttributeModifiers(itemstack, EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    double totalAdd = 0;
                    for (EntityAttributeModifier modifier : modifiers) {
                        if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                            totalAdd += modifier.getValue();
                        }
                    }
                    return totalAdd > 1;
                }
        ));
    }

    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);

    }
}
