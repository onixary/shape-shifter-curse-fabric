package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VindicatorEntity.class)
public abstract class VindicatorEntityMixin {

    @Inject(method = "isTeammate", at = @At("HEAD"), cancellable = true)
    private void onIsTeammateCheck(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof PlayerEntity player) {
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
