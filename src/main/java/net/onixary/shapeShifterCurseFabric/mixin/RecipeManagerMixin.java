package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.onixary.shapeShifterCurseFabric.event.SSCEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Unique
    private void registerRecipe(
            Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipeBuilder,
            ImmutableMap.Builder<Identifier, Recipe<?>> recipeIDBuilder,
            @Nullable Identifier recipeID, @NotNull Recipe<?> recipe)
    {
        if (recipeID == null) {
            recipeID = recipe.getId();
        }
        if (recipeID == null) {
            return;
        }
        if (recipe.getType() == null) {
            return;
        }
        ((ImmutableMap.Builder<Identifier, Recipe<?>>) recipeBuilder.computeIfAbsent(recipe.getType(), (recipeType) -> ImmutableMap.builder())).put(recipeID, recipe);
        recipeIDBuilder.put(recipeID, recipe);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0))
    private void onApply(
            Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci,
            @Local(ordinal = 1) Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipeBuilder,
            @Local(ordinal = 0) ImmutableMap.Builder<Identifier, Recipe<?>> recipeIDBuilder
    ) {
        SSCEvent.BEFORE_APPLY_RECIPE.invoker().beforeApplyRecipe(
                (recipeID, recipe) -> registerRecipe(recipeBuilder, recipeIDBuilder, recipeID, recipe)
        );
    }
}
