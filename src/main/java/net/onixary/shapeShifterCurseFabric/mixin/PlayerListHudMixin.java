package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @ModifyReturnValue(method = "getPlayerName", at = @At("RETURN"))
    private Text getPlayerName(Text original, @Local(argsOnly = true) PlayerListEntry entry) {
        UUID uuid = entry.getProfile().getId();
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity nowPlayer = client.player;
        PlayerEntity playerEntity = null;
        if (client.world != null) {
            playerEntity = client.world.getPlayerByUuid(uuid);
        }
        if (nowPlayer == null || playerEntity == null) {
            return original;
        }
        if (nowPlayer.input.sneaking) {
            IForm form = FormUtils.getPlayerForm(playerEntity);
            if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isEquals(form)) {
                return original;
            }
            Text formName = form.getContentText(CodexData.ContentType.NAME);
            return Text.of(original.getString() + " | " + formName.getString());
        }
        return original;
    }
}
