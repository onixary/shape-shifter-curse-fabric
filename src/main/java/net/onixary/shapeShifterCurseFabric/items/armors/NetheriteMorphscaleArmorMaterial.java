package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class NetheriteMorphscaleArmorMaterial implements ArmorMaterial {
    public static final NetheriteMorphscaleArmorMaterial INSTANCE = new NetheriteMorphscaleArmorMaterial();

    private static final int[] BASE_DURABILITY = new int[] {462, 592, 555, 481};
    private static final int[] PROTECTION_VALUES = new int[] {3, 6, 7, 3};

    @Override
    public int getDurability(ArmorItem.Type type) {
        // Replace this multiplier by a constant value for the durability of the armor.
        // For reference, diamond uses 33 for all armor pieces, whilst leather uses 5.
        return switch (type) {
            case BOOTS -> BASE_DURABILITY[0];
            case LEGGINGS -> BASE_DURABILITY[1];
            case CHESTPLATE -> BASE_DURABILITY[2];
            case HELMET -> BASE_DURABILITY[3];
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
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.NETHERITE_SCRAP);
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return "netherite_morphscale";
    }

    @Override
    public float getToughness() {
        return 2.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
