package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.spider;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
public class TSpiderEntityRenderer extends MobEntityRenderer<TransformativeSpiderEntity, SpiderEntityModel<TransformativeSpiderEntity>> {
	private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/mob/t_spider.png");

	public TSpiderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SpiderEntityModel<>(context.getPart(EntityModelLayers.SPIDER)), 0.8F);
	}

	public Identifier getTexture(TransformativeSpiderEntity ocelotEntity) {
		return TEXTURE;
	}

	@Override
	protected void scale(TransformativeSpiderEntity entity, MatrixStack matrices, float amount) {
		matrices.scale(0.5f, 0.5f, 0.5f);
	}
}
