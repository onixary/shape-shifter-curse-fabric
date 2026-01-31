package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ManaUtilsApoli {
    private static final Logger log = LoggerFactory.getLogger(ManaUtilsApoli.class);

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("set_mana"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana = data.get("mana");
                        ManaUtils.setPlayerMana(playerEntity, mana);
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("gain_mana"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana = data.get("mana");
                        ManaUtils.gainPlayerMana(playerEntity, mana);
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("consume_mana"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana = data.get("mana");
                        ManaUtils.consumePlayerMana(playerEntity, mana);
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("gain_mana_with_time"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d)
                        .add("time", SerializableDataTypes.INT, 0),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana = data.get("mana");
                        int time = data.get("time");
                        ManaUtils.gainPlayerManaWithTime(playerEntity, mana, time);
                    }
                })
        );
    }

    public static void registerCondition(Consumer<ConditionFactory<Entity>> registerFunc) {
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("has_mana"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        double mana = data.get("mana");
                        boolean manaAbove = ManaUtils.isPlayerManaAbove(player, mana);
                        return manaAbove;
                    }
                    return false;
                }
        ));
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("has_mana_percent"),
                new SerializableData()
                        .add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        double mana_percent = data.get("mana_percent");
                        boolean manaAbove = ManaUtils.getPlayerManaPercent(player, 0.0d) >= mana_percent;
                        return manaAbove;
                    }
                    return false;
                }
        ));
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("mana_compare"),
                new SerializableData()
                        .add("comparison", ApoliDataTypes.COMPARISON, null)
                        .add("compare_to", SerializableDataTypes.DOUBLE, 0.0d),
                (data, entity) -> {
                    Comparison comparison = data.get("comparison");
                    if (comparison != null && entity instanceof PlayerEntity player) {
                        return comparison.compare(ManaUtils.getPlayerMana(player), data.getDouble("compare_to"));
                    }
                    return false;
                }
        ));
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("mana_percent_compare"),
                new SerializableData()
                        .add("comparison", ApoliDataTypes.COMPARISON, null)
                        .add("compare_to", SerializableDataTypes.DOUBLE, 0.0d),
                (data, entity) -> {
                    Comparison comparison = data.get("comparison");
                    if (comparison != null && entity instanceof PlayerEntity player) {
                        return comparison.compare(ManaUtils.getPlayerManaPercent(player, 0.0d), data.getDouble("compare_to"));
                    }
                    return false;
                }
        ));

    }
}
