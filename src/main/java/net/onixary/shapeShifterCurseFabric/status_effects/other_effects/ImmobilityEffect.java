package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ImmobilityEffect extends StatusEffect {
    public ImmobilityEffect() {
        super(StatusEffectCategory.HARMFUL, 0x000000); // Color can be changed as needed
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity){
            double speedY = entity.getVelocity().y;
            entity.setVelocity(0, speedY, 0);
            entity.velocityModified = true;
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // Apply effect every tick
    }


}
