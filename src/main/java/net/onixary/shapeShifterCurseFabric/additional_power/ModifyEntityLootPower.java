package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;

public class ModifyEntityLootPower extends Power {
    private final ConditionFactory<ItemStack>.Instance FromItemCondition;
    private final float chance;
    private final Item targetItem;
    private final ItemStack targetItemStack;

    public ModifyEntityLootPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.FromItemCondition = data.get("from_item_condition");
        this.chance = data.get("chance");
        this.targetItem = data.get("target_item");
        this.targetItemStack = data.get("target_item_stack");
    }

    public ItemStack ApplyModifyDrop(ItemStack itemStack, Random randomSource) {
        if (!FromItemCondition.test(itemStack)) {
            return itemStack;
        }
        if (randomSource.nextFloat() < this.chance) {
            if (targetItem != null) {
                ItemStack finalItemStack = new ItemStack(targetItem, itemStack.getCount());
                finalItemStack.setNbt(itemStack.getNbt());
                return finalItemStack;
            } else if (targetItemStack != null) {
                return targetItemStack.copy();
            }
        }
        return itemStack;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_entity_loot"),
                new SerializableData()
                        .add("from_item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("chance", SerializableDataTypes.FLOAT, 0.0f)
                        .add("target_item", SerializableDataTypes.ITEM, null)  // 保留除了物品的所有数据
                        .add("target_item_stack", SerializableDataTypes.ITEM_STACK, null),  // 直接修改掉落物品
                data -> (powerType, entity) -> new ModifyEntityLootPower(powerType, entity, data)
        ).allowCondition();
    }
}
