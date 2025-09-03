package net.onixary.shapeShifterCurseFabric.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.NoRenderArmPower;
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// issue: OverrideSkinFirstPersonMixin会与某些其他mod不兼容，需要寻找原因所在
@Mixin(PlayerEntityRenderer.class)
public abstract class OverrideSkinFirstPersonMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    protected OverrideSkinFirstPersonMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowSize) {
        super(ctx, model, shadowSize);
    }

    // 自定义皮肤路径
    private static final Identifier CUSTOM_SKIN = new Identifier(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");

    @Inject(
            method =  "renderArm",
            at = @At("HEAD"),
            cancellable = true
    )

    private void shape_shifter_curse$onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(player, NoRenderArmPower.class)) {  // 不渲染手臂情况
            ci.cancel();
        }
    }

    @Redirect(method="renderArm", at= @At(value="INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTexture()Lnet/minecraft/util/Identifier;"))
    private Identifier shape_shifter_curse$getSkinTexture(AbstractClientPlayerEntity player) {
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm() != PlayerForms.ORIGINAL_BEFORE_ENABLE)  // 仅当玩家激活Mod后才进行修改
        {
            if (!RegPlayerSkinComponent.SKIN_SETTINGS.get(player).shouldKeepOriginalSkin()) {
                return CUSTOM_SKIN;
            }
        }
        return player.getSkinTexture();
    }
}
