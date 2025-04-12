package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.ToAxolotlStatus0;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.ToBatStatus0;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.ToOcelotStatus0;

public class RegTStatusEffect {
    private RegTStatusEffect(){}

    //public static final BaseTransformativeStatusEffect EMPTY_EFFECT = register("empty_effect",new BaseTransformativeStatusEffect(null, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false) );
    public static final BaseTransformativeStatusEffect TO_BAT_0_EFFECT = register("to_bat_0_effect",new ToBatStatus0());
    public static final BaseTransformativeStatusEffect TO_AXOLOTL_0_EFFECT = register("to_axolotl_0_effect",new ToAxolotlStatus0());
    public static final BaseTransformativeStatusEffect TO_OCELOT_0_EFFECT = register("to_ocelot_0_effect",new ToOcelotStatus0());

    public static <T extends BaseTransformativeStatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static boolean hasAnyEffect(PlayerEntity player) {
        // is player has any transformative effect
        return player.hasStatusEffect(TO_BAT_0_EFFECT)
                || player.hasStatusEffect(TO_AXOLOTL_0_EFFECT)
                || player.hasStatusEffect(TO_OCELOT_0_EFFECT);
    }

    public static void removeVisualEffects(PlayerEntity player) {
        // remove all transformative effects potion icon
        player.removeStatusEffect(TO_BAT_0_EFFECT);
        player.removeStatusEffect(TO_AXOLOTL_0_EFFECT);
        player.removeStatusEffect(TO_OCELOT_0_EFFECT);
    }

    public static void initialize() {}
}
