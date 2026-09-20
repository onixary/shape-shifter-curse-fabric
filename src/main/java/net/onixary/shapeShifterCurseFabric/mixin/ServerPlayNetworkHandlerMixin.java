package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onUpdatePlayerAbilities", at = @At("TAIL"))
    private void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet, CallbackInfo ci) {
        PlayerFormComponent.COMPONENT.get(this.player).checkFlyUpdate();
    }
}
