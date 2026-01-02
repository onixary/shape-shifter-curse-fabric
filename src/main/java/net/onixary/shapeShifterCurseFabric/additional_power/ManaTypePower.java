package net.onixary.shapeShifterCurseFabric.additional_power;

import blue.endless.jankson.annotation.Nullable;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;

public class ManaTypePower extends Power {
    private @Nullable Identifier manaType = null;
    private @Nullable Identifier manaSource = null;
    private boolean isApplyed = false;

    public ManaTypePower(PowerType<?> type, LivingEntity entity, @Nullable Identifier manaType, @Nullable Identifier manaSource) {
        super(type, entity);
        this.manaType = manaType;
        if (manaSource == null) {
            this.manaSource = type.getIdentifier();
        } else {
            this.manaSource = manaSource;
        }
    }


    // 在能力获取 + 玩家进游戏时
    @Override
    public void onAdded() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null && !isApplyed) {
            ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            this.isApplyed = true;
        }
    }

    /*
    // 在能力移除 + 玩家退游戏时
    @Override
    public void onRemoved() {
        return;
    }
     */

    // 在能力获取
    @Override
    public void onGained() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null && !isApplyed) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
                ManaUtils.gainPlayerManaWithTime(playerEntity, Double.MAX_VALUE / 8, 8);  // 没有防溢出 别直接用Double.MAX_VALUE
                this.isApplyed = true;
            }
        }
    }

    // 在能力移除
    @Override
    public void onLost() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null && isApplyed) {
            ManaUtils.loseManaTypeID(playerEntity, manaType, manaSource);
            this.isApplyed = false;
        }
    }

    @Override
    public void onRespawn() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null && !isApplyed) {
            ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            // 调整：复活时也会补满魔力值
            ManaUtils.gainPlayerMana(playerEntity, Double.MAX_VALUE / 8);
            this.isApplyed = true;
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
