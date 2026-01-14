package net.onixary.shapeShifterCurseFabric.recipes;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import static net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister.MORPH_SCALE_UPGRADE;

public class MorphScaleUpgradeRecipe extends UpgradeRecipe {
    public Ingredient template;
    public Ingredient addition;

    public MorphScaleUpgradeRecipe(Identifier id, Ingredient template, Ingredient addition) {
        super(id, template, (itemStack -> {
            if (itemStack.isEmpty()) {
                return false;
            }
            NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound == null) {
                return true;
            }
            return !(nbtCompound.contains("MorphScaleItem") && nbtCompound.getBoolean("MorphScaleItem"));
        }), addition, itemStack -> {
            NbtCompound nbtCompound = itemStack.getOrCreateNbt();
            nbtCompound.putBoolean("MorphScaleItem", true);
            return itemStack;
        });
        this.template = template;
        this.addition = addition;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MORPH_SCALE_UPGRADE;
    }

    public static class Serializer implements RecipeSerializer<MorphScaleUpgradeRecipe> {
        public MorphScaleUpgradeRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient template = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "template"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
            return new MorphScaleUpgradeRecipe(identifier, template, addition);
        }

        public MorphScaleUpgradeRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient template = Ingredient.fromPacket(packetByteBuf);
            Ingredient addition = Ingredient.fromPacket(packetByteBuf);
            return new MorphScaleUpgradeRecipe(identifier, template, addition);
        }

        public void write(PacketByteBuf packetByteBuf, MorphScaleUpgradeRecipe morphScaleUpgradeRecipe) {
            morphScaleUpgradeRecipe.template.write(packetByteBuf);
            morphScaleUpgradeRecipe.addition.write(packetByteBuf);
        }
    }
}
