package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects.FEED_EFFECT;
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
    public static final Potion SNOW_FOX_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_snow_fox_0_potion"),
                    new Potion(new StatusEffectInstance(TO_SNOW_FOX_0_POTION)));
    public static final Potion ANUBIS_WOLF_FORM_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "to_anubis_wolf_0_potion"),
                    new Potion(new StatusEffectInstance(TO_ANUBIS_WOLF_0_POTION)));
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
    // other custom potions
    // feed potion can only be obtained via familiar_fox_2 and familiar_fox_3, no recipe
    public static final Potion FEED_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "feed_potion"),
                    new Potion(new StatusEffectInstance(FEED_EFFECT)));

    public static void registerPotions(){

    }

    public static void registerPotionsRecipes(){
        // awkward + moondust_matrix = moondust_potion
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, RegCustomItem.MOONDUST_MATRIX, RegCustomPotions.MOONDUST_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.POINTED_DRIPSTONE, BAT_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.BIG_DRIPLEAF, AXOLOTL_FORM_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(MOONDUST_POTION, Items.CHICKEN, OCELOT_FORM_POTION);
        // familiar fox只能通过女巫发射或掉落的溅射药水给与，没有配方
        // The familiar fox can only be obtained via splash potions thrown or drop by witches, no recipe available
        // todo: anubis wolf 药水配方待定，可能是狼灵的特殊掉落物
        // snow fox 需要通过净化familiar fox药水来得到
        // snow fox can be obtained by purifying familiar fox potion
        BrewingRecipeRegistry.registerPotionRecipe(FAMILIAR_FOX_FORM_POTION, Items.GOLD_NUGGET, SNOW_FOX_FORM_POTION);
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
