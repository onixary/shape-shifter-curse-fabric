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

public class CrawlingPower extends Power {
    private float originalEyeHeightScale = 0.0f;
    private float originalHitboxHeightScale = 0.0f;
    private float CRAWL_EYE_HEIGHT_SCALE = 0.0f;
    private float CRAWL_HITBOX_HEIGHT_SCALE = 0.0f;

    public CrawlingPower(PowerType<?> powerType, LivingEntity livingEntity, float scale, float eyeScale, float activeScale, float activeEyeScale) {
        super(powerType, livingEntity);
        this.setTicking(true);
        originalHitboxHeightScale = scale;
        originalEyeHeightScale = eyeScale;
        CRAWL_EYE_HEIGHT_SCALE = activeEyeScale;
        CRAWL_HITBOX_HEIGHT_SCALE = activeScale;
    }

    @Override
    public void tick() {
        if (entity instanceof ServerPlayerEntity player) {
            if(this.isActive()) {
                ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
                ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                scaleDataEyeHeight.setScale(CRAWL_EYE_HEIGHT_SCALE);
                scaleDataHitboxHeight.setScale(CRAWL_HITBOX_HEIGHT_SCALE);
            } else {
                ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
                ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                scaleDataEyeHeight.setScale(originalEyeHeightScale);
                scaleDataHitboxHeight.setScale(originalHitboxHeightScale);
            }
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("crawling"),
                new SerializableData()
                        .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                        .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f)
                        .add("active_scale", SerializableDataTypes.FLOAT, 0.6f)
                        .add("active_eye_scale", SerializableDataTypes.FLOAT, 0.35f),
                data -> (powerType, livingEntity) -> new CrawlingPower(
                        powerType,
                        livingEntity,
                        data.getFloat("scale"),
                        data.getFloat("eye_scale"),
                        data.getFloat("active_scale"),
                        data.getFloat("active_eye_scale")
                )
        ).allowCondition();
    }

}
