package net.onixary.shapeShifterCurseFabric.integration.toughasnails;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.TANFormTemperatureModifierPower;
import net.onixary.shapeShifterCurseFabric.additional_power.TANModifyThirstExhaustionPower;
import net.onixary.shapeShifterCurseFabric.additional_power.TANPreventDirtyWaterThirstEffectPower;

import java.util.List;

public class ToughAsNailsPowerUtils {
    public static boolean isToughAsNailsLoaded() {
        return FabricLoader.getInstance().isModLoaded("toughasnails");
    }

    public static int modifyFormTemperatureOrdinal(PlayerEntity player, int currentOrdinal) {
        for (TANFormTemperatureModifierPower power : PowerHolderComponent.getPowers(player, TANFormTemperatureModifierPower.class)) {
            if (power.isActive()) {
                return power.modifyTemperatureOrdinal(currentOrdinal);
            }
        }
        return currentOrdinal;
    }

    public static boolean shouldPreventDirtyWaterThirstEffect(PlayerEntity player) {
        return PowerHolderComponent.getPowers(player, TANPreventDirtyWaterThirstEffectPower.class)
                .stream()
                .anyMatch(TANPreventDirtyWaterThirstEffectPower::isActive);
    }

    public static float modifyThirstExhaustion(PlayerEntity player, float exhaustion) {
        double modified = exhaustion;
        for (TANModifyThirstExhaustionPower power : PowerHolderComponent.getPowers(player, TANModifyThirstExhaustionPower.class)) {
            if (!power.isActive()) {
                continue;
            }
            List<Modifier> modifiers = power.getModifiers();
            modified = ModifierUtil.applyModifiers((Entity) player, modifiers, modified);
        }
        return Math.max(0.0F, (float) modified);
    }
}
