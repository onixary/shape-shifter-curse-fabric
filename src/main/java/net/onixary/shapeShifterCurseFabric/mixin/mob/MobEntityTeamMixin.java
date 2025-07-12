package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
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
                PlayerForms curForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
                boolean isFamiliarFox = curForm == PlayerForms.FAMILIAR_FOX_1
                        || curForm == PlayerForms.FAMILIAR_FOX_2
                        || curForm == PlayerForms.FAMILIAR_FOX_3;

                if (isFamiliarFox) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

}
