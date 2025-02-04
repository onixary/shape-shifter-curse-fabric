package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.effects.ToBatStatus0;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RegTStatusEffect {
    private RegTStatusEffect(){}

    //public static final BaseTransformativeStatusEffect EMPTY_EFFECT = register("empty_effect",new BaseTransformativeStatusEffect(null, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false) );
    public static final BaseTransformativeStatusEffect TO_BAT_0_EFFECT = register("to_bat_0_effect",new ToBatStatus0());

    public static <T extends BaseTransformativeStatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static boolean hasAnyEffect(PlayerEntity player) {
        // is player has any transformative effect
        return player.hasStatusEffect(TO_BAT_0_EFFECT);
    }

    public static void removeEffects(PlayerEntity player) {
        // remove all transformative effects potion icon
        player.removeStatusEffect(TO_BAT_0_EFFECT);
    }

    public static void initialize() {}
}
