package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
public class TAxolotlEntityRenderer extends MobEntityRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {

	// 1. 删除了整个 TEXTURES 静态映射的定义

	private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mob/t_axolotl.png");

	public TAxolotlEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AxolotlEntityModel<>(context.getPart(EntityModelLayers.AXOLOTL)), 0.5F);
	}

	@Override
	public Identifier getTexture(AxolotlEntity axolotlEntity) {
		return TEXTURE;
	}
}