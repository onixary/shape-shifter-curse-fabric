package net.onixary.shapeShifterCurseFabric.recipes.alter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;

import java.util.List;
import java.util.function.*;

public abstract class BuiltinAlterRecipe {
    private static record BARecipeConfig (
            BiPredicate<AlterBlockEntity, World> match,
            BiFunction<AlterBlockEntity, DynamicRegistryManager, ItemStack> craft,
            Function<DynamicRegistryManager, ItemStack> virtualOutput,
            Predicate<PlayerEntity> canCraft,
            int recipeTime,
            int fuelUsage,
            Predicate<AlterBlockEntity> isInputsCountEnough,
            Consumer<AlterBlockEntity> consumeInputs,
            Function<AlterBlockEntity, List<ItemStack>> extraOutput
    ) {}
}
