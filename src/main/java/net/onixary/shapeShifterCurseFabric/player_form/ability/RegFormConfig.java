package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.HashMap;
import java.util.Map;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegFormConfig {
    private static final Map<PlayerForms, FormConfig> CONFIGS = new HashMap<>();
    private static final String defaultLayerID = "origin";

    public static void register() {
        CONFIGS.put(PlayerForms.ORIGINAL_BEFORE_ENABLE,
                new FormConfig(defaultLayerID, "form_original_before_enable", 1.0f));
        CONFIGS.put(PlayerForms.ORIGINAL_SHIFTER,
                new FormConfig(defaultLayerID, "form_original_shifter", 0.5f));
        CONFIGS.put(PlayerForms.BAT_0,
                new FormConfig(defaultLayerID, "form_bat_0", 0.8f));
        CONFIGS.put(PlayerForms.BAT_1,
                new FormConfig(defaultLayerID, "form_bat_1", 1.0f));
        CONFIGS.put(PlayerForms.BAT_2,
                new FormConfig(defaultLayerID, "form_bat_2", 1.0f));
    }

    public static FormConfig getConfig(PlayerForms form) {
        return CONFIGS.get(form);
    }
}
