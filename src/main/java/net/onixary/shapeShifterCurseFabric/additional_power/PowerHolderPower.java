package net.onixary.shapeShifterCurseFabric.additional_power;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

public class PowerHolderPower extends Power {
    public Power TargetPower = null;

    public PowerHolderPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        // TODO 注入 PowerHolderComponent.getPowers 为返回值添加 PowerHolderPower.TargetPower
        super(type, entity);
        if (data.get("power_data_index") == null) {
            ShapeShifterCurseFabric.LOGGER.warn("Power Data Index is null!");
            return;
        }
        if (entity instanceof PlayerEntity pe && RegPlayerFormComponent.PLAYER_FORM.get(pe).getCurrentForm() instanceof PlayerFormDynamic pfd) {
            int TargetPowerIndex = data.get("power_data_index");
            JsonObject PowerData = pfd.PowerData.get(TargetPowerIndex);
            if (PowerData == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Power Data is null!");
                return;
            }
            try {
                Identifier PowerID = Identifier.tryParse(PowerData.get("type").getAsString());
                PowerFactory<Power> pf = ApoliRegistries.POWER_FACTORY.get(PowerID);
                if (pf == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("Power Factory is null!");
                    return;
                }
                PowerFactory<Power>.Instance pi = pf.read(PowerData);
                TargetPower = pi.apply(new PowerType<Power>(new Identifier(pfd.FormID.getNamespace(), pfd.FormID.getPath() + "_temp_power_" + TargetPowerIndex), pi), entity);
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.warn("Power Data is invalid!");
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public boolean shouldTick() {
        if (this.TargetPower == null) {return false;}
        return this.TargetPower.shouldTick();
    }

    @Override
    public boolean shouldTickWhenInactive() {
        if (this.TargetPower == null) {return false;}
        return this.TargetPower.shouldTickWhenInactive();
    }

    @Override
    public void tick() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.tick();
    }

    @Override
    public void onGained() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.onGained();
    }

    @Override
    public void onLost() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.onLost();
    }

    @Override
    public void onAdded() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.onAdded();
    }

    @Override
    public void onRemoved() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.onRemoved();
    }

    @Override
    public void onRespawn() {
        if (this.TargetPower == null) {return;}
        this.TargetPower.onRespawn();
    }

    @Override
    public boolean isActive() {
        if (this.TargetPower == null) {return false;}
        return this.TargetPower.isActive();
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("power_holder"),
                new SerializableData()
                        .add("power_data_index", SerializableDataTypes.INT),
                data -> (powerType, livingEntity) -> new PowerHolderPower(
                        powerType,
                        livingEntity,
                        data
                )
        ).allowCondition();
    }
}
