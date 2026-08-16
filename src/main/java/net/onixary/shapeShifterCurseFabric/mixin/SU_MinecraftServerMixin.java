package net.onixary.shapeShifterCurseFabric.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.util.SuperUserUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class SU_MinecraftServerMixin {
    @Inject(method = "getPermissionLevel", at = @At("HEAD"), cancellable = true)
    private void getPermissionLevel(GameProfile profile, CallbackInfoReturnable<Integer> cir) {
        int superUserLevel = SuperUserUtils.getCurrentPermissionLevel(profile.getId());
        if (superUserLevel != -1) {
            cir.setReturnValue(superUserLevel);
        }
    }
}
