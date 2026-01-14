package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.AttackEntityDataTracker;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;

public class AdditionalEntityConditions {
    public static void register() {
        register(DiggingBareHandCondition.getFactory());
        register(ChanceCondition.getFactory());
        register(JumpEventCondition.getFactory());
        register(MustCrawlingCondition.getFactory());
        TrinketsCondition.registerCondition(AdditionalEntityConditions::register);
        ManaUtilsApoli.registerCondition(AdditionalEntityConditions::register);
        register(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("can_render_gui"),
                new SerializableData(),
                (data, e) -> {
                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                        return ClientUtils.CanDisplayGUI();
                    }
                    return true;
                }
        ));
        register(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("enable_random_sound"),
                new SerializableData(),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        PlayerSkinComponent skinComponent = RegPlayerSkinComponent.SKIN_SETTINGS.get(e);
                        return skinComponent.isEnableFormRandomSound();
                    }
                    return true;
                }
        ));
        register(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("last_attack_witch_time"),
                new SerializableData()
                        .add("comparison", ApoliDataTypes.COMPARISON)
                        .add("compare_to", SerializableDataTypes.INT, 0),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        long lastAttackTime = AttackEntityDataTracker.lastAttackWitchTimeMap.getOrDefault(player.getUuid(), Long.MIN_VALUE / 16);
                        long trueLastAttackTime = player.getWorld().getTime() - lastAttackTime;
                        Comparison comparison = (Comparison)data.get("comparison");
                        if (comparison == null) {
                            return false;
                        }
                        return comparison.compare(trueLastAttackTime, (int)data.get("compare_to"));
                    }
                    return false;
                }
        ));
        register(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("last_attack_pillager_time"),
                new SerializableData()
                        .add("comparison", ApoliDataTypes.COMPARISON)
                        .add("compare_to", SerializableDataTypes.INT, 0),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        long lastAttackTime = AttackEntityDataTracker.lastAttackPillagerTimeMap.getOrDefault(player.getUuid(), Long.MIN_VALUE / 16);
                        long trueLastAttackTime = player.getWorld().getTime() - lastAttackTime;
                        Comparison comparison = (Comparison)data.get("comparison");
                        if (comparison == null) {
                            return false;
                        }
                        return comparison.compare(trueLastAttackTime, (int)data.get("compare_to"));
                    }
                    return false;
                }
        ));
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);

    }
}
