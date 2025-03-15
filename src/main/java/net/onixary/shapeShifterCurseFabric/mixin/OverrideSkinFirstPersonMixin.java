package net.onixary.shapeShifterCurseFabric.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
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
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// issue: OverrideSkinFirstPersonMixin会与某些其他mod不兼容，需要寻找原因所在
@Mixin(PlayerEntityRenderer.class)
public abstract class OverrideSkinFirstPersonMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    protected OverrideSkinFirstPersonMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowSize) {
        super(ctx, model, shadowSize);
    }

    @Inject(
            method =  "renderArm",
            at = @At("HEAD"),
            cancellable = true
    )

    private void shape_shifter_curse$onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if(RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm() != PlayerForms.ORIGINAL_BEFORE_ENABLE && !ShapeShifterCurseFabric.CONFIG.keepOriginalSkin())
        {

            // 渲染手臂

            Identifier CUSTOM_SKIN =
                    new Identifier(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");
            RenderSystem.disableCull();
            MinecraftClient.getInstance().getTextureManager().bindTexture(CUSTOM_SKIN);
            VertexConsumer vertexConsumerArm = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(CUSTOM_SKIN));
            arm.render(matrices, vertexConsumerArm, light, OverlayTexture.DEFAULT_UV);
            VertexConsumer vertexConsumerSleeve = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(CUSTOM_SKIN));
            sleeve.render(matrices, vertexConsumerSleeve, light, OverlayTexture.DEFAULT_UV);
            RenderSystem.enableCull();
            ci.cancel(); // 取消默认渲染
        }
    }
}
