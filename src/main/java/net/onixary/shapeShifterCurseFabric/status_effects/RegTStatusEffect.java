package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RegTStatusEffect {
    private RegTStatusEffect(){}

    public static final BaseTransformativeStatusEffect TO_BAT_0_EFFECT = register("to_bat_0_effect",new ToBatStatus0());
    public static final BaseTransformativeStatusEffect TO_AXOLOTL_0_EFFECT = register("to_axolotl_0_effect",new ToAxolotlStatus0());
    public static final BaseTransformativeStatusEffect TO_OCELOT_0_EFFECT = register("to_ocelot_0_effect",new ToOcelotStatus0());
    public static final BaseTransformativeStatusEffect TO_ALLAY_SP_EFFECT = register("to_allay_sp_effect",new ToAllayStatusSP());
    public static final BaseTransformativeStatusEffect TO_FERAL_CAT_SP_EFFECT = register("to_feral_cat_sp_effect",new ToFeralCatStatusSP());
    // empty custom forms
    public static final BaseTransformativeStatusEffect TO_ALPHA_0_EFFECT = register("to_alpha_0_effect",new ToAlphaStatus0());
    public static final BaseTransformativeStatusEffect TO_BETA_0_EFFECT = register("to_beta_0_effect",new ToBetaStatus0());
    public static final BaseTransformativeStatusEffect TO_GAMMA_0_EFFECT = register("to_gamma_0_effect",new ToGammaStatus0());
    public static final BaseTransformativeStatusEffect TO_OMEGA_SP_EFFECT = register("to_omega_sp_effect",new ToOmegaStatusSP());
    public static final BaseTransformativeStatusEffect TO_PSI_SP_EFFECT = register("to_psi_sp_effect",new ToPsiStatusSP());
    public static final BaseTransformativeStatusEffect TO_CHI_SP_EFFECT = register("to_chi_sp_effect",new ToChiStatusSP());
    public static final BaseTransformativeStatusEffect TO_PHI_SP_EFFECT = register("to_phi_sp_effect",new ToPhiStatusSP());

    public static <T extends BaseTransformativeStatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static boolean hasAnyEffect(PlayerEntity player) {
        // is player has any transformative effect
        return player.hasStatusEffect(TO_BAT_0_EFFECT)
                || player.hasStatusEffect(TO_AXOLOTL_0_EFFECT)
                || player.hasStatusEffect(TO_OCELOT_0_EFFECT)
                || player.hasStatusEffect(TO_ALLAY_SP_EFFECT)
                || player.hasStatusEffect(TO_FERAL_CAT_SP_EFFECT)
                || player.hasStatusEffect(TO_ALPHA_0_EFFECT)
                || player.hasStatusEffect(TO_BETA_0_EFFECT)
                || player.hasStatusEffect(TO_GAMMA_0_EFFECT)
                || player.hasStatusEffect(TO_OMEGA_SP_EFFECT)
                || player.hasStatusEffect(TO_PSI_SP_EFFECT)
                || player.hasStatusEffect(TO_CHI_SP_EFFECT)
                || player.hasStatusEffect(TO_PHI_SP_EFFECT);
    }

    public static void removeVisualEffects(PlayerEntity player) {
        // remove all transformative effects potion icon
        player.removeStatusEffect(TO_BAT_0_EFFECT);
        player.removeStatusEffect(TO_AXOLOTL_0_EFFECT);
        player.removeStatusEffect(TO_OCELOT_0_EFFECT);
        player.removeStatusEffect(TO_ALLAY_SP_EFFECT);
        player.removeStatusEffect(TO_FERAL_CAT_SP_EFFECT);
        player.removeStatusEffect(TO_ALPHA_0_EFFECT);
        player.removeStatusEffect(TO_BETA_0_EFFECT);
        player.removeStatusEffect(TO_GAMMA_0_EFFECT);
        player.removeStatusEffect(TO_OMEGA_SP_EFFECT);
        player.removeStatusEffect(TO_PSI_SP_EFFECT);
        player.removeStatusEffect(TO_CHI_SP_EFFECT);
        player.removeStatusEffect(TO_PHI_SP_EFFECT);
    }

    public static void initialize() {}
}
