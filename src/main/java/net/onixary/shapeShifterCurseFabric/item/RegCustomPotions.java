package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
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
    public static final Potion FAMILIAR_FOX_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_familiar_fox_0_potion"),
                    new Potion(new StatusEffectInstance(TO_FAMILIAR_FOX_0_POTION)));
    public static final Potion ALLEY_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_allay_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_ALLAY_SP_POTION)));
    public static final Potion FERAL_CAT_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_feral_cat_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_FERAL_CAT_SP_POTION)));
    // custom empty forms
    public static final Potion ALPHA_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_alpha_0_potion"),
                    new Potion(new StatusEffectInstance(TO_ALPHA_0_POTION)));
    public static final Potion BETA_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_beta_0_potion"),
                    new Potion(new StatusEffectInstance(TO_BETA_0_POTION)));
    public static final Potion GAMMA_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_gamma_0_potion"),
                    new Potion(new StatusEffectInstance(TO_GAMMA_0_POTION)));
    public static final Potion OMEGA_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_omega_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_OMEGA_SP_POTION)));
    public static final Potion PSI_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_psi_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_PSI_SP_POTION)));
    public static final Potion CHI_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_chi_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_CHI_SP_POTION)));
    public static final Potion PHI_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_phi_sp_potion"),
                    new Potion(new StatusEffectInstance(TO_PHI_SP_POTION)));


    public static void registerPotions(){

    }

    public static void registerPotionsRecipes(){
        // awkward + moondust_matrix = moondust_potion
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, RegCustomItem.MOONDUST_MATRIX, RegCustomPotions.MOONDUST_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.POINTED_DRIPSTONE, BAT_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.BIG_DRIPLEAF, AXOLOTL_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.CHICKEN, OCELOT_FORM_POTION);
        // familiar fox只能通过女巫发射的溅射药水给与，没有配方
        // The familiar fox can only be obtained via splash potions thrown by witches, no recipe available
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.AMETHYST_SHARD, ALLEY_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.COD_BUCKET, FERAL_CAT_FORM_POTION);
        // custom empty forms
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.RED_DYE, ALPHA_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.YELLOW_DYE, BETA_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.BLUE_DYE, GAMMA_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.GREEN_DYE, OMEGA_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.ORANGE_DYE, PSI_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.PURPLE_DYE, CHI_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.WHITE_DYE, PHI_FORM_POTION);
    }
}
