package net.onixary.shapeShifterCurseFabric.client;

import io.github.apace100.apoli.component.PowerHolderComponent;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.CustomEdiblePower;
import net.onixary.shapeShifterCurseFabric.additional_power.LevitatePower;
import net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreenV2_P1;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreenV2;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TAxolotlEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TOcelotEntityRenderer;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.items.armors.MorphscaleArmorRenderer;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_animation.RegPlayerAnimation;
import net.onixary.shapeShifterCurseFabric.render.render_layer.FurGradientRenderLayer;
import net.onixary.shapeShifterCurseFabric.util.ClientTicker;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import net.onixary.shapeShifterCurseFabric.util.TickManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;

public class ShapeShifterCurseFabricClient implements ClientModInitializer {
	public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");
	public static final EntityModelLayer T_AXOLOTL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_axolotl"), "main");
	public static final EntityModelLayer T_OCELOT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_ocelot"), "main");

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}
	private static ShaderProgram furGradientShader;

	public static void openBookScreen(PlayerEntity user) {
		// 仅当owo_lib加载时才能调用旧版页面，否则回退回新版
		if (commonConfig.enableNewStartBook | !FabricLoader.getInstance().isModLoaded("owo")) {
			if (!commonConfig.enableNewStartBook) {
				// 以未安装owo_lib为理由进入新版页面时打印日志
				LOGGER.error("Owo lib is not installed! Old book screen is unavailable, opening new book screen instead.");
			}
			if (!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreenV2_P1)) {
				BookOfShapeShifterScreenV2_P1 bookScreen = new BookOfShapeShifterScreenV2_P1();
				bookScreen.currentPlayer = user;
				MinecraftClient.getInstance().setScreen(bookScreen);
			}
		}
		else {
			// 反射
			try {
				Class<?> ScreenClass = Class.forName("net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreen");
				Object startScreen = ScreenClass.getDeclaredConstructor().newInstance();
				startScreen.getClass().getMethod("setCurrentPlayer", PlayerEntity.class).invoke(startScreen, user);
				MinecraftClient.getInstance().setScreen((Screen) startScreen);
			}
			catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
				LOGGER.error("Failed to open old book screen!");
				return;
			}
//			if (!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreen)) {
//				BookOfShapeShifterScreen bookScreen = new BookOfShapeShifterScreen();
//				bookScreen.currentPlayer = user;
//				MinecraftClient.getInstance().setScreen(bookScreen);
//			}
		}
	}
	public static void openStartBookScreen(PlayerEntity user) {
		// 仅当owo_lib加载时才能调用旧版页面，否则回退回新版
		if (commonConfig.enableNewStartBook | !FabricLoader.getInstance().isModLoaded("owo")) {
			if (!commonConfig.enableNewStartBook) {
				// 以未安装owo_lib为理由进入新版页面时打印日志
				LOGGER.error("Owo lib is not installed! Old book screen is unavailable, opening new book screen instead.");
			}
			if (!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreenV2)) {
				StartBookScreenV2 startScreen = new StartBookScreenV2();
				startScreen.currentPlayer = user;
				MinecraftClient.getInstance().setScreen(startScreen);
			}
		}
		else {
			// 反射
			try {
				Class<?> ScreenClass = Class.forName("net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreen");
				Object startScreen = ScreenClass.getDeclaredConstructor().newInstance();
				startScreen.getClass().getMethod("setCurrentPlayer", PlayerEntity.class).invoke(startScreen, user);
				MinecraftClient.getInstance().setScreen((Screen) startScreen);
			}
			catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
				LOGGER.error("Failed to open old book screen!");
				return;
			}
