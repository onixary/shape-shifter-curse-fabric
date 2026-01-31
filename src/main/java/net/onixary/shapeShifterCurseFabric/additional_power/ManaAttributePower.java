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
import org.jetbrains.annotations.Nullable;

public class ManaAttributePower extends Power {
    private final @Nullable Identifier modifierID;
    private final @Nullable ManaUtils.Modifier maxManaModifier;
    private final @Nullable ManaUtils.Modifier regenManaModifier;
    private final boolean playerSide;

    public ManaAttributePower(PowerType<?> type, LivingEntity entity, @Nullable Identifier modifierID, @Nullable ManaUtils.Modifier maxManaModifier, @Nullable ManaUtils.Modifier manaRegenModifier, boolean playerSide)  {
        super(type, entity);
        this.modifierID = modifierID;
        this.maxManaModifier = maxManaModifier;
        this.regenManaModifier = manaRegenModifier;
        this.playerSide = playerSide;
    }
    @Override
    public void onAdded() {
        if (modifierID == null) {
            return;
        }
        if (this.entity instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.addMaxManaModifier(playerEntity, modifierID, maxManaModifier, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.addRegenManaModifier(playerEntity, modifierID, regenManaModifier, playerSide);
            }
        }
    }

    @Override
    public void onLost() {
        if (modifierID == null) {
            return;
        }
        if (this.entity instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.removeMaxManaModifier(playerEntity, modifierID, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.removeRegenManaModifier(playerEntity, modifierID, playerSide);
            }
        }
    }


    @Override
    public void onRespawn() {
        // 同样与 ManaTypePower 一样写个保底
        this.onAdded();
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("mana_attribute"),
                new SerializableData()
                        .add("modifierID", SerializableDataTypes.IDENTIFIER, null)
                        .add("max_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("regen_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("player_side", SerializableDataTypes.BOOLEAN, false),
                (data) -> (type, entity) -> new ManaAttributePower(type, entity, data.get("modifierID"), data.get("max_mana_modifier"), data.get("regen_mana_modifier"), data.get("player_side"))
        );
    }
}
