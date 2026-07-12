package net.onixary.shapeShifterCurseFabric.mixin.render;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.NoRenderArmPower;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormRenderFeature;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    // 挂载Feature
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void onInit(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.addFeature(new FormRenderFeature<>((PlayerEntityRenderer) (Object) this));
    }

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$RenderArm_HEAD(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.ignoreNoRenderArmPower && PowerHolderComponent.hasPower(player, NoRenderArmPower.class)) {  // 不渲染手臂情况
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V", shift = At.Shift.AFTER))
    private void shape_shifter_curse$RenderArm_setModelPose_AFTER(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型-根据模型设置修改手臂组件渲染
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        PlayerEntityRenderer realThis = (PlayerEntityRenderer) (Object) this;
        FormRenderFeature.rFPM_PartA(realThis, matrices, vertexConsumers, light, player, arm, sleeve);
    }

    @Inject(method = "renderArm", at = @At("RETURN"))
    private void shape_shifter_curse$RenderArm_RETURN(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        PlayerEntityRenderer realThis = (PlayerEntityRenderer) (Object) this;
        FormRenderFeature.rFPM_PartB(realThis, matrices, vertexConsumers, light, player, arm, sleeve);
    }
}