//			if (!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen)) {
//				StartBookScreen startScreen = new StartBookScreen();
//				startScreen.currentPlayer = user;
//				MinecraftClient.getInstance().setScreen(startScreen);
//			}
		}
	}

	public static void registerEntityModels() {
		EntityRendererRegistry.register(T_BAT, BatEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(T_AXOLOTL, TAxolotlEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(T_AXOLOTL_LAYER, BatEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(T_OCELOT, TOcelotEntityRenderer::new); // i dont know why T_OCELOT_LAYER is unused, but im not gonna change that
		MinionRegister.registerClient();
	}

    public static void registerAzureArmorGeo(){
        AzArmorRendererRegistry.register(
                MorphscaleArmorRenderer::new,
                RegCustomItem.MORPHSCALE_HEADRING,
                RegCustomItem.MORPHSCALE_VEST,
                RegCustomItem.MORPHSCALE_CUISH,
                RegCustomItem.MORPHSCALE_ANKLET
        );
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

	// 原先的仅考虑到当前玩家变身动作 其他玩家变身动作不会更新
	public static class TransformingStage {
		public boolean IsTransforming = false;
        public String TransformFromForm = null;
        public String TransformToForm = null;
	}

	public static HashMap<UUID, TransformingStage> clientTransformState = new HashMap<>();

	public static TransformingStage getClientTransformState(UUID playerUuid) {
		if (!clientTransformState.containsKey(playerUuid)) {
			clientTransformState.put(playerUuid, new TransformingStage());
		}
		return clientTransformState.get(playerUuid);
	}

	public static void updateTransformState(UUID playerUuid, boolean isTransforming, String fromForm, String toForm) {
		if (!clientTransformState.containsKey(playerUuid)) {
			clientTransformState.put(playerUuid, new TransformingStage());
		}
		TransformingStage stage = clientTransformState.get(playerUuid);
		stage.IsTransforming = isTransforming;
		stage.TransformFromForm = fromForm;
		stage.TransformToForm = toForm;
		ShapeShifterCurseFabric.LOGGER.info("Updated client transform state: " + isTransforming +
											", from: " + fromForm + ", to: " + toForm);
	}

	// 获取客户端变身状态的方法（供动画系统使用）
	public static boolean isClientTransforming(UUID playerUuid) {
		return getClientTransformState(playerUuid).IsTransforming;
	}

	public static String getClientTransformFromForm(UUID playerUuid) {
		return getClientTransformState(playerUuid).TransformFromForm;
	}

	public static String getClientTransformToForm(UUID playerUuid) {
		return getClientTransformState(playerUuid).TransformToForm;
	}

	private void registerShaderResource()
	{
		CoreShaderRegistrationCallback.EVENT.register(context -> {
			// 1. 定义着色器的 Identifier
			Identifier shaderId = new Identifier(ShapeShifterCurseFabric.MOD_ID, "fur_gradient_remap");

			// 2. 使用 context.register 方法注册
			//    这个方法会处理底层的加载逻辑
			context.register(shaderId, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, program -> {
				// 3. 将加载好的 ShaderProgram 实例保存到我们的静态变量中
				ShapeShifterCurseFabricClient.furGradientShader = program;
			});
		});
	}

	@Override
	public void onInitializeClient() {
		RegPlayerAnimation.register();
		registerEntityModels();
		ModPacketsS2C.register();

		registerShaderResource();
		FurGradientRenderLayer.onInitializeClient();
        registerAzureArmorGeo();

		ClientTickEvents.END_CLIENT_TICK.register(ShapeShifterCurseFabricClient::onClientTick);
		// 客户端能力处理
		ClientTickEvents.START_CLIENT_TICK.register((minecraftClient) -> {
			ClientPlayerEntity clientPlayer = minecraftClient.player;
			if(clientPlayer == null){
				return;
			}
			// 由于LevitatePower覆写了isActive没法通过getPowers获取到
			// List<LevitatePower> clientLevitatePower = PowerHolderComponent.getPowers(clientPlayer, LevitatePower.class);
			// if (!clientLevitatePower.isEmpty()) {
			// 	// getFirst是Java21的新特性 Java17没有
			// 	LevitatePower power = clientLevitatePower.get(0);
			// 	power.clientTick(clientPlayer);
			// }
			PowerHolderComponent.KEY.get(clientPlayer).getPowers().stream().filter(p -> p instanceof LevitatePower).forEach(p -> ((LevitatePower) p).clientTick(clientPlayer));
			CustomEdiblePower.OnClientTick(clientPlayer);
		});
		PatronUtils.OnClientInit();
	}

	public static ShaderProgram getFurGradientShader() {
		return furGradientShader;
	}

}
