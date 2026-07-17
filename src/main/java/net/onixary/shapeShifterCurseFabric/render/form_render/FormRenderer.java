package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class FormRenderer extends GeoObjectRenderer<FormAnimatable> {
    public FormAnimatable realAnimatable = null;
    public FormModel realModel = null;

    public FormRenderer(JsonObject modelJson) {
        super(new FormModel(modelJson));
        this.realModel = (FormModel) this.model;
        this.realAnimatable = new FormAnimatable();
        this.animatable = this.realAnimatable;
    }

    public void setPlayer(PlayerEntity player, boolean slim) {
        this.realAnimatable.setPlayer(player);
        this.realModel.setPlayer(player, slim);
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, FormAnimatable animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        FormEyeBlinkController.applyForRenderedBone(animatable, bone);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
