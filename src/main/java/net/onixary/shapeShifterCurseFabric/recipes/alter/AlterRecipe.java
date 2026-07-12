package net.onixary.shapeShifterCurseFabric.recipes.alter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.recipes.MorphScaleUpgradeRecipe;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

// 类熔炉配方 多输入物品 单种燃料 多输出物品
public class AlterRecipe implements Recipe<Inventory> {
    public static final RecipeType<AlterRecipe> ALTER_RECIPE = RecipeUtils.registerRecipeType(ShapeShifterCurseFabric.identifier("alter"));
    public static final Identifier EmptyRecipeId = ShapeShifterCurseFabric.identifier("empty_alter_recipe");

    public static final AlterRecipe EmptyRecipe = new AlterRecipe(EmptyRecipeId, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, (inventory) -> new ArrayList<>(), 0);

    public static final int InputSlotIndex = 0;
    public static final int InputSlotCount = 7;
    public static final int FuelSlotIndex = 7;
    public static final int FuelSlotCount = 1;
    public static final int OutputSlotIndex = 8;
    public static final int OutputSlotCount = 9;

    public final Ingredient input1;
    public final Ingredient input2;
    public final Ingredient input3;
    public final Ingredient input4;
    public final Ingredient input5;
    public final Ingredient input6;
    public final Ingredient input7;
    public final Function<@Nullable Inventory, List<ItemStack>> output;  // 支持从战利品表拉取
    public final int recipeTime;

    public ItemStack VirtualOutput;

    public final Identifier id;

    public AlterRecipe(Identifier id, Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, Ingredient input5, Ingredient input6, Ingredient input7, Function<@Nullable Inventory, List<ItemStack>> output, int recipeTime) {
        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.input5 = input5;
        this.input6 = input6;
        this.input7 = input7;
        this.output = output;
        this.recipeTime = recipeTime;
        this.VirtualOutput = this.getVirtualOutput(null);
        List<ItemStack> list = output.apply(null);
        if (list.size() > 9) {
            ShapeShifterCurseFabric.LOGGER.warn("AlterRecipe " + id + " has more than 9 outputs!");  // 警告一下 防止吞物品
        }
    }

    public ItemStack getVirtualOutput(@Nullable Inventory inventory) {
        List<ItemStack> list = output.apply(inventory);
        if (!list.isEmpty()) {
            if (list.size() >= 5) {
                return list.get(4);
            } else {
                return list.get(0);
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (this.id.equals(EmptyRecipeId)) {
            return false;
        }
        boolean noPass = false;
        noPass |= !this.input1.test(inventory.getStack(InputSlotIndex + 0));
        noPass |= !this.input2.test(inventory.getStack(InputSlotIndex + 1));
        noPass |= !this.input3.test(inventory.getStack(InputSlotIndex + 2));
        noPass |= !this.input4.test(inventory.getStack(InputSlotIndex + 3));
        noPass |= !this.input5.test(inventory.getStack(InputSlotIndex + 4));
        noPass |= !this.input6.test(inventory.getStack(InputSlotIndex + 5));
        noPass |= !this.input7.test(inventory.getStack(InputSlotIndex + 6));
        return !noPass;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.getVirtualOutput(inventory);
    }

    @Override
    public boolean fits(int width, int height) {
        return !this.id.equals(EmptyRecipeId);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.VirtualOutput;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegister.ALTER_RECIPE;
    }

    @Override
    public RecipeType<?> getType() {
        return ALTER_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<AlterRecipe> {
        public AlterRecipe read(Identifier identifier, JsonObject jsonObject) {
            try {
                Ingredient input1 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input1"));
                Ingredient input2 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input2"));
                Ingredient input3 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input3"));
                Ingredient input4 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input4"));
                Ingredient input5 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input5"));
                Ingredient input6 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input6"));
                Ingredient input7 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "input7"));
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "output");
                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); ++i) {
                    list.add(ShapedRecipe.outputFromJson(jsonArray.get(i).getAsJsonObject()));
                }
                Function<@Nullable Inventory, List<ItemStack>> function = inventory -> list;
                int recipeTime = JsonHelper.getInt(jsonObject, "recipeTime", 200);
                return new AlterRecipe(identifier, input1, input2, input3, input4, input5, input6, input7, function, recipeTime);
            } catch (Exception exception) {
                ShapeShifterCurseFabric.LOGGER.warn("Couldn't read recipe " + identifier, exception);
                return EmptyRecipe;
            }
        }

        public AlterRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            try {
                Ingredient input1 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input2 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input3 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input4 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input5 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input6 = Ingredient.fromPacket(packetByteBuf);
                Ingredient input7 = Ingredient.fromPacket(packetByteBuf);
                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < packetByteBuf.readVarInt(); ++i) {
                    list.add(packetByteBuf.readItemStack());
                }
                Function<@Nullable Inventory, List<ItemStack>> function = inventory -> list;
                int recipeTime = packetByteBuf.readVarInt();
                return new AlterRecipe(identifier, input1, input2, input3, input4, input5, input6, input7, function, recipeTime);
            } catch (Exception exception) {
                ShapeShifterCurseFabric.LOGGER.warn("Couldn't read recipe " + identifier, exception);
                return EmptyRecipe;
            }
        }

        public void write(PacketByteBuf packetByteBuf, AlterRecipe alterRecipe) {
            alterRecipe.input1.write(packetByteBuf);
            alterRecipe.input2.write(packetByteBuf);
            alterRecipe.input3.write(packetByteBuf);
            alterRecipe.input4.write(packetByteBuf);
            alterRecipe.input5.write(packetByteBuf);
            alterRecipe.input6.write(packetByteBuf);
            alterRecipe.input7.write(packetByteBuf);
            List<ItemStack> list = alterRecipe.output.apply(null);
            packetByteBuf.writeVarInt(list.size());
            for (ItemStack itemStack : list) {
                packetByteBuf.writeItemStack(itemStack);
            }
            packetByteBuf.writeVarInt(alterRecipe.recipeTime);
        }
    }
}
