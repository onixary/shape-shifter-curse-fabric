package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;

import java.util.function.Consumer;

public class ManaUtilsApoli {
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
                ShapeShifterCurseFabric.identifier("has_mana"),  // 为了之后写双端不用改 还是使用equip_accessories吧
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d)
                        .add("Inverted", SerializableDataTypes.BOOLEAN, false),
                (data, e) -> {
                    boolean inverted = data.get("Inverted");
                    if (e instanceof PlayerEntity player) {
                        double mana = data.get("mana");
                        boolean manaAbove = ManaUtils.isPlayerManaAbove(player, mana);
                        return manaAbove ^ inverted;
                    }
                    return inverted;
                }
        ));
    }
}
