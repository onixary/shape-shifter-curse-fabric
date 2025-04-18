package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.HashMap;
import java.util.Map;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegFormConfig {
    public static final Map<PlayerForms, FormConfig> CONFIGS = new HashMap<>();
    private static final String defaultLayerID = "origin";

    public static void register() {
        CONFIGS.put(PlayerForms.ORIGINAL_BEFORE_ENABLE,
                new FormConfig(defaultLayerID,
                        "form_original_before_enable",
                        1.0f,
                        PlayerFormPhase.PHASE_CLEAR));
        CONFIGS.put(PlayerForms.ORIGINAL_SHIFTER,
                new FormConfig(defaultLayerID,
                        "form_original_shifter",
                        1.0f,
                        PlayerFormPhase.PHASE_CLEAR));
        CONFIGS.put(PlayerForms.BAT_0,
                new FormConfig(defaultLayerID,
                        "form_bat_0",
                        0.9f,
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.BAT_1,
                new FormConfig(defaultLayerID,
                        "form_bat_1",
                        0.75f,
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.BAT_2,
                new FormConfig(defaultLayerID,
                        "form_bat_2",
                        0.5f,
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.AXOLOTL_0,
                new FormConfig(defaultLayerID,
                        "form_axolotl_0",
                        1.0f,
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.AXOLOTL_1,
                new FormConfig(defaultLayerID,
                        "form_axolotl_1",
                        1.0f,
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.AXOLOTL_2,
                new FormConfig(defaultLayerID,
                        "form_axolotl_2",
                        0.9f,
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.OCELOT_0,
                new FormConfig(defaultLayerID,
                        "form_ocelot_0",
                        0.95f,
                        PlayerFormPhase.PHASE_0));
        CONFIGS.put(PlayerForms.OCELOT_1,
                new FormConfig(defaultLayerID,
                        "form_ocelot_1",
                        0.85f,
                        PlayerFormPhase.PHASE_1));
        CONFIGS.put(PlayerForms.OCELOT_2,
                new FormConfig(defaultLayerID,
                        "form_ocelot_2",
                        0.65f,
                        PlayerFormPhase.PHASE_2));
        CONFIGS.put(PlayerForms.ALLEY_SP,
                new FormConfig(defaultLayerID,
                        "form_alley_sp",
                        0.35f,
                        PlayerFormPhase.PHASE_SP));

        //CONFIGS.get(PlayerForms.BAT_0).getFormOriginID();
    }

    public static FormConfig getConfig(PlayerForms form) {
        return CONFIGS.get(form);
    }
}
