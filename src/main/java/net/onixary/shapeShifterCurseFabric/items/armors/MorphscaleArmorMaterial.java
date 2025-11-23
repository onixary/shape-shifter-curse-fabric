package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MorphscaleArmorMaterial  implements ArmorMaterial {
    public static final MorphscaleArmorMaterial INSTANCE = new MorphscaleArmorMaterial();

    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {2, 6, 7, 2};

    @Override
    public int getDurability(ArmorItem.Type type) {
        // Replace this multiplier by a constant value for the durability of the armor.
        // For reference, diamond uses 33 for all armor pieces, whilst leather uses 5.
        int DURABILITY_MULTIPLIER = 33;
        return switch (type) {
            case BOOTS -> BASE_DURABILITY[0] * DURABILITY_MULTIPLIER;
            case LEGGINGS -> BASE_DURABILITY[1] * DURABILITY_MULTIPLIER;
            case CHESTPLATE -> BASE_DURABILITY[2] * DURABILITY_MULTIPLIER;
            case HELMET -> BASE_DURABILITY[3] * DURABILITY_MULTIPLIER;
            default -> 0;
        };
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        // Protection values for all the slots.
        // For reference, diamond uses 3 for boots, 6 for leggings, 8 for chestplate, and 3 for helmet,
        // whilst leather uses 1, 2, 3 and 1 respectively.
        return switch (type) {
            case HELMET -> PROTECTION_VALUES[0];
            case LEGGINGS -> PROTECTION_VALUES[1];
            case CHESTPLATE -> PROTECTION_VALUES[2];
            case BOOTS -> PROTECTION_VALUES[3];
            default -> 0;
        };
    }

    @Override
    public int getEnchantability() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.DIAMOND);
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return "morphscale";
    }

    @Override
    public float getToughness() {
        return 1.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
