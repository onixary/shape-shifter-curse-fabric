package net.onixary.shapeShifterCurseFabric.additional_power;

import blue.endless.jankson.annotation.Nullable;
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
    private @Nullable Identifier manaType = null;
    private @Nullable Identifier manaSource = null;

    public ManaTypePower(PowerType<?> type, LivingEntity entity, @Nullable Identifier manaType, @Nullable Identifier manaSource) {
        super(type, entity);
        this.manaType = manaType;
        if (manaSource == null) {
            this.manaSource = type.getIdentifier();
        } else {
            this.manaSource = manaSource;
        }
    }

    @Override
    public void onAdded() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            ManaUtils.gainPlayerManaWithTime(playerEntity, Double.MAX_VALUE / 8, 8);  // 没有防溢出 别直接用Double.MAX_VALUE
        }
    }

    @Override
    public void onRemoved() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            ManaUtils.loseManaTypeID(playerEntity, manaType, manaSource);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("mana_type_power"),
                new SerializableData()
                        .add("mana_type", SerializableDataTypes.IDENTIFIER, null)
                        .add("mana_source", SerializableDataTypes.IDENTIFIER, null),
                (data) -> (type, entity) -> new ManaTypePower(type, entity, data.get("mana_type"), data.get("mana_source"))
        );
    }
}
