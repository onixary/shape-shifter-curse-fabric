package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.FeedEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.ImmobilityEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.ToBatStatus0;

public class RegOtherStatusEffects {
    private RegOtherStatusEffects(){}

    //public static final BaseTransformativeStatusEffect EMPTY_EFFECT = register("empty_effect",new BaseTransformativeStatusEffect(null, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false) );
    public static final ImmobilityEffect IMMOBILITY_EFFECT = register("immobility_effect",new ImmobilityEffect());
    public static final FeedEffect FEED_EFFECT = register("feed_effect", new FeedEffect());

    public static <T extends ImmobilityEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static <T extends FeedEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static void initialize() {}
}
