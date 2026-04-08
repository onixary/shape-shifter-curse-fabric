package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.IronGolemHostilePower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemHostileMixin extends GolemEntity {

    protected IronGolemHostileMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addHostileTargetGoal(CallbackInfo ci) {
        this.targetSelector.add(3, new ActiveTargetGoal<>(
                (IronGolemEntity) (Object) this,
                LivingEntity.class,
                10,
                true,
                false,
                entity -> PowerHolderComponent.hasPower(entity, IronGolemHostilePower.class)
        ));
    }
}
