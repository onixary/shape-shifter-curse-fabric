package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.KeepSneakingPower;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityForceInputMixin {
    // 我当时在想什么呢 怎么这里用上static了
    @Unique
    private boolean lastForceSneakState = false;

    @Unique
    private int ticker = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // 检查玩家是否拥有并激活了 KeepSneakingPower
        boolean shouldForceSneak = PowerHolderComponent.getPowers(player, KeepSneakingPower.class)
                .stream()
                .anyMatch(KeepSneakingPower::isActive);
        // 发送状态到客户端
        if (shouldForceSneak != lastForceSneakState || ticker++ > 100) {
            lastForceSneakState = shouldForceSneak;
            ticker = 0;
            ModPacketsS2CServer.sendForceSneakState(player, shouldForceSneak);
        }
    }

}
