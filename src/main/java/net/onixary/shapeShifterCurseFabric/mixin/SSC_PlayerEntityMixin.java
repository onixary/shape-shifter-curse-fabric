package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 原先的PlayerEntityMixin是Origin Furs的Mixin 为了减少后续移除Origins套件的工作量 所以单独写一个新的Mixin
@Mixin(PlayerEntity.class)
public class SSC_PlayerEntityMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    private void clipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        PlayerFormBase currentForm = FormAbilityManager.getForm((PlayerEntity) (Object)this);
        if (currentForm.getCanSneakRush()) {
            cir.setReturnValue(false);
        }
        return;
    }
}
