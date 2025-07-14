package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.WaterFlexibilityPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class WaterFlexibilityMixin {
    @Unique
    private static final float MAX_FLEXIBILITY = 0.98F;

    @ModifyVariable(
            method = "travel",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private float modifyWaterMovementSpeed(float f) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // 只对玩家生效且在水中
        if (!(entity instanceof PlayerEntity player) || !entity.isTouchingWater()) {
            return f;
        }

        // 获取水流抵抗Power
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);

        for (WaterFlexibilityPower power : component.getPowers(WaterFlexibilityPower.class)) {
            if (power.isActive()) {
                float resistance = power.getResistance();

                float targetSpeed = 0.8F + (MAX_FLEXIBILITY - 0.8F) * resistance;
                return targetSpeed;
            }
        }

        return f;
    }
}
