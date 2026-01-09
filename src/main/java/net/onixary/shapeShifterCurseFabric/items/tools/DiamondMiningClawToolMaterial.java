package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DiamondMiningClawToolMaterial implements ToolMaterial {
    public static final DiamondMiningClawToolMaterial INSTANCE = new DiamondMiningClawToolMaterial();

    @Override
    public int getDurability() {
        return 781;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 4f;   // 石稿速度  蝙蝠为 4 + (4 * 2) = 12  4/3倍下界合金镐
    }

    @Override
    public float getAttackDamage() {
        return 2;
    }

    @Override
    public int getMiningLevel() {
        return 3;
    }

    @Override
    public int getEnchantability() {
        return 10;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND});
    }
}
