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

public class ConditionedManaAttributePower extends Power {
    private final @Nullable Identifier modifierID;
    private final @Nullable ManaUtils.Modifier maxManaModifier;
    private final @Nullable ManaUtils.Modifier regenManaModifier;
    private final boolean playerSide;
    private final int tickRate;
    private boolean isAdded = false;

    public ConditionedManaAttributePower(PowerType<?> type, LivingEntity entity, @Nullable Identifier modifierID, @Nullable ManaUtils.Modifier maxManaModifier, @Nullable ManaUtils.Modifier manaRegenModifier, boolean playerSide, int tickRate)  {
        super(type, entity);
        this.modifierID = modifierID;
        this.maxManaModifier = maxManaModifier;
        this.regenManaModifier = manaRegenModifier;
        this.playerSide = playerSide;
        this.setTicking(true);
        this.tickRate = tickRate;
    }

    public void tick() {
        if (this.entity.age % this.tickRate == 0) {
            if (this.isActive()) {
                if (!isAdded) {
                    this.AddAttr();
                }
            } else {
                if (isAdded){
                    this.DelAttr();
                }
            }
        }

    }

    public void AddAttr() {
        this.isAdded = true;
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

    public void DelAttr() {
        this.isAdded = false;
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

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("conditioned_mana_attribute"),
                new SerializableData()
                        .add("modifierID", SerializableDataTypes.IDENTIFIER, null)
                        .add("max_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("regen_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("player_side", SerializableDataTypes.BOOLEAN, false)
                        .add("tick_rate", SerializableDataTypes.INT, 20),
                (data) -> (type, entity) -> new ConditionedManaAttributePower(type, entity, data.get("modifierID"), data.get("max_mana_modifier"), data.get("regen_mana_modifier"), data.get("player_side"), data.get("tick_rate"))
        ).allowCondition();
    }
}
