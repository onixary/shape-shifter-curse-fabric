package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
public class TOcelotEntityRenderer extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>> {
	private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/mob/t_ocelot.png");

	public TOcelotEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new OcelotEntityModel<>(context.getPart(EntityModelLayers.OCELOT)), 0.4F);
	}

	public Identifier getTexture(OcelotEntity ocelotEntity) {
		return TEXTURE;
	}
}
