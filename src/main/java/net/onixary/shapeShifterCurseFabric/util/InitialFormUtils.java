package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InitialFormUtils {
    // 给Mixin留个参数 给个player应该能实现一些特殊操作
    public static @NotNull IForm getInitialForm(PlayerEntity player) {
        // "namespace:id:weight"
        if (!ShapeShifterCurseFabric.commonConfig.enableInitialForm) {
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;  // 默认形态
        }
        if (player.getServer() != null && !player.getServer().getGameRules().getBoolean(ModGameRules.USE_INITIAL_FORM)) {
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;  // 默认形态
        }
        try {
            String[] initialFormArray = ShapeShifterCurseFabric.commonConfig.initialFormIds;
            HashMap<IForm, Integer> validForms = new HashMap<>();
            HashMap<String, Integer> formWeightMap = new HashMap<>();
            for (String formId : initialFormArray) {
                if (formId == null || formId.trim().isEmpty()) continue;
                String[] parts = formId.split(":");
                String namespace = parts[0];
                String id = parts[1];
                int weight = 1;
                if (parts.length >= 3) {
                    try {
                        weight = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        weight = 1;
                    }
                }
                String key = namespace + ":" + id;
                if (weight < 1) {
                    continue;
                }
                formWeightMap.put(key, weight);
            }
            for (String formID : formWeightMap.keySet()) {
                IForm form = RegPlayerForms.getPlayerForm(formID);
                if (form != null) {
                    validForms.put(form, formWeightMap.get(formID));
                }
            }

            if (validForms.isEmpty()) {
                return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;  // 防止空形态
            }

            int totalWeight = 0;
            for (int w : validForms.values()) {
                totalWeight += w;
            }
            if (totalWeight <= 0) {
                return validForms.keySet().iterator().next();
            }

            Random random = player.getRandom();
            int randomValue = random.nextInt(totalWeight);

            int cumulative = 0;
            for (Map.Entry<IForm, Integer> entry : validForms.entrySet()) {
                cumulative += entry.getValue();
                if (randomValue < cumulative) {
                    return entry.getKey();
                }
            }
            return validForms.keySet().iterator().next();
        } catch (Exception exception) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to get initial form. Using default. Exception: {}", String.valueOf(exception));
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;  // 防止空形态
        }
    }
}
