package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.util.SuperUserUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class SU_ServerPlayerEntityMixin {
    @Inject(method = "getPermissionLevel", at = @At("HEAD"), cancellable = true)
    private void getPermissionLevel(CallbackInfoReturnable<Integer> cir) {
        int superUserLevel = SuperUserUtils.getCurrentPermissionLevel((ServerPlayerEntity) (Object) this);
        if (superUserLevel != -1) {
            cir.setReturnValue(superUserLevel);
        }
    }
}
