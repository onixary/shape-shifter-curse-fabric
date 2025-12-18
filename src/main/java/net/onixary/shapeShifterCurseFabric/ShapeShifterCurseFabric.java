package net.onixary.shapeShifterCurseFabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.additional_power.*;
import net.onixary.shapeShifterCurseFabric.advancement.*;
import net.onixary.shapeShifterCurseFabric.command.CustomFormArgumentType;
import net.onixary.shapeShifterCurseFabric.command.FormArgumentType;
import net.onixary.shapeShifterCurseFabric.command.ShapeShifterCurseCommand;
import net.onixary.shapeShifterCurseFabric.config.ClientConfig;
import net.onixary.shapeShifterCurseFabric.config.CommonConfig;
import net.onixary.shapeShifterCurseFabric.config.PlayerCustomConfig;
import net.onixary.shapeShifterCurseFabric.data.CursedMoonData;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegTransformativeEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegTransformativeEntitySpawnEgg;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.TransformativeEntitySpawning;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf.TransformativeWolfEntity;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.items.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.minion.RegPlayerMinionComponent;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_animation.form_animation.AnimationTransform;
import net.onixary.shapeShifterCurseFabric.player_form.FormDataPackReloadListener;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import net.onixary.shapeShifterCurseFabric.util.PlayerEventHandler;
import net.onixary.shapeShifterCurseFabric.util.TickManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager.saveForm;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.saveInstinctComp;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;


public class ShapeShifterCurseFabric implements ModInitializer {

    public static final String MOD_ID = "shape-shifter-curse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Path MOD_LOCAL_DATA_STORAGE = Path.of("ssc_data");

    public static PlayerCustomConfig playerCustomConfig;
    public static ClientConfig clientConfig;
    public static CommonConfig commonConfig;

    // 用于在游戏内测试调参用的临时变量
    public static Vec3d feralItemCenter = new Vec3d(0.0F, 0.0F, 0.0F);
    public static Vec3d feralItemPosOffset = new Vec3d(0.0F, 0.0F, 0.0F);
    public static float feralItemEulerX = 0.0F;

    public static CursedMoonData cursedMoonData = new CursedMoonData();
    // Reg custom advancement criterion
    public static final OnEnableMod ON_ENABLE_MOD = Criteria.register(new OnEnableMod());
    public static final OnOpenBookOfShapeShifter ON_OPEN_BOOK_OF_SHAPE_SHIFTER = Criteria.register(new OnOpenBookOfShapeShifter());
    public static final OnEndCursedMoon ON_END_CURSED_MOON = Criteria.register(new OnEndCursedMoon());
    public static final OnEndCursedMoonCured ON_END_CURSED_MOON_CURED = Criteria.register(new OnEndCursedMoonCured());
    public static final OnEndCursedMoonCuredForm2 ON_END_CURSED_MOON_CURED_FORM_2 = Criteria.register(new OnEndCursedMoonCuredForm2());
    public static final OnGetTransformEffect ON_GET_TRANSFORM_EFFECT = Criteria.register(new OnGetTransformEffect());
    public static final OnSleepWhenHaveTransformEffect ON_SLEEP_WHEN_HAVE_TRANSFORM_EFFECT = Criteria.register(new OnSleepWhenHaveTransformEffect());
    public static final OnTransform0 ON_TRANSFORM_0 = Criteria.register(new OnTransform0());
    public static final OnTransform1 ON_TRANSFORM_1 = Criteria.register(new OnTransform1());
    public static final OnTransform2 ON_TRANSFORM_2 = Criteria.register(new OnTransform2());
    public static final OnTransformByCatalyst ON_TRANSFORM_BY_CATALYST = Criteria.register(new OnTransformByCatalyst());
    public static final OnTransformByCure ON_TRANSFORM_BY_CURE = Criteria.register(new OnTransformByCure());
    public static final OnUseGoldenApple ON_USE_GOLDEN_APPLE = Criteria.register(new OnUseGoldenApple());
    public static final OnTransformByCureFinal ON_TRANSFORM_BY_CURE_FINAL = Criteria.register(new OnTransformByCureFinal());
    public static final OnTransformEffectFade ON_TRANSFORM_EFFECT_FADE = Criteria.register(new OnTransformEffectFade());
    public static final OnTriggerCursedMoon ON_TRIGGER_CURSED_MOON = Criteria.register(new OnTriggerCursedMoon());
    public static final OnTriggerCursedMoonForm2 ON_TRIGGER_CURSED_MOON_FORM_2 = Criteria.register(new OnTriggerCursedMoonForm2());
    public static final OnFirstJoinWithMod ON_FIRST_JOIN_WITH_MOD = Criteria.register(new OnFirstJoinWithMod());
    public static final OnEndCursedMoonBuggedForm2 ON_END_CURSED_MOON_BUGGED_FORM_2 = Criteria.register(new OnEndCursedMoonBuggedForm2());

