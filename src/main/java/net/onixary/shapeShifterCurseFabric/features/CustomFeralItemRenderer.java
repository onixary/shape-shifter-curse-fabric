package net.onixary.shapeShifterCurseFabric.features;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class CustomFeralItemRenderer {
	private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
	private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
	private static final float field_32735 = -0.4F;
	private static final float field_32736 = 0.2F;
	private static final float field_32737 = -0.2F;
	private static final float field_32738 = -0.6F;
	private static final float EQUIP_OFFSET_TRANSLATE_X = 0.56F;
	private static final float EQUIP_OFFSET_TRANSLATE_Y = -0.52F;
	private static final float EQUIP_OFFSET_TRANSLATE_Z = -0.72F;
	private static final float field_32742 = 45.0F;
	private static final float field_32743 = -80.0F;
	private static final float field_32744 = -20.0F;
	private static final float field_32745 = -20.0F;
	private static final float EAT_OR_DRINK_X_ANGLE_MULTIPLIER = 10.0F;
	private static final float EAT_OR_DRINK_Y_ANGLE_MULTIPLIER = 90.0F;
	private static final float EAT_OR_DRINK_Z_ANGLE_MULTIPLIER = 30.0F;
	private static final float field_32749 = 0.6F;
	private static final float field_32750 = -0.5F;
	private static final float field_32751 = 0.0F;
	private static final double field_32752 = 27.0;
	private static final float field_32753 = 0.8F;
	private static final float field_32754 = 0.1F;
	private static final float field_32755 = -0.3F;
	private static final float field_32756 = 0.4F;
	private static final float field_32757 = -0.4F;
	private static final float ARM_HOLDING_ITEM_SECOND_Y_ANGLE_MULTIPLIER = 70.0F;
	private static final float ARM_HOLDING_ITEM_FIRST_Z_ANGLE_MULTIPLIER = -20.0F;
	private static final float field_32690 = -0.6F;
	private static final float field_32691 = 0.8F;
	private static final float field_32692 = 0.8F;
	private static final float field_32693 = -0.75F;
	private static final float field_32694 = -0.9F;
	private static final float field_32695 = 45.0F;
	private static final float field_32696 = -1.0F;
	private static final float field_32697 = 3.6F;
	private static final float field_32698 = 3.5F;
	private static final float ARM_HOLDING_ITEM_TRANSLATE_X = 5.6F;
	private static final int ARM_HOLDING_ITEM_X_ANGLE_MULTIPLIER = 200;
	private static final int ARM_HOLDING_ITEM_THIRD_Y_ANGLE_MULTIPLIER = -135;
	private static final int ARM_HOLDING_ITEM_SECOND_Z_ANGLE_MULTIPLIER = 120;
	private static final float field_32703 = -0.4F;
	private static final float field_32704 = -0.2F;
	private static final float field_32705 = 0.0F;
	private static final float field_32706 = 0.04F;
	private static final float field_32707 = -0.72F;
	private static final float field_32708 = -1.2F;
	private static final float field_32709 = -0.5F;
	private static final float field_32710 = 45.0F;
	private static final float field_32711 = -85.0F;
	private static final float ARM_X_ANGLE_MULTIPLIER = 45.0F;
	private static final float ARM_Y_ANGLE_MULTIPLIER = 92.0F;
	private static final float ARM_Z_ANGLE_MULTIPLIER = -41.0F;
	private static final float ARM_TRANSLATE_X = 0.3F;
	private static final float ARM_TRANSLATE_Y = -1.1F;
	private static final float ARM_TRANSLATE_Z = 0.45F;
	private static final float field_32718 = 20.0F;
	private static final float FIRST_PERSON_MAP_FIRST_SCALE = 0.38F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_X = -0.5F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_Y = -0.5F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_Z = 0.0F;
	private static final float FIRST_PERSON_MAP_SECOND_SCALE = 0.0078125F;
	private static final int field_32724 = 7;
	private static final int field_32725 = 128;
	private static final int field_32726 = 128;
	private static final float field_32727 = 0.0F;
	private static final float field_32728 = 0.0F;
	private static final float field_32729 = 0.04F;
	private static final float field_32730 = 0.0F;
	private static final float field_32731 = 0.004F;
	private static final float field_32732 = 0.0F;
	private static final float field_32733 = 0.2F;
	private static final float field_32734 = 0.1F;
	private final MinecraftClient client;
	public ItemStack mainHand = ItemStack.EMPTY;
	private ItemStack offHand = ItemStack.EMPTY;
	public float equipProgressMainHand;
	public float prevEquipProgressMainHand;
	private float equipProgressOffHand;
	private float prevEquipProgressOffHand;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private final ItemRenderer itemRenderer;

	public CustomFeralItemRenderer(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		this.client = client;
		this.entityRenderDispatcher = entityRenderDispatcher;
		this.itemRenderer = itemRenderer;
	}

	public void renderItem(
		LivingEntity entity,
		ItemStack stack,
		ModelTransformationMode renderMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!stack.isEmpty()) {
			this.itemRenderer
				.renderItem(
					entity,
					stack,
					renderMode,
					leftHanded,
					matrices,
					vertexConsumers,
					entity.getWorld(),
					light,
					OverlayTexture.DEFAULT_UV,
					entity.getId() + renderMode.ordinal()
				);
		}
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
		RenderSystem.setShaderTexture(0, this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.entityRenderDispatcher.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrices.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(92.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * -41.0F));
		matrices.translate(f * 0.3F, -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, this.client.player);
		}

		matrices.pop();
	}

	private void renderMapInOneHand(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack
	) {
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.translate(f * 0.125F, -0.125F, 0.0F);
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0F));
			this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
			matrices.pop();
		}

		matrices.push();
		matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
		float g = MathHelper.sqrt(swingProgress);
		float h = MathHelper.sin(g * (float) Math.PI);
		float i = -0.5F * h;
		float j = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float k = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate(f * i, j - 0.3F * h, k);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * -45.0F));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * h * -30.0F));
		this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
		matrices.pop();
	}

	private void renderMapInBothHands(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress
	) {
		float f = MathHelper.sqrt(swingProgress);
		float g = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		float h = -0.4F * MathHelper.sin(f * (float) Math.PI);
		matrices.translate(0.0F, -g / 2.0F, h);
		float i = this.getMapAngle(pitch);
		matrices.translate(0.0F, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.72F);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * -85.0F));
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
			this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
			this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
			matrices.pop();
		}

		float j = MathHelper.sin(f * (float) Math.PI);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * 20.0F));
		matrices.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
		matrices.scale(0.38F, 0.38F, 0.38F);
		matrices.translate(-0.5F, -0.5F, 0.0F);
		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
		Integer integer = FilledMapItem.getMapId(stack);
		MapState mapState = FilledMapItem.getMapState(integer, this.client.world);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgress).next();
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, integer, mapState, false, swingProgress);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float f = bl ? 1.0F : -1.0F;
		float g = MathHelper.sqrt(swingProgress);
		float h = -0.3F * MathHelper.sin(g * (float) Math.PI);
		float i = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float j = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate(f * (h + 0.64000005F), i + -0.6F + equipProgress * -0.6F, j + -0.71999997F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 45.0F));
		float k = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float l = MathHelper.sin(g * (float) Math.PI);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * l * 70.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * k * -20.0F));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		RenderSystem.setShaderTexture(0, abstractClientPlayerEntity.getSkinTexture());
		matrices.translate(f * -1.0F, 3.6F, 3.5F);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 120.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(200.0F));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * -135.0F));
		matrices.translate(f * 5.6F, 0.0F, 0.0F);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.entityRenderDispatcher
			.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
		float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F;
		float g = f / (float)stack.getMaxUseTime();
		if (g < 0.8F) {
			float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
			matrices.translate(0.0F, h, 0.0F);
		}

		float h = 1.0F - (float)Math.pow((double)g, 27.0);
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate(h * 0.6F * (float)i, h * -0.5F, h * 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * h * 90.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * 10.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * h * 30.0F));
	}

	private void applyBrushTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, float equipProgress) {
		this.applyEquipOffset(matrices, arm, equipProgress);
		float f = (float)(this.client.player.getItemUseTimeLeft() % 10);
		float g = f - tickDelta + 1.0F;
		float h = 1.0F - g / 10.0F;
		float i = -90.0F;
		float j = 60.0F;
		float k = 150.0F;
		float l = -15.0F;
		int m = 2;
		float n = -15.0F + 75.0F * MathHelper.cos(h * 2.0F * (float) Math.PI);
		if (arm != Arm.RIGHT) {
			matrices.translate(0.1, 0.83, 0.35);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(n));
			matrices.translate(-0.3, 0.22, 0.35);
		} else {
			matrices.translate(-0.25, 0.22, 0.35);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(n));
		}
	}

	private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0F + f * -20.0F)));
		float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0F));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0F));
	}

	private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate((float)i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
	}

	public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
		float f = player.getHandSwingProgress(tickDelta);
		Hand hand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
		float g = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
		CustomFeralItemRenderer.HandRenderType handRenderType = getHandRenderType(player);
		float h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
		float i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((player.getPitch(tickDelta) - h) * 0.1F));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((player.getYaw(tickDelta) - i) * 0.1F));
		if (handRenderType.renderMainHand) {
			float j = hand == Hand.MAIN_HAND ? f : 0.0F;
			float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
		}

		if (handRenderType.renderOffHand) {
			float j = hand == Hand.OFF_HAND ? f : 0.0F;
			float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, j, this.offHand, k, matrices, vertexConsumers, light);
		}

		vertexConsumers.draw();
	}

	@VisibleForTesting
	static CustomFeralItemRenderer.HandRenderType getHandRenderType(ClientPlayerEntity player) {
		ItemStack itemStack = player.getMainHandStack();
		ItemStack itemStack2 = player.getOffHandStack();
		boolean bl = itemStack.isOf(Items.BOW) || itemStack2.isOf(Items.BOW);
		boolean bl2 = itemStack.isOf(Items.CROSSBOW) || itemStack2.isOf(Items.CROSSBOW);
		if (!bl && !bl2) {
			return CustomFeralItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		} else if (player.isUsingItem()) {
			return getUsingItemHandRenderType(player);
		} else {
			return isChargedCrossbow(itemStack) ? CustomFeralItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : CustomFeralItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		}
	}

	private static CustomFeralItemRenderer.HandRenderType getUsingItemHandRenderType(ClientPlayerEntity player) {
		ItemStack itemStack = player.getActiveItem();
		Hand hand = player.getActiveHand();
		if (!itemStack.isOf(Items.BOW) && !itemStack.isOf(Items.CROSSBOW)) {
			return hand == Hand.MAIN_HAND && isChargedCrossbow(player.getOffHandStack())
				? CustomFeralItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY
				: CustomFeralItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		} else {
			return CustomFeralItemRenderer.HandRenderType.shouldOnlyRender(hand);
		}
	}

	private static boolean isChargedCrossbow(ItemStack stack) {
		return stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack);
	}

	public void renderFirstPersonItem(
		AbstractClientPlayerEntity player,
		float tickDelta,
		float pitch,
		Hand hand,
		float swingProgress,
		ItemStack item,
		float equipProgress,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!player.isUsingSpyglass()) {
			boolean bl = hand == Hand.MAIN_HAND;
			Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
			matrices.push();
			if (item.isEmpty()) {
				if (bl && !player.isInvisible()) {
					this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
				}
			} else if (item.isOf(Items.FILLED_MAP)) {
				if (bl && this.offHand.isEmpty()) {
					this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
				} else {
					this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
				}
			} else if (item.isOf(Items.CROSSBOW)) {
				boolean bl2 = CrossbowItem.isCharged(item);
				boolean bl3 = arm == Arm.RIGHT;
				int i = bl3 ? 1 : -1;
				if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
					this.applyEquipOffset(matrices, arm, equipProgress);
					matrices.translate((float)i * -0.4785682F, -0.094387F, 0.05731531F);
					matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
					matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 65.3F));
					matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * -9.785F));
					float f = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
					float g = f / (float)CrossbowItem.getPullTime(item);
					if (g > 1.0F) {
						g = 1.0F;
					}

					if (g > 0.1F) {
						float h = MathHelper.sin((f - 0.1F) * 1.3F);
						float j = g - 0.1F;
						float k = h * j;
						matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
					}

					matrices.translate(g * 0.0F, g * 0.0F, g * 0.04F);
					matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
					matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * 45.0F));
				} else {
					float fx = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
					float gx = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
					float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
					matrices.translate((float)i * fx, gx, h);
					this.applyEquipOffset(matrices, arm, equipProgress);
					this.applySwingOffset(matrices, arm, swingProgress);
					if (bl2 && swingProgress < 0.001F && bl) {
						matrices.translate((float)i * -0.641864F, 0.0F, 0.0F);
						matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 10.0F));
					}
				}

				this.renderItem(
					player,
					item,
					bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
					!bl3,
					matrices,
					vertexConsumers,
					light
				);
			} else {
				boolean bl2 = arm == Arm.RIGHT;
				if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
					int l = bl2 ? 1 : -1;
					switch (item.getUseAction()) {
						case NONE:
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case EAT:
						case DRINK:
							this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case BLOCK:
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case BOW:
							this.applyEquipOffset(matrices, arm, equipProgress);
							matrices.translate((float)l * -0.2785682F, 0.18344387F, 0.15731531F);
							matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
							matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3F));
							matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785F));
							float mx = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
							float fxx = mx / 20.0F;
							fxx = (fxx * fxx + fxx * 2.0F) / 3.0F;
							if (fxx > 1.0F) {
								fxx = 1.0F;
							}

							if (fxx > 0.1F) {
								float gx = MathHelper.sin((mx - 0.1F) * 1.3F);
								float h = fxx - 0.1F;
								float j = gx * h;
								matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
							}

							matrices.translate(fxx * 0.0F, fxx * 0.0F, fxx * 0.04F);
							matrices.scale(1.0F, 1.0F, 1.0F + fxx * 0.2F);
							matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0F));
							break;
						case SPEAR:
							this.applyEquipOffset(matrices, arm, equipProgress);
							matrices.translate((float)l * -0.5F, 0.7F, 0.1F);
							matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
							matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3F));
							matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785F));
							float m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
							float fx = m / 10.0F;
							if (fx > 1.0F) {
								fx = 1.0F;
							}

							if (fx > 0.1F) {
								float gx = MathHelper.sin((m - 0.1F) * 1.3F);
								float h = fx - 0.1F;
								float j = gx * h;
								matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
							}

							matrices.translate(0.0F, 0.0F, fx * 0.2F);
							matrices.scale(1.0F, 1.0F, 1.0F + fx * 0.2F);
							matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0F));
							break;
						case BRUSH:
							this.applyBrushTransformation(matrices, tickDelta, arm, item, equipProgress);
					}
				} else if (player.isUsingRiptide()) {
					this.applyEquipOffset(matrices, arm, equipProgress);
					int l = bl2 ? 1 : -1;
					matrices.translate((float)l * -0.4F, 0.8F, 0.3F);
					matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 65.0F));
					matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -85.0F));
				} else {
					float n = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
					float mxx = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
					float fxxx = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
					int o = bl2 ? 1 : -1;
					matrices.translate((float)o * n, mxx, fxxx);
					this.applyEquipOffset(matrices, arm, equipProgress);
					this.applySwingOffset(matrices, arm, swingProgress);
				}

				this.renderItem(
					player,
					item,
					bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
					!bl2,
					matrices,
					vertexConsumers,
					light
				);
			}

			matrices.pop();
		}
	}

	public void updateHeldItems() {
		this.prevEquipProgressMainHand = this.equipProgressMainHand;
		this.prevEquipProgressOffHand = this.equipProgressOffHand;
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (ItemStack.areEqual(this.mainHand, itemStack)) {
			this.mainHand = itemStack;
		}

		if (ItemStack.areEqual(this.offHand, itemStack2)) {
			this.offHand = itemStack2;
		}

		if (clientPlayerEntity.isRiding()) {
			this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
			this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
			this.equipProgressMainHand = this.equipProgressMainHand
				+ MathHelper.clamp((this.mainHand == itemStack ? f * f * f : 0.0F) - this.equipProgressMainHand, -0.4F, 0.4F);
			this.equipProgressOffHand = this.equipProgressOffHand
				+ MathHelper.clamp((float)(this.offHand == itemStack2 ? 1 : 0) - this.equipProgressOffHand, -0.4F, 0.4F);
		}

		if (this.equipProgressMainHand < 0.1F) {
			this.mainHand = itemStack;
		}

		if (this.equipProgressOffHand < 0.1F) {
			this.offHand = itemStack2;
		}
	}

	public void resetEquipProgress(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			this.equipProgressMainHand = 0.0F;
		} else {
			this.equipProgressOffHand = 0.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	@VisibleForTesting
	static enum HandRenderType {
		RENDER_BOTH_HANDS(true, true),
		RENDER_MAIN_HAND_ONLY(true, false),
		RENDER_OFF_HAND_ONLY(false, true);

		final boolean renderMainHand;
		final boolean renderOffHand;

		private HandRenderType(boolean renderMainHand, boolean renderOffHand) {
			this.renderMainHand = renderMainHand;
			this.renderOffHand = renderOffHand;
		}

		public static CustomFeralItemRenderer.HandRenderType shouldOnlyRender(Hand hand) {
			return hand == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
		}
	}
}
