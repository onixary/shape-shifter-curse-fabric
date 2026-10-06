package net.onixary.shapeShifterCurseFabric.recipes.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.additional_power.IsMorphScaleItemCondition;
import net.onixary.shapeShifterCurseFabric.event.SSCEvent;

public class RegBuiltinAlterRecipe {
    public static final Identifier MorphScaleFood = new Identifier("shape_shifter_curse_fabric", "altar/morph_scale_food");
    public static final BuiltinAltarRecipe.BARecipeConfig MorphScaleFoodConfig = new BuiltinAltarRecipe.BARecipeConfigBuilder()
            .match(
                    (altarBlockEntity, world) -> {
                        int airCount = 0;
                        ItemStack foodStack = null;
                        for (int j = 0; j < 9; ++j) {
                            ItemStack itemStack = altarBlockEntity.getStack(j);
                            if (itemStack.isEmpty()) {
                                ++airCount;
                            } else {
                                foodStack = itemStack;
                            }
                        }
                        if (airCount != 8 || foodStack == null || !foodStack.isFood()) {
                            return false;
                        }
                        NbtCompound foodNBT = foodStack.getNbt();
                        if (foodNBT != null && foodNBT.getBoolean(IsMorphScaleItemCondition.IsMorphScaleFoodTagName)) {
                            return false;
                        }
                        return true;
                    }
            )
            .craft(
                    (altarBlockEntity, registryManager) -> {
                        int airCount = 0;
                        ItemStack foodStack = null;
                        for (int j = 0; j < 9; ++j) {
                            ItemStack itemStack = altarBlockEntity.getStack(j);
                            if (itemStack.isEmpty()) {
                                ++airCount;
                            } else {
                                foodStack = itemStack;
                            }
                        }
                        if (airCount != 8 || foodStack == null || !foodStack.isFood()) {
                            return ItemStack.EMPTY;
                        }
                        ItemStack output = foodStack.copyWithCount(1);
                        NbtCompound nbtCompound = output.getOrCreateNbt();
                        nbtCompound.putBoolean(IsMorphScaleItemCondition.IsMorphScaleFoodTagName, true);
                        return output;
                    }
            )
            .consumeInputs(
                    altarBlockEntity -> {
                        for (int j = 0; j < 9; ++j) {
                            ItemStack itemStack = altarBlockEntity.getStack(j);
                            if (!itemStack.isEmpty()) altarBlockEntity.removeStack(j, 1);
                        }
                    }
            )
            .fuelUsage(1)
            .recipeTime(200)
            .virtualOutput(
                    registryManager -> {
                        ItemStack example = new ItemStack(Items.APPLE);
                        NbtCompound nbtCompound = example.getOrCreateNbt();
                        nbtCompound.putBoolean(IsMorphScaleItemCondition.IsMorphScaleFoodTagName, true);
                        return example;
                    }
            )
            .build();

    public static final BuiltinAltarRecipe MorphScaleFoodRecipe = new BuiltinAltarRecipe(MorphScaleFood, MorphScaleFoodConfig);

    static {
        MorphScaleFoodConfig.register(MorphScaleFood);
    }

    public static void init() {
        SSCEvent.BEFORE_APPLY_RECIPE.register(
                register -> {
                    register.accept(MorphScaleFood, MorphScaleFoodRecipe);
                }
        );
    }
}