    // Reg custom entities
    // Bat
    public static final EntityType<TransformativeBatEntity> T_BAT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_bat"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, TransformativeBatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    // Axolotl
    public static final EntityType<TransformativeAxolotlEntity> T_AXOLOTL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_axolotl"),
            FabricEntityTypeBuilder.create(SpawnGroup.AXOLOTLS, TransformativeAxolotlEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    // Ocelot
    public static final EntityType<TransformativeOcelotEntity> T_OCELOT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_ocelot"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TransformativeOcelotEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static final EntityType<TransformativeWolfEntity> T_WOLF = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_wolf"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TransformativeWolfEntity::new)
                .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                .build()
    );


    private int save_timer = 0;


    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }


    /**
     * 注册动画系统
     * 这个方法需要在服务端也执行，以确保变换动画能够正确同步
     */
    private static void registerAnimations() {
        try {
            // 注册变换动画
            AnimationTransform.registerAnims();
            LOGGER.info("Transform animations registered successfully");
        } catch (Exception e) {
            LOGGER.warn("Failed to register transform animations: " + e.getMessage());
        }
    }

    @Override
    public void onInitialize() {
        // PlayerDataStorage.initialize(); // 移除这行，因为这里还没有服务器实例
        RegCustomItem.initialize();
        RegTransformativeEntitySpawnEgg.initialize();
        RegTStatusEffect.initialize();
        RegTStatusPotionEffect.initialize();
        PlayerEventHandler.register();
        RegTransformativeEntity.register();
        RegOtherStatusEffects.initialize();
        TransformativeEntitySpawning.addEntitySpawns();
        BatAttachEventHandler.register();
        // 注册动画（需要在服务端也执行以支持变换动画的同步）
        registerAnimations();

        AdditionalEntityConditions.register();
        AdditionalItemCondition.register();
        AdditionalPowers.register();
        AdditionalEntityActions.register();

        // 注册召唤物属性
        MinionRegister.register();

        // 注册配置文件
        AutoConfig.register(PlayerCustomConfig.class, Toml4jConfigSerializer::new);  // 客户端配置
        playerCustomConfig = AutoConfig.getConfigHolder(PlayerCustomConfig.class).getConfig();
        AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);  // 客户端配置
        clientConfig = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        AutoConfig.register(CommonConfig.class, Toml4jConfigSerializer::new);  // 双端配置
        commonConfig = AutoConfig.getConfigHolder(CommonConfig.class).getConfig();

        // network package
        ModPacketsC2S.register();
        cursedMoonData = new CursedMoonData();

        //TransformFX.INSTANCE.registerCallbacks();
        TransformOverlay.INSTANCE.init();
        save_timer = 0;

        // Reg potions
        RegCustomPotions.registerPotions();
        RegCustomPotions.registerPotionsRecipes();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // 获取主世界作为默认世界
            ServerWorld overworld = server.getOverworld();
            FormAbilityManager.getServerWorld(overworld);
            // 更新Patron状态
            PatronUtils.OnServerLoad(server);
        });
        // 获取动态Form(DataPack)
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new FormDataPackReloadListener());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> server.getPlayerManager().getPlayerList().forEach((player) -> {
            ModPacketsS2CServer.updateDynamicForm(player);
            if (!player.getComponent(RegPlayerFormComponent.PLAYER_FORM).isCurrentFormExist()) {
                FormAbilityManager.applyForm(player, RegPlayerForms.ORIGINAL_BEFORE_ENABLE);
            }
        }));
        initLocalDataStorage();

        // Reg origins content

        // do not reset effect when player respawn or enter hell

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ShapeShifterCurseCommand.register(dispatcher));
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of(MOD_ID, "form_argument_type"),
                FormArgumentType.class,
                ConstantArgumentSerializer.of(FormArgumentType::new)
        );
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of(MOD_ID, "custom_form_argument_type"),
                CustomFormArgumentType.class,
                ConstantArgumentSerializer.of(CustomFormArgumentType::new)
        );
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity player = handler.player;
            // 清空玩家召唤物
            MinionRegister.DisSpawnAllMinion(player);
            LOGGER.info("Player disconnect, save attachment");
            // saveCurrentAttachment(server.getOverworld(), player);
            saveForm(player);
            saveInstinctComp(player);
            // save cursed moon data
            ShapeShifterCurseFabric.cursedMoonData.getInstance().save(server.getOverworld());
        });

        // Reg listeners
        ServerTickEvents.END_SERVER_TICK.register(this::onPlayerServerTick);
        EntitySleepEvents.STOP_SLEEPING.register((entity, world) -> {
            if (entity instanceof PlayerEntity) {
                onPlayerEndSleeping(entity);
            }
        });
        // allow sleep when status effect is active
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((entity, world, pos) -> {
            if (entity instanceof PlayerEntity) {
                if (EffectManager.hasTransformativeEffect(entity)) {
                    return ActionResult.success(true);
                }
                else{
                    return ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        });

        /// Debug instinct: unregister this to see instinct debug info
        //InstinctDebugHUD.register();

        /*HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                for (StatusEffectInstance effect : player.getStatusEffects()) {
                    if (effect.getEffectType() instanceof BaseTransformativeStatusEffect) {
                        Text description = Text.translatable(effect.getEffectType().getTranslationKey() + ".description");
                        drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, description, 0, 0, 0xFFFFFF);
                    }
                }
            }
        });*/
        //TStatusHUDHandler.register();

        /*EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);

        // entity spawn replacer
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof BatEntity) {
                // 50% 概率替换为自定义蝙蝠
                if (world.getRandom().nextFloat() < 0.5f) {
                    TransformativeBatEntity customBat = new TransformativeBatEntity(
                            T_BAT, world
                    );
                    customBat.refreshPositionAndAngles(
                            entity.getX(), entity.getY(), entity.getZ(),
                            entity.getYaw(), entity.getPitch()
                    );
                    world.spawnEntity(customBat);
                    entity.discard(); // 移除原版蝙蝠
                }
            }
        });*/


        //LOGGER.info(CONFIG.keepOriginalSkin() ? "Original skin will be kept." : "Override skin");
    }

    private void initLocalDataStorage() {
        if (!PatronUtils.EnablePatronFeature) {
            return;
        }
        File dataFolder = MOD_LOCAL_DATA_STORAGE.toFile();
        if (!dataFolder.isDirectory()) {
            try {
                Files.createDirectories(dataFolder.toPath());
            } catch (IOException e) {
                LOGGER.error("Failed to create local data storage folder", e);
            }
        }
    }

    private void onPlayerEndSleeping(LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            // handle transformative effects
            //LOGGER.info(EffectManager.EFFECT_ATTACHMENT.toString());
            //PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
            //LOGGER.info(attachment == null? "attachment is null" : attachment.currentEffect.toString());
            // 不用检测诅咒之月状态--作为一个特性还挺有意思的
            if (EffectManager.hasTransformativeEffect(player)) {
                EffectManager.ActiveTransformativeEffect(player);
                // 触发自定义成就
                ON_SLEEP_WHEN_HAVE_TRANSFORM_EFFECT.trigger(player);
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_sleep_when_attached").formatted(Formatting.LIGHT_PURPLE));
            }
        }
    }

    private void onPlayerServerTick(MinecraftServer minecraftServer) {
        List<ServerPlayerEntity> players = minecraftServer.getPlayerManager().getPlayerList();
        if (players.isEmpty()) return;

        for(ServerPlayerEntity player : players) {
            // handle instinct tick
            InstinctTicker.tick(player);
            // handle transform manager update
            TransformManager.update(player);
            TickManager.tickServerAll();

            // CustomEdiblePower Tick
            CustomEdiblePower.OnServerTick(player);

            /* 重构后不需要了 仅用于参考旧实现逻辑
            // handle transformative effects tick
            PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
            if (attachment != null && attachment.currentEffect != null) {
                //LOGGER.info("Effect tick");
                attachment.remainingTicks--;
                if (attachment.remainingTicks <= 0) {
                    // 取消效果
                    cancelEffect(player);
                    // 触发自定义成就
                    ShapeShifterCurseFabric.ON_TRANSFORM_EFFECT_FADE.trigger(player);
                }
            }

            // save every 5 sec
            save_timer += 1;
            if(save_timer >= 100) {
                //LOGGER.info("Player paused, save attachment");
                // 重新给与玩家视觉效果，以防其被奶桶等消除
                if(attachment != null && attachment.currentToForm != null){
                    if(!player.hasStatusEffect(attachment.currentRegEffect)){
                        loadEffect(player, attachment);
                    }
                }
                saveCurrentAttachment(minecraftServer.getOverworld(), player);
                saveForm(player);
                save_timer = 0;
            }
             */
        }
    }

    // 用于实现一些日志功能 仅用于特定日志 禁止用于其他用途 防止造成开发环境混乱
    public static boolean IsDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
