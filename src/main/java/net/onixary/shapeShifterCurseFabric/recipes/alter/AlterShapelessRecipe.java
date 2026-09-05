package net.onixary.shapeShifterCurseFabric.recipes.alter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister;
import org.jetbrains.annotations.Nullable;

public class AlterShapelessRecipe extends AlterRecipe {
    public final Identifier id;
    public final ItemStack output;
    public final DefaultedList<Ingredient> input;
    public final @Nullable Ingredient catalyst;
    public final int recipeTime;


    public AlterShapelessRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> input, Ingredient catalyst, int recipeTime) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.recipeTime = recipeTime;
        this.catalyst = catalyst;
    }

    @Override
    public int recipeTime() {
        return recipeTime;
    }

    @Override
    public boolean canCraft(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean matches(SidedInventory inventory, World world) {
        if (this.catalyst != null) {
            ItemStack itemStack = inventory.getStack(9);
            if (!this.catalyst.test(itemStack)) {
                return false;
            }
        }

        RecipeMatcher recipeMatcher = new RecipeMatcher();
        int i = 0;
        for(int j = 0; j < 9; ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (!itemStack.isEmpty()) {
                ++i;
                recipeMatcher.addInput(itemStack, 1);
            }
        }

        return i == this.input.size() && recipeMatcher.match(this, (IntList)null);
    }

    @Override
    public ItemStack craft(SidedInventory inventory, DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= this.input.size();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegister.ALTER_SHAPELESS_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<AlterShapelessRecipe> {
        public AlterShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
            int time = JsonHelper.getInt(jsonObject, "time", 200);
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
            Ingredient catalyst = null;
            if (jsonObject.has("catalyst")) {
                catalyst = Ingredient.fromJson(jsonObject.get("catalyst"), true);
            }
            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for alter shapeless recipe");
            } else if (defaultedList.size() > 9) {
                throw new JsonParseException("Too many ingredients for alter shapeless recipe");
            } else {
                ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
                return new AlterShapelessRecipe(identifier, itemStack, defaultedList, catalyst, time);
            }
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();
            for(int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }
            return defaultedList;
        }

        public AlterShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient catalyst = null;
            if (packetByteBuf.readBoolean()) {
                catalyst = Ingredient.fromPacket(packetByteBuf);
            }
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            for(int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
            }
            ItemStack itemStack = packetByteBuf.readItemStack();
            int time = packetByteBuf.readVarInt();
            return new AlterShapelessRecipe(identifier, itemStack, defaultedList, catalyst, time);
        }

        public void write(PacketByteBuf packetByteBuf, AlterShapelessRecipe shapelessRecipe) {
            if (shapelessRecipe.catalyst != null) {
                packetByteBuf.writeBoolean(true);
                shapelessRecipe.catalyst.write(packetByteBuf);
            } else {
                packetByteBuf.writeBoolean(false);
            }
            packetByteBuf.writeVarInt(shapelessRecipe.input.size());
            for(Ingredient ingredient : shapelessRecipe.input) {
                ingredient.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(shapelessRecipe.output);
            packetByteBuf.writeVarInt(shapelessRecipe.recipeTime);
        }
    }
}
