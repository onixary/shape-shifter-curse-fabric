package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.ToBatStatus0;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_AXOLOTL_0_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_BAT_0_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect.*;

public class RegCustomPotions {
    public static final Potion MOONDUST_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "moondust_potion"),
                    new Potion());
    public static final Potion BAT_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_bat_0_potion"),
                    new Potion(new StatusEffectInstance(TO_BAT_0_POTION)));
    public static final Potion AXOLOTL_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_axolotl_0_potion"),
                    new Potion(new StatusEffectInstance(TO_AXOLOTL_0_POTION)));
    public static final Potion OCELOT_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_ocelot_0_potion"),
                    new Potion(new StatusEffectInstance(TO_OCELOT_0_POTION)));


    public static void registerPotions(){

    }

    public static void registerPotionsRecipes(){
        // awkward + moondust_matrix = moondust_potion
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, RegCustomItem.MOONDUST_MATRIX, RegCustomPotions.MOONDUST_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.POINTED_DRIPSTONE, BAT_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.BIG_DRIPLEAF, AXOLOTL_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.CHICKEN, OCELOT_FORM_POTION);
    }
}
