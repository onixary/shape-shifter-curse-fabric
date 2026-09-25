package net.onixary.shapeShifterCurseFabric.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.render.form_render.ICanGetLastPos;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements ICanGetLastPos {
    @Unique
    private static final Identifier CUSTOM_SKIN = new Identifier(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");

    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true, order = 1000)
    private void shape_shifter_curse$modifyPlayerSkin(CallbackInfoReturnable<Identifier> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
        if (!RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player))
        {
            if (FormTextureUtils.useTempCustomSkinConfig && MinecraftClient.getInstance().player == player) {
                if (FormTextureUtils.tempCustomSkinConfigOverrider.keepOriginalSkin()) {
                    return;
                } else {
                    cir.setReturnValue(CUSTOM_SKIN);
                    return;
                }
            }
            if (!RegPlayerSkinComponent.SKIN_SETTINGS.get(player).shouldKeepOriginalSkin()) {
                cir.setReturnValue(CUSTOM_SKIN);
                return;
            }
        }
        return;
    }

    @Unique
    private Vec3d lastPos = Vec3d.ZERO;

    @Inject(method = "tick", at = @At("HEAD"))
    private void shape_shifter_curse$lastPos(CallbackInfo ci) {
        lastPos = ((AbstractClientPlayerEntity) (Object) this).getPos();
    }

    @Override
    public Vec3d shape_shifter_curse_fabric$getLastPos() {
        return lastPos;
    }

    @Override
    public double shape_shifter_curse_fabric$getLastPosX() {
        return lastPos.x;
    }

    @Override
    public double shape_shifter_curse_fabric$getLastPosY() {
        return lastPos.y;
    }

    @Override
    public double shape_shifter_curse_fabric$getLastPosZ() {
        return lastPos.z;
    }
}
