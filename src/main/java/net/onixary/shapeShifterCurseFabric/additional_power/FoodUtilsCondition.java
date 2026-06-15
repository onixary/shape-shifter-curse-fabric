package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;

public class FoodUtilsCondition {
    private static final String VeganDelightTag = "vegandelight:is_vegan";

    public static boolean FC_isVegan(ItemStack itemStack, boolean Default) {
        NbtCompound itemNBT = itemStack.getNbt();
        if (itemNBT != null) {
            return itemNBT.getByte(VeganDelightTag) == 1;
        }
        return Default;
    }


    public static void registerCondition(Consumer<ConditionFactory<ItemStack>> register) {
        register.accept(
                new ConditionFactory<ItemStack>(
                        ShapeShifterCurseFabric.identifier("is_vegan_ex"),
                        new SerializableData()
                                .add("default", SerializableDataTypes.BOOLEAN, false),
                        (data, itemstack) -> {
                            return FC_isVegan(itemstack, data.getBoolean("default"));
                        }
                )
        );
    }
}
