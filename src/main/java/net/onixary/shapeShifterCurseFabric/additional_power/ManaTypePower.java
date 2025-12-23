package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;

public class ManaTypePower extends Power {
    private Identifier manaType = null;

    public ManaTypePower(PowerType<?> type, LivingEntity entity, Identifier manaType) {
        super(type, entity);
        this.manaType = manaType;
    }

    @Override
    public void onAdded() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            ManaUtils.gainManaTypeID(playerEntity, manaType);
        }
    }

    @Override
    public void onRemoved() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            ManaUtils.loseManaTypeID(playerEntity, manaType);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("mana_type_power"),
                new SerializableData()
                        .add("mana_type", SerializableDataTypes.IDENTIFIER, null),
                (data) -> (type, entity) -> new ManaTypePower(type, entity, data.get("mana_type"))
        );
    }
}
