package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.onixary.shapeShifterCurseFabric.util.SuperUserUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerCommandSource.class)
public class SU_ServerCommandSourceMixin {
    @Shadow
    @Final
    @Nullable
    private Entity entity;

    @Inject(method = "hasPermissionLevel", at = @At("HEAD"), cancellable = true)
    private void hasPermissionLevel(int permissionLevel, CallbackInfoReturnable<Boolean> cir) {
        Entity nowEntity = this.entity;
        if (nowEntity instanceof PlayerEntity playerEntity) {
            int superUserLevel = SuperUserUtils.getCurrentPermissionLevel(playerEntity);
            if (superUserLevel != -1) {
                cir.setReturnValue(superUserLevel >= permissionLevel);
            }
        }
    }
}
