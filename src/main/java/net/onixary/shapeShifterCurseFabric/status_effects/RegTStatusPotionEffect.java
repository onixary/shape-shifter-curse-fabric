package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.*;

public class RegTStatusPotionEffect {
    private RegTStatusPotionEffect(){}

    public static final StatusEffect TO_BAT_0_POTION = register("to_bat_0_potion",new ToBatStatusPotion());
    public static final StatusEffect TO_AXOLOTL_0_POTION = register("to_axolotl_0_potion", new ToAxolotlStatusPotion());
    public static final StatusEffect TO_OCELOT_0_POTION = register("to_ocelot_0_potion", new ToOcelotStatusPotion());
    public static final StatusEffect TO_FAMILIAR_FOX_0_POTION = register("to_familiar_fox_0_potion", new ToFamiliarFoxStatusPotion());
    public static final StatusEffect TO_SNOW_FOX_0_POTION = register("to_snow_fox_0_potion", new ToSnowFoxStatusPotion());
    public static final StatusEffect TO_ALLAY_SP_POTION = register("to_allay_sp_potion", new ToAllayStatusPotion());
    public static final StatusEffect TO_FERAL_CAT_SP_POTION = register("to_feral_cat_sp_potion", new ToFeralCatStatusPotion());
    // empty custom forms
    public static final StatusEffect TO_ALPHA_0_POTION = register("to_alpha_0_potion", new ToAlphaStatusPotion());
    public static final StatusEffect TO_BETA_0_POTION = register("to_beta_0_potion", new ToBetaStatusPotion());
    public static final StatusEffect TO_GAMMA_0_POTION = register("to_gamma_0_potion", new ToGammaStatusPotion());
    public static final StatusEffect TO_OMEGA_SP_POTION = register("to_omega_sp_potion", new ToOmegaStatusPotion());
    public static final StatusEffect TO_PSI_SP_POTION = register("to_psi_sp_potion", new ToPsiStatusPotion());
    public static final StatusEffect TO_CHI_SP_POTION = register("to_chi_sp_potion", new ToChiStatusPotion());
    public static final StatusEffect TO_PHI_SP_POTION = register("to_phi_sp_potion", new ToPhiStatusPotion());

    public static <T extends StatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static void initialize() {}
}
