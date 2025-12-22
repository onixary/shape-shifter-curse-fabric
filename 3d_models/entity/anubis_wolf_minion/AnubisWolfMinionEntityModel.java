// Made with Blockbench 5.0.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AnubisWolfMinionEntityModel extends EntityModel<AnubisWolfMinionEntityModel> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart upperBody;
	private final ModelPart leg0;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart tail;
	public AnubisWolfMinionEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.upperBody = root.getChild("upperBody");
		this.leg0 = root.getChild("leg0");
		this.leg1 = root.getChild("leg1");
		this.leg2 = root.getChild("leg2");
		this.leg3 = root.getChild("leg3");
		this.tail = root.getChild("tail");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new Dilation(0.0F))
		.uv(16, 14).cuboid(-3.4F, -5.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(38, 14).cuboid(-2.3F, -6.0F, -0.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(16, 14).cuboid(1.4F, -5.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(38, 14).cuboid(1.3F, -6.0F, -0.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 10).cuboid(-1.5F, 0.9844F, -5.0F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 13.5F, -7.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 14).cuboid(-4.0F, -3.0F, -3.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData upperBody = modelPartData.addChild("upperBody", ModelPartBuilder.create().uv(21, 0).cuboid(-4.0F, -3.0F, -3.0F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F))
		.uv(43, 18).cuboid(-1.0F, -5.3F, 2.2F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 14.0F, -3.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData leg0 = modelPartData.addChild("leg0", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(52, 18).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 16.0F, 7.0F));

		ModelPartData leg1 = modelPartData.addChild("leg1", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(52, 18).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 16.0F, 7.0F));

		ModelPartData leg2 = modelPartData.addChild("leg2", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(52, 6).cuboid(-1.6F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 16.0F, -4.0F));

		ModelPartData leg3 = modelPartData.addChild("leg3", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(52, 6).cuboid(0.6F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 16.0F, -4.0F));

		ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(9, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(52, 24).cuboid(-0.5F, 4.0F, 0.3F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 12.0F, 8.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(AnubisWolfMinionEntityModel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		upperBody.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg0.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		tail.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}