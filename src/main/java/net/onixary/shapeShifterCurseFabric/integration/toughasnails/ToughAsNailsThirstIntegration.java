package net.onixary.shapeShifterCurseFabric.integration.toughasnails;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.thirst.ThirstHandler;

public class ToughAsNailsThirstIntegration {
    public static void addThirst(PlayerEntity player, int amount) {
        IThirst thirst = ThirstHelper.getThirst(player);
        int modifiedThirst = Math.max(0, Math.min(20, thirst.getThirst() + amount));
        thirst.setThirst(modifiedThirst);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ThirstHandler.syncThirst(serverPlayer);
        }
    }
}
