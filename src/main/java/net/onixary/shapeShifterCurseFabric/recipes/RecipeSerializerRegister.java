package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class RecipeSerializerRegister {
    public static RecipeSerializer<MorphScaleUpgradeRecipe> MORPH_SCALE_UPGRADE = register(ShapeShifterCurseFabric.identifier("morph_scale_upgrade"), new MorphScaleUpgradeRecipe.Serializer());

    public static void register() {
        // 用于加载静态注册
    };


    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(Identifier id, S serializer) {
        return (S)(Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
    };
}
