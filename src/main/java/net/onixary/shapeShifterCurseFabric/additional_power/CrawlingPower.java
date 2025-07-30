package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class CrawlingPower extends Power {
    private float originalEyeHeightScale = 0.0f;
    private float originalHitboxHeightScale = 0.0f;
    private final static float CRAWL_EYE_HEIGHT_SCALE = 0.35f;
    private final static float CRAWL_HITBOX_HEIGHT_SCALE = 0.6f;

    public CrawlingPower(PowerType<?> powerType, LivingEntity livingEntity) {
        super(powerType, livingEntity);
        this.setTicking(true);
        if (livingEntity instanceof ServerPlayerEntity player) {
            ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
            ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
            originalEyeHeightScale = scaleDataEyeHeight.getScale();
            originalHitboxHeightScale = scaleDataHitboxHeight.getScale();
        }
    }

    @Override
    public void tick() {
        if (entity instanceof ServerPlayerEntity player) {
            if(player.isSneaking()) {
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
                Apoli.identifier("crawling"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new CrawlingPower(
                        powerType,
                        livingEntity
                )
        ).allowCondition();
    }

}
