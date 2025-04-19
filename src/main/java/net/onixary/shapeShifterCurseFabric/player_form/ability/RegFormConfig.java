package net.onixary.shapeShifterCurseFabric.player_form.ability;

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
                        PlayerFormPhase.PHASE_CLEAR));
        CONFIGS.put(PlayerForms.ORIGINAL_SHIFTER,
                new FormConfig(defaultLayerID,
                        "form_original_shifter",
                        PlayerFormPhase.PHASE_CLEAR));
        CONFIGS.put(PlayerForms.BAT_0,
                new FormConfig(defaultLayerID,
                        "form_bat_0",
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.BAT_1,
                new FormConfig(defaultLayerID,
                        "form_bat_1",
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.BAT_2,
                new FormConfig(defaultLayerID,
                        "form_bat_2",
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.AXOLOTL_0,
                new FormConfig(defaultLayerID,
                        "form_axolotl_0",
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.AXOLOTL_1,
                new FormConfig(defaultLayerID,
                        "form_axolotl_1",
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.AXOLOTL_2,
                new FormConfig(defaultLayerID,
                        "form_axolotl_2",
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.OCELOT_0,
                new FormConfig(defaultLayerID,
                        "form_ocelot_0",
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.OCELOT_1,
                new FormConfig(defaultLayerID,
                        "form_ocelot_1",
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.OCELOT_2,
                new FormConfig(defaultLayerID,
                        "form_ocelot_2",
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.ALLAY_SP,
                new FormConfig(defaultLayerID,
                        "form_allay_sp",
                        PlayerFormPhase.PHASE_SP));

        //CONFIGS.get(PlayerForms.BAT_0).getFormOriginID();
    }

    public static FormConfig getConfig(PlayerForms form) {
        return CONFIGS.get(form);
    }
}
