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
        // 写个保底 治标不治本
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
        }
    }

    // 在能力获取
    @Override
    public void onGained() {
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
            // 获得 Power 时补满魔力
            ManaUtils.gainPlayerMana(playerEntity, Double.MAX_VALUE / 8);
        }
    }

    // 在能力移除
    @Override
    public void onLost() {
        // 不知道为什么有时Apoli会在玩家死亡时调用onLost 而且在我(XuHaoNan)电脑上复现概率极低 没法测具体原因 先写个治标不治本的解决方案
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.loseManaTypeID(playerEntity, manaType, manaSource);
            }
        }
    }

    @Override
    public void onRespawn() {
        // 写个保底 治标不治本
        if (this.entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
            // 调整：复活时也会补满魔力值
            ManaUtils.gainPlayerMana(playerEntity, Double.MAX_VALUE / 8);
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
