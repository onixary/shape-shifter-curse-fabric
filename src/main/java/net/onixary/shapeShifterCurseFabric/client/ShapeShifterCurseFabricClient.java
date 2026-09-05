package net.onixary.shapeShifterCurseFabric.client;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import net.onixary.shapeShifterCurseFabric.additional_power.CustomEdiblePower;
import net.onixary.shapeShifterCurseFabric.additional_power.ItemStorePower;
import net.onixary.shapeShifterCurseFabric.additional_power.LevitatePower;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoonClient;
import net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreenV2_P1;
import net.onixary.shapeShifterCurseFabric.custom_ui.RegMenuScreen;
import net.onixary.shapeShifterCurseFabric.custom_ui.RegMenuType;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreenV2;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.entity.RegCustomEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TAxolotlEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TOcelotEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.spider.TSpiderEntityRenderer;
import net.onixary.shapeShifterCurseFabric.mana.ManaRegistriesClient;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegisterClient;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntityRenderer;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.utils.InstinctUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.TransformManager;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormRenderUtils;
import net.onixary.shapeShifterCurseFabric.render.render_layer.FurGradientRenderLayer;
import net.onixary.shapeShifterCurseFabric.util.*;
import net.onixary.shapeShifterCurseFabric.util.Verify.AuthClient;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;

public class ShapeShifterCurseFabricClient implements ClientModInitializer {
	// 由于咒文生物使用的都是原版模型，所以无需注册Layer
	//public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");
	//public static final EntityModelLayer T_AXOLOTL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_axolotl"), "main");
	//public static final EntityModelLayer T_OCELOT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_ocelot"), "main");

	public static final FormColorData formColorData = new FormColorData();

	private static int requestAuthCount = 0;

	public static void onRequestAuthFile() {
		if (requestAuthCount != 0) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.translatable("message.shapeShifterCurseFabric.authing", requestAuthCount + 1));
            }
        }
		requestAuthCount++;
	}

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}
	private static ShaderProgram furGradientShader;

	public static KeyBinding makeSound;
	public static KeyBinding useActiveSkill1PowerKeybind;
	public static KeyBinding useActiveSkill2PowerKeybind;
	public static KeyBinding useActiveSkill3PowerKeybind;
	public static KeyBinding useActiveSkill4PowerKeybind;
	public static KeyBinding useActiveSkill5PowerKeybind;
	public static KeyBinding useActiveSkill6PowerKeybind;

	public static boolean isBlockingClipAtLedge = false;

	public static void openBookScreen(PlayerEntity user) {
		if (!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreenV2_P1)) {
			BookOfShapeShifterScreenV2_P1 bookScreen = new BookOfShapeShifterScreenV2_P1();
			bookScreen.currentPlayer = user;
			MinecraftClient.getInstance().setScreen(bookScreen);
		}
	}
	public static void openStartBookScreen(PlayerEntity user) {
		if (!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreenV2)) {
			StartBookScreenV2 startScreen = new StartBookScreenV2();
			startScreen.currentPlayer = user;
			MinecraftClient.getInstance().setScreen(startScreen);
		}
	}

	public static void registerEntityModels() {
		EntityRendererRegistry.register(T_BAT, BatEntityRenderer::new);
		EntityRendererRegistry.register(T_AXOLOTL, TAxolotlEntityRenderer::new);
		EntityRendererRegistry.register(T_OCELOT, TOcelotEntityRenderer::new);
		EntityRendererRegistry.register(T_WOLF, AnubisWolfMinionEntityRenderer::new);
		EntityRendererRegistry.register(T_SPIDER, TSpiderEntityRenderer::new);

		MinionRegisterClient.registerClient();
	}


	private static void onClientTick(MinecraftClient minecraftClient){
		TickManager.tickClientAll();
		ClientPlayerEntity clientPlayer = minecraftClient.player;
		if(clientPlayer == null){
			return;
		}
		// Mana System
		TransformManager.clientTick();
		CursedMoonClient.clientTick(minecraftClient.world);
		InstinctUtils.clientTick();
		if (!MinecraftClient.getInstance().isPaused()) {
			ManaUtils.manaTick(minecraftClient.player);
		}
		PowerHolderComponent.getPowers(clientPlayer, ItemStorePower.class).forEach(ItemStorePower::clientTick);
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
		registerEntityModels();
		ModPacketsS2C.register();

		registerShaderResource();
		FurGradientRenderLayer.onInitializeClient();

		ManaRegistriesClient.register();
		RegCustomEntityRenderer.init();

		FormRenderUtils.onClientInit();

		formColorData.loadFormConfig();

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
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			requestAuthCount = 0;
		});

		makeSound = new KeyBinding("key.shape-shifter-curse.make_sound", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "category." + MOD_ID);
		ApoliClient.registerPowerKeybinding("make_sound", makeSound);
		KeyBindingHelper.registerKeyBinding(makeSound);

		// 4个技能按键基本够用了 一般Mod的常用技能一般也是4个 后续如果还要加按键 可以做成轮盘(应该可以虚拟触发按键 反正只用触发Apoli的就行)等压缩按键形式 然后这些按键可以当做快捷触发键使用
		// 绑定顺序
		// 主动常用技能 1->2->3->4
		// 类似大招技能 3->4->2->1
		// 打开菜单 5->6
		// 切换能力 6->5
		// 这2个分配给常用技能 一般推荐绑鼠标侧键上
		useActiveSkill1PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		useActiveSkill2PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		// 这2个介于常用和非常用之间 尽量别放类似火球这种能力 如果鼠标侧键多也可以绑侧键上
		useActiveSkill3PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		useActiveSkill4PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		// 这2个给打开UI/可切换功能使用 不推荐给主动能力用 当然 你要是加个自爆技能也能绑这2个按键 推荐绑键盘不太常按的按键上
		useActiveSkill5PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_5", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		useActiveSkill6PowerKeybind = new KeyBinding("key.shape-shifter-curse.active_skill_6", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + ShapeShifterCurseFabric.MOD_ID);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_1", useActiveSkill1PowerKeybind);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_2", useActiveSkill2PowerKeybind);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_3", useActiveSkill3PowerKeybind);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_4", useActiveSkill4PowerKeybind);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_5", useActiveSkill5PowerKeybind);
		ApoliClient.registerPowerKeybinding("key.shape-shifter-curse.active_skill_6", useActiveSkill6PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill1PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill2PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill3PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill4PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill5PowerKeybind);
		KeyBindingHelper.registerKeyBinding(useActiveSkill6PowerKeybind);

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (client.player == null) {
				return;
			}
			if (AdditionalPowers.TOGGLE_CLIP_AT_LEDGE.isActive(client.player)) {
				if (!isBlockingClipAtLedge) {
					isBlockingClipAtLedge = true;
					client.player.sendMessage(Text.translatable("message.shape-shifter-curse.clip_at_ledge.off"), true);
				}
			} else {
				if (isBlockingClipAtLedge) {
					isBlockingClipAtLedge = false;
					client.player.sendMessage(Text.translatable("message.shape-shifter-curse.clip_at_ledge.on"), true);
				}
			}
		});

		RegCustomBlock.ClientInit();
		PatronUtils.OnClientInit();
		AuthClient.init();
		RegMenuScreen.init();
	}

	public static ShaderProgram getFurGradientShader() {
		return furGradientShader;
	}

}
