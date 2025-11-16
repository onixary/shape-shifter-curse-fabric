package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.AxolotlEntity.Variant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Locale;
import java.util.Map;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
public class TAxolotlEntityRenderer extends MobEntityRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {
	private static final Map<Variant, Identifier> TEXTURES = Util.make(Maps.<Variant, Identifier>newHashMap(), variants -> {
		for (Variant variant : Variant.values()) {
			// 替换变体名称中的非法字符':'为'/'
			String safeVariantName = variant.getName().replace(':', '/');
			variants.put(variant, new Identifier(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", safeVariantName)));
		}
	});

	private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/mob/t_axolotl.png");

	public TAxolotlEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AxolotlEntityModel<>(context.getPart(EntityModelLayers.AXOLOTL)), 0.5F);
	}

	public Identifier getTexture(AxolotlEntity axolotlEntity) {
		return TEXTURE; //有个多余的 (Identifier) 强制转换，我去掉了
	}
}