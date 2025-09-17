package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.PillagerFriendlyPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MobEntityTeamMixin {
    @Inject(method = "isTeammate", at = @At("HEAD"), cancellable = true)
    private void onIsTeammateCheck(Entity other, CallbackInfoReturnable<Boolean> cir) {
        // `this` 在 Mixin 中指向 Mixin 类的实例，需要强制转换为 Entity
        Entity self = (Entity) (Object) this;

        // 检查目标实体是否为玩家
        if (other instanceof PlayerEntity player) {
            if(self instanceof net.minecraft.entity.mob.EvokerEntity ||
                    self instanceof net.minecraft.entity.mob.VindicatorEntity ||
                    self instanceof net.minecraft.entity.mob.RavagerEntity ||
                    self instanceof net.minecraft.entity.mob.PillagerEntity ||
                    self instanceof net.minecraft.entity.mob.WitchEntity)
            {
                if (PowerHolderComponent.hasPower(player, PillagerFriendlyPower.class)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

}
