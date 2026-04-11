package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.EntangledEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.FeedEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.ImmobilityEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.SimpleStatusEffect;

public class RegOtherStatusEffects {
    private RegOtherStatusEffects(){}

    //public static final BaseTransformativeStatusEffect EMPTY_EFFECT = register("empty_effect",new BaseTransformativeStatusEffect(null, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false) );
    public static final ImmobilityEffect IMMOBILITY_EFFECT = register("immobility_effect",new ImmobilityEffect());
    public static final FeedEffect FEED_EFFECT = register("feed_effect", new FeedEffect());

    // 裹茧1级效果不手动减速，使用减速效果
    public static final StatusEffect ENTANGLED_EFFECT = register("entangled_effect", new EntangledEffect(StatusEffectCategory.HARMFUL, 0x9F9F9F));
    public static final StatusEffect ENTANGLED_FULL_EFFECT  = register("entangled_full_effect", new SimpleStatusEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "ef0f1c0e-3d99-45e8-9283-0d48d07751fe", -1.0F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "e2f8008f-4b87-4f19-96c0-a10acce2f960", 100.0F, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "e2f8008f-4b87-4f19-96c0-a10acce2f960", -0.8F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    );

    public static <T extends StatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static void initialize() {}
}
