package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ConditionScalePower extends Power {
    private ScaleData scaleDataWidth;
    private ScaleData scaleDataHeight;
    private ScaleData scaleDataEyeHeight;
    private ScaleData scaleDataHitboxHeight;

    private float original_scale;
    private float original_eye_scale;
    private float scale;
    private float eye_scale;

    private boolean IsPowerActive = false;

    public ConditionScalePower(PowerType<?> type, LivingEntity entity, float scale, float eye_scale, float original_scale, float original_eye_scale) {
        super(type, entity);
        if(entity instanceof ServerPlayerEntity) {
            this.scaleDataWidth = ScaleTypes.WIDTH.getScaleData(entity);
            this.scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(entity);
            this.scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(entity);
            this.scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(entity);
            this.scale = scale;
            this.eye_scale = eye_scale;
            this.original_scale = original_scale;
            this.original_eye_scale = original_eye_scale;
            this.setTicking(true);
        }
    }

    @Override
    public void tick() {
        if (this.isActive() && !this.IsPowerActive)  {
            this.ApplyScale();
        } else if (!this.isActive() && this.IsPowerActive) {
            this.RemoveScale();
        }
    }

    public void ApplyScale() {
        this.scaleDataWidth.setScale(scale);
        this.scaleDataWidth.setPersistence(true);
        this.scaleDataHeight.setScale(scale);
        this.scaleDataHeight.setPersistence(true);
        this.scaleDataEyeHeight.setScale(eye_scale);
        this.scaleDataEyeHeight.setPersistence(true);
        this.scaleDataHitboxHeight.setScale(eye_scale);
        this.scaleDataHitboxHeight.setPersistence(true);
        this.IsPowerActive = true;
    }

    public void RemoveScale() {
        this.scaleDataWidth.setScale(original_scale);
        this.scaleDataWidth.setPersistence(true);
        this.scaleDataHeight.setScale(original_scale);
        this.scaleDataHeight.setPersistence(true);
        this.scaleDataEyeHeight.setScale(original_eye_scale);
        this.scaleDataEyeHeight.setPersistence(true);
        this.scaleDataHitboxHeight.setScale(original_eye_scale);
        this.scaleDataHitboxHeight.setPersistence(true);
        this.IsPowerActive = false;
    }

    public void onRemoved() {
        if (this.IsPowerActive) {
            this.RemoveScale();
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("condition_scale"),
                new SerializableData()
                        .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                        .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f)
                        .add("original_scale", SerializableDataTypes.FLOAT, 1.0f)
                        .add("original_eye_scale", SerializableDataTypes.FLOAT, 1.0f),
                data -> (powerType, livingEntity) -> new ConditionScalePower(
                        powerType,
                        livingEntity,
                        data.getFloat("scale"),
                        data.getFloat("eye_scale"),
                        data.getFloat("original_scale"),
                        data.getFloat("original_eye_scale")
                )
        ).allowCondition();
    }

}