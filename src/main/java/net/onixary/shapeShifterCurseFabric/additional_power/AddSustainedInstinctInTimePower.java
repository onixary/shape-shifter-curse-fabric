package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctEffectType;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applySustainedEffect;

public class AddSustainedInstinctInTimePower extends Power {

    private final InstinctEffectType instinctEffectType;
    private final int duration;
    private final boolean isOnItemFinished;
    private int remainingDuration;
    private int currentTicks = 0;
    private boolean isEffectActive = false;

    public AddSustainedInstinctInTimePower(PowerType<?> type, LivingEntity entity, String instinctEffectType, int duration, boolean isOnItemFinished) {
        super(type, entity);
        this.duration = duration;
        this.isOnItemFinished = isOnItemFinished;
        remainingDuration = duration;
        InstinctEffectType effectType = null;
        try {
            effectType = InstinctEffectType.valueOf(instinctEffectType);
        } catch (IllegalArgumentException e) {
            // Handle the error, for example, log it or set a default value
            ShapeShifterCurseFabric.LOGGER.error("Invalid instinct effect type: " + instinctEffectType + ", it should be matching the enum InstinctEffectType");
        }
        this.instinctEffectType = effectType;
        isEffectActive = false;
        currentTicks = 0;
        this.setTicking(true);
    }

    public void tick() {
        // 食用食物的时间是30tick，直接在这里写死
        // 非常屎山的写法，暂时先这样
        if(isActive()){
            if(isOnItemFinished){
                currentTicks++;
                if(currentTicks >= 28){
                    isEffectActive = true;
                }
            }
            else{
                isEffectActive = true;
            }
        }
        else if(isEffectActive){
            if(entity instanceof ServerPlayerEntity && instinctEffectType != null && instinctEffectType.isSustained()) {
                if(remainingDuration > 0){
                    ShapeShifterCurseFabric.LOGGER.info("Applying sustained effect bt power: " + instinctEffectType);
                    applySustainedEffect((ServerPlayerEntity)entity, instinctEffectType);
                    remainingDuration--;
                }
                else{
                    remainingDuration = this.duration;
                    currentTicks = 0;
                    isEffectActive = false;
                }
            }
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            Apoli.identifier("add_sustained_instinct_in_time"),
            new SerializableData()
                .add("instinct_effect_type", SerializableDataTypes.STRING)
                    .add("duration", SerializableDataTypes.INT, 20)
                .add("is_on_item_finished", SerializableDataTypes.BOOLEAN, false),
            data -> (powerType, livingEntity) -> new AddSustainedInstinctInTimePower(
                powerType,
                livingEntity,
                data.getString("instinct_effect_type"),
                data.getInt("duration"),
                data.getBoolean("is_on_item_finished")
            )
        ).allowCondition();
    }

}