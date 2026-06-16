package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class PlayerClipAtLedgeMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    private void clipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if (!ShapeShifterCurseFabricClient.isClipAtLedge) {
            cir.setReturnValue(false);
        }
        return;
    }

    @ModifyArg(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"), index = 1)
    private Box fixStepHeightClipAtLedge(Box par2) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (!par2.contains(realThis.getPos())) {
            return par2.withMaxY(realThis.getPos().y);
        }
        return par2;
    }
}