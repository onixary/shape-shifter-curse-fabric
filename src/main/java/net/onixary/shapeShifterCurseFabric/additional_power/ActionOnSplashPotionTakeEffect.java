package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Predicate;

public class ActionOnSplashPotionTakeEffect extends Power {

    private final ActionFactory<Entity>.Instance entityAction;
    private final Predicate<Entity> entityCondition;
    private final boolean triggerOnNoEffect;

    public ActionOnSplashPotionTakeEffect(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance entityAction, Predicate<Entity> condition, boolean triggerOnNoEffect) {
        super(type, entity);
        this.entityAction = entityAction;
        this.entityCondition = condition;
        this.triggerOnNoEffect = triggerOnNoEffect;
    }

    public void executeAction() {
        if (entity instanceof PlayerEntity player) {
            if (entityCondition == null || entityCondition.test(player)) {
                if (entityAction != null) {
                    entityAction.accept(player);
                }
            }
        }
    }

    public boolean shouldTriggerOnNoEffect() {
        return triggerOnNoEffect;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("action_on_splash_potion_take_effect"),
                new SerializableData()
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("trigger_on_no_effect", SerializableDataTypes.BOOLEAN, false),
                data -> (type, entity) -> new ActionOnSplashPotionTakeEffect(
                        type,
                        entity,
                        data.get("entity_action"),
                        data.get("entity_condition"),
                        data.get("trigger_on_no_effect")
                )
        ).allowCondition();
    }
}
