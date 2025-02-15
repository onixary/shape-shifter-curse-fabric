package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class OverrideSkinThirdPersonMixin {

    // 自定义皮肤路径
    private static final Identifier CUSTOM_SKIN =
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");

    @Inject(
            method = "getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shape_shifter_curse$onGetTexture(Entity entity, CallbackInfoReturnable<Identifier> cir) {
        // 在这里可以添加自定义逻辑来决定是否使用自定义皮肤
        if(entity instanceof PlayerEntity player) {
            if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm() != PlayerForms.ORIGINAL_BEFORE_ENABLE && !ShapeShifterCurseFabric.CONFIG.keepOriginalSkin()) {
                cir.setReturnValue(CUSTOM_SKIN);
                cir.cancel();
            }
        }

    }
}
