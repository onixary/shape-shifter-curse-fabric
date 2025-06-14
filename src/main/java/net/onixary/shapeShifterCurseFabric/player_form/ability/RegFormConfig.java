package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.HashMap;
import java.util.Map;

public class RegFormConfig {
    public static final Map<PlayerForms, FormConfig> CONFIGS = new HashMap<>();
    private static final String defaultLayerID = "origin";

    public static void register() {
        CONFIGS.put(PlayerForms.ORIGINAL_BEFORE_ENABLE,
                new FormConfig(defaultLayerID,
                        "form_original_before_enable",
                        PlayerFormPhase.PHASE_CLEAR, PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.ORIGINAL_SHIFTER,
                new FormConfig(defaultLayerID,
                        "form_original_shifter",
                        PlayerFormPhase.PHASE_CLEAR,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BAT_0,
                new FormConfig(defaultLayerID,
                        "form_bat_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BAT_1,
                new FormConfig(defaultLayerID,
                        "form_bat_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BAT_2,
                new FormConfig(defaultLayerID,
                        "form_bat_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.AXOLOTL_0,
                new FormConfig(defaultLayerID,
                        "form_axolotl_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.AXOLOTL_1,
                new FormConfig(defaultLayerID,
                        "form_axolotl_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.AXOLOTL_2,
                new FormConfig(defaultLayerID,
                        "form_axolotl_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.OCELOT_0,
                new FormConfig(defaultLayerID,
                        "form_ocelot_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.OCELOT_1,
                new FormConfig(defaultLayerID,
                        "form_ocelot_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.OCELOT_2,
                new FormConfig(defaultLayerID,
                        "form_ocelot_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.OCELOT_3,
                new FormConfig(defaultLayerID,
                        "form_ocelot_3",
                        PlayerFormPhase.PHASE_3,PlayerFormBodyType.FERAL));
        CONFIGS.put(PlayerForms.FAMILIAR_FOX_0,
                new FormConfig(defaultLayerID,
                        "form_familiar_fox_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.FAMILIAR_FOX_1,
                new FormConfig(defaultLayerID,
                        "form_familiar_fox_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.FAMILIAR_FOX_2,
                new FormConfig(defaultLayerID,
                        "form_familiar_fox_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.FAMILIAR_FOX_3,
                new FormConfig(defaultLayerID,
                        "form_familiar_fox_3",
                        PlayerFormPhase.PHASE_3,PlayerFormBodyType.FERAL));
        CONFIGS.put(PlayerForms.ALLAY_SP,
                new FormConfig(defaultLayerID,
                        "form_allay_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.FERAL_CAT_SP,
                new FormConfig(defaultLayerID,
                        "form_feral_cat_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.FERAL));

        // Custom empty forms
        CONFIGS.put(PlayerForms.ALPHA_0,
                new FormConfig(defaultLayerID,
                        "form_alpha_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.ALPHA_1,
                new FormConfig(defaultLayerID,
                        "form_alpha_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.ALPHA_2,
                new FormConfig(defaultLayerID,
                        "form_alpha_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BETA_0,
                new FormConfig(defaultLayerID,
                        "form_beta_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BETA_1,
                new FormConfig(defaultLayerID,
                        "form_beta_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.BETA_2,
                new FormConfig(defaultLayerID,
                        "form_beta_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.GAMMA_0,
                new FormConfig(defaultLayerID,
                        "form_gamma_0",
                        PlayerFormPhase.PHASE_0,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.GAMMA_1,
                new FormConfig(defaultLayerID,
                        "form_gamma_1",
                        PlayerFormPhase.PHASE_1,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.GAMMA_2,
                new FormConfig(defaultLayerID,
                        "form_gamma_2",
                        PlayerFormPhase.PHASE_2,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.OMEGA_SP,
                new FormConfig(defaultLayerID,
                        "form_omega_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.PSI_SP,
                new FormConfig(defaultLayerID,
                        "form_psi_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.CHI_SP,
                new FormConfig(defaultLayerID,
                        "form_chi_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.NORMAL));
        CONFIGS.put(PlayerForms.PHI_SP,
                new FormConfig(defaultLayerID,
                        "form_phi_sp",
                        PlayerFormPhase.PHASE_SP,PlayerFormBodyType.FERAL));

        //CONFIGS.get(PlayerForms.BAT_0).getFormOriginID();
    }

    public static FormConfig getConfig(PlayerForms form) {
        return CONFIGS.get(form);
    }
}
