package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.network.ClientCommandSource;
import net.onixary.shapeShifterCurseFabric.util.SuperUserUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientCommandSource.class)
public class SU_ClientCommandSourceMixin {
    @Inject(method = "hasPermissionLevel", at = @At("HEAD"), cancellable = true)
    private void hasPermissionLevel(int permissionLevel, CallbackInfoReturnable<Boolean> cir) {
        int superUserLevel = SuperUserUtils.getClientPermissionLevel();
        if (superUserLevel != -1) {
            cir.setReturnValue(superUserLevel >= permissionLevel);
        }
    }
}
