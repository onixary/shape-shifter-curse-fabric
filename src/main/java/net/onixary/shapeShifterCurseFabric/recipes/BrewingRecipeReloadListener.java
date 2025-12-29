package net.onixary.shapeShifterCurseFabric.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.io.IOException;

public class BrewingRecipeReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(ShapeShifterCurseFabric.MOD_ID, "dynamic_brewing_recipes");
    }

    @Override
    public void reload(ResourceManager manager) {
        BrewingRecipeUtils.onLoadDynamicBrewingRecipesStart();
        manager.findResources("dynamic_brewing_recipes", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
            JsonObject accessoryData;
            try {
                accessoryData = JsonParser.parseString(new String(resource.getInputStream().readAllBytes())).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BrewingRecipeUtils.registerPotionRecipe(accessoryData);
        });
        BrewingRecipeUtils.onLoadDynamicBrewingRecipesEnd();
    }
}
