package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.function.Predicate;

public class ConditionedModifySlipperinessPower extends ValueModifyingPower {
    private final Predicate<CachedBlockPosition> predicate;
    private final ConditionFactory<LivingEntity>.Instance condition;
    private final float slipperinessModifier;

    public ConditionedModifySlipperinessPower(PowerType<?> type, LivingEntity entity, Predicate<CachedBlockPosition> predicate, ConditionFactory<LivingEntity>.Instance condition, float slipperinessModifier) {
        super(type, entity);
        this.predicate = predicate;
        this.condition = condition;
        this.slipperinessModifier = slipperinessModifier;
    }

    public boolean doesApply(WorldView world, BlockPos pos) {
        CachedBlockPosition cbp = new CachedBlockPosition(world, pos, true);

        return predicate.test(cbp) && (condition == null || condition.test(entity));
    }

    public float getSlipperinessModifier() {
        return slipperinessModifier;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("conditioned_modify_slipperiness"),
                new SerializableData()
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("modifier", SerializableDataTypes.FLOAT),
                data ->
                        (type, player) -> new ConditionedModifySlipperinessPower(
                                type,
                                player,
                                data.get("block_condition"),
                                data.get("entity_condition"),
                                data.getFloat("modifier"))
        ).allowCondition();
    }

}
