package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ModTags;

public class IsMorphScaleItemCondition {
    public static final String IsMorphScaleArmorTagName = "MorphScaleItem";

    public static boolean condition(SerializableData.Instance data, ItemStack itemStack) {
        if (itemStack.isIn(ModTags.MorphScaleItem_Tag)) {
            return true;
        }
        NbtCompound itemNBT = itemStack.getNbt();
        if (itemNBT != null) {
            if (itemNBT.getBoolean(IsMorphScaleArmorTagName)) {
                return true;
            }
        }
        return false;
    }

    public static ConditionFactory<ItemStack> getFactory() {
        return new ConditionFactory<ItemStack>(
                ShapeShifterCurseFabric.identifier("is_morph_scale_item"),
                new SerializableData(),
                IsMorphScaleItemCondition::condition
        );
    }
}
