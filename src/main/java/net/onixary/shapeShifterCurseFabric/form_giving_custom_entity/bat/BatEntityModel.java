package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class BatEntityModel extends SinglePartEntityModel<BatEntity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;

	public BatEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
		this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
		this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
		this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
				EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.NONE
		);
		modelPartData2.addChild(
				EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), ModelTransform.NONE
		);
		modelPartData2.addChild(
				EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
				EntityModelPartNames.BODY,
				ModelPartBuilder.create().uv(0, 16).cuboid(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F).uv(0, 34).cuboid(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F),
				ModelTransform.NONE
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
				EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(42, 0).cuboid(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), ModelTransform.NONE
		);
		modelPartData4.addChild(
				EntityModelPartNames.RIGHT_WING_TIP,
				ModelPartBuilder.create().uv(24, 16).cuboid(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
				ModelTransform.pivot(-12.0F, 1.0F, 1.5F)
		);
		ModelPartData modelPartData5 = modelPartData3.addChild(
				EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(42, 0).mirrored().cuboid(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), ModelTransform.NONE
		);
		modelPartData5.addChild(
				EntityModelPartNames.LEFT_WING_TIP,
				ModelPartBuilder.create().uv(24, 16).mirrored().cuboid(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
				ModelTransform.pivot(12.0F, 1.0F, 1.5F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j) {
		if (batEntity.isRoosting()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = (float) Math.PI - i * (float) (Math.PI / 180.0);
			this.head.roll = (float) Math.PI;
			this.head.setPivot(0.0F, -2.0F, 0.0F);
			this.rightWing.setPivot(-3.0F, 0.0F, 3.0F);
			this.leftWing.setPivot(3.0F, 0.0F, 3.0F);
			this.body.pitch = (float) Math.PI;
			this.rightWing.pitch = (float) (-Math.PI / 20);
			this.rightWing.yaw = (float) (-Math.PI * 2.0 / 5.0);
			this.rightWingTip.yaw = -1.7278761F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.leftWingTip.yaw = -this.rightWingTip.yaw;
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
			this.head.roll = 0.0F;
			this.head.setPivot(0.0F, 0.0F, 0.0F);
			this.rightWing.setPivot(0.0F, 0.0F, 0.0F);
			this.leftWing.setPivot(0.0F, 0.0F, 0.0F);
			this.body.pitch = (float) (Math.PI / 4) + MathHelper.cos(h * 0.1F) * 0.15F;
			this.body.yaw = 0.0F;
			this.rightWing.yaw = MathHelper.cos(h * 74.48451F * (float) (Math.PI / 180.0)) * (float) Math.PI * 0.25F;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.rightWingTip.yaw = this.rightWing.yaw * 0.5F;
			this.leftWingTip.yaw = -this.rightWing.yaw * 0.5F;
		}
	}
}