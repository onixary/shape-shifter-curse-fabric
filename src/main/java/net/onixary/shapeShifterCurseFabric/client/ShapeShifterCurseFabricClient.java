package net.onixary.shapeShifterCurseFabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreen;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreen;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TAxolotlEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TOcelotEntityRenderer;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_animation.RegPlayerAnimation;
import net.onixary.shapeShifterCurseFabric.util.ClientTicker;
import net.onixary.shapeShifterCurseFabric.util.TickManager;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;

public class ShapeShifterCurseFabricClient implements ClientModInitializer {
	public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");
	public static final EntityModelLayer T_AXOLOTL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_axolotl"), "main");
	public static final EntityModelLayer T_OCELOT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_ocelot"), "main");

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}

	public static void openBookScreen(PlayerEntity user) {
		if (!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreen)) {
			BookOfShapeShifterScreen bookScreen = new BookOfShapeShifterScreen();
			bookScreen.currentPlayer = user;
			MinecraftClient.getInstance().setScreen(bookScreen);
		}
	}
	public static void openStartBookScreen(PlayerEntity user) {
		if (!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen)) {
			StartBookScreen startScreen = new StartBookScreen();
			startScreen.currentPlayer = user;
			MinecraftClient.getInstance().setScreen(startScreen);
		}
	}

	public static void registerEntityModels() {
		EntityRendererRegistry.register(T_BAT, BatEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(T_AXOLOTL, TAxolotlEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(T_AXOLOTL_LAYER, BatEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(T_OCELOT, TOcelotEntityRenderer::new); // i dont know why T_OCELOT_LAYER is unused, but im not gonna change that
	}

	private static void onClientTick(MinecraftClient minecraftClient){
		TickManager.tickClientAll();
	}

	public static void emitTransformParticle(int duration) {
		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		if(clientPlayer == null){
			return;
		}


		// similar to DOTween in Unity
		Runnable particleTask = () -> {
			for (int i = 0; i < 2; i++) {
				clientPlayer.getWorld().addParticle(StaticParams.PLAYER_TRANSFORM_PARTICLE,
					clientPlayer.getX() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
					clientPlayer.getY() + clientPlayer.getRandom().nextDouble() * 1.5 + 1,
					clientPlayer.getZ() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
					0, -1, 0);
				//ShapeShifterCurseFabric.LOGGER.info("Particle effect emitted");
			}
		};

		// Get the Minecraft client instance
		MinecraftClient client = MinecraftClient.getInstance();
		// Create and start the client ticker
		ClientTicker ticker = new ClientTicker(client, particleTask, duration);
		ticker.start();
	}

	public static void applyInstinctThresholdEffect() {
		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		if(clientPlayer == null){
			return;
		}

		for (int i = 0; i < 1; i++) {
			clientPlayer.getWorld().addParticle(StaticParams.PLAYER_TRANSFORM_PARTICLE,
				clientPlayer.getX() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.5,
				clientPlayer.getY() + clientPlayer.getRandom().nextDouble() * 1,
				clientPlayer.getZ() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.5,
				0, 1, 0.5);
		}
	}

	// 添加动画刷新方法
	public static void refreshPlayerAnimations() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			ShapeShifterCurseFabric.LOGGER.info("Refreshing player animations on client");
			// 强制重新初始化动画系统
			// 这里可以触发动画重新注册或刷新
		}
	}

	// 添加变身状态更新方法
	private static boolean clientIsTransforming = false;
	private static String clientTransformFromForm = null;
	private static String clientTransformToForm = null;

	public static void updateTransformState(boolean isTransforming, String fromForm, String toForm) {
		clientIsTransforming = isTransforming;
		clientTransformFromForm = fromForm;
		clientTransformToForm = toForm;
		ShapeShifterCurseFabric.LOGGER.info("Updated client transform state: " + isTransforming +
											", from: " + fromForm + ", to: " + toForm);
	}

	// 获取客户端变身状态的方法（供动画系统使用）
	public static boolean isClientTransforming() {
		return clientIsTransforming;
	}

	public static String getClientTransformFromForm() {
		return clientTransformFromForm;
	}

	public static String getClientTransformToForm() {
		return clientTransformToForm;
	}

	@Override
	public void onInitializeClient() {
		RegPlayerAnimation.register();
		registerEntityModels();
		ModPacketsS2C.register();

		ClientTickEvents.END_CLIENT_TICK.register(ShapeShifterCurseFabricClient::onClientTick);
	}
}
