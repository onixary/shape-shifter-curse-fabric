package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BatEntityRenderer extends MobEntityRenderer<BatEntity, net.minecraft.client.render.entity.model.BatEntityModel> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/mob/t_bat.png");

    public BatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BatEntityModel(context.getPart(EntityModelLayers.BAT)), 0.25F);
    }

    public Identifier getTexture(BatEntity batEntity) {
        return TEXTURE;
    }

    protected void scale(BatEntity batEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.35F, 0.35F, 0.35F);
    }

    protected void setupTransforms(BatEntity batEntity, MatrixStack matrixStack, float f, float g, float h) {
        if (batEntity.isRoosting()) {
            matrixStack.translate(0.0F, -0.1F, 0.0F);
        } else {
            matrixStack.translate(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
        }

        super.setupTransforms(batEntity, matrixStack, f, g, h);
    }
}
