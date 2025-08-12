package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.FoxFriendlyPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FoxEntity.class)
public class FoxEntityMixin {

    @Inject(method = "canTrust", at = @At("HEAD"), cancellable = true)
    private void allowTrustingFriendlyPlayers(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        FoxEntity fox = (FoxEntity) (Object) this;

        // 查找拥有该 UUID 的玩家
        PlayerEntity player = fox.getWorld().getPlayerByUuid(uuid);

        // 如果找到玩家且该玩家拥有 FoxFriendlyPower，则信任该玩家
        if (PowerHolderComponent.hasPower(player, FoxFriendlyPower.class)) {
            cir.setReturnValue(true);
        }
    }
}
