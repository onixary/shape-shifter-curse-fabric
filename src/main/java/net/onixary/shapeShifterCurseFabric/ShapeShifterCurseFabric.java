package net.onixary.shapeShifterCurseFabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalEntityActions;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalEntityConditions;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import net.onixary.shapeShifterCurseFabric.advancement.*;
import net.onixary.shapeShifterCurseFabric.command.FormArgumentType;
import net.onixary.shapeShifterCurseFabric.command.ShapeShifterCurseCommand;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.CursedMoonData;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegEntitySpawnEgg;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.TEntitySpawnHandler;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.item.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.item.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_animation.RegPlayerAnimation;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctDebugHUD;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.util.PlayerEventHandler;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import net.onixary.shapeShifterCurseFabric.util.TickManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager.saveForm;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.saveInstinctComp;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;


public class ShapeShifterCurseFabric implements ModInitializer {

    public static final String MOD_ID = "shape-shifter-curse";
    // 使用AuthMe模组获取真实的持久UUID来记录保存，因此不需要使用之前很蠢的测试UUID了
    // public static final String DEBUG_UUID = "testUUID-3d9ab571-1ea5-360b-bc9d-77cd0b2f72a9";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigSSC CONFIG = ConfigSSC.createAndLoad();
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
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TransformativeBatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");
    // Axolotl
    public static final EntityType<TransformativeAxolotlEntity> T_AXOLOTL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_axolotl"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TransformativeAxolotlEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    // Ocelot
    public static final EntityType<TransformativeOcelotEntity> T_OCELOT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "t_ocelot"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TransformativeOcelotEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    private int save_timer = 0;


    @Override
    public void onInitialize() {
        PlayerDataStorage.initialize();
        RegCustomItem.initialize();
        RegEntitySpawnEgg.initialize();
        RegTStatusEffect.initialize();
        RegTStatusPotionEffect.initialize();
        PlayerEventHandler.register();
        TEntitySpawnHandler.register();
        RegFormConfig.register();
        RegPlayerAnimation.register();
        RegOtherStatusEffects.initialize();
        AdditionalEntityConditions.register();
        AdditionalPowers.register();
        AdditionalEntityActions.register();
        // network package
        ModPacketsC2S.register();
        ModPacketsS2C.register();
        cursedMoonData = new CursedMoonData();

        //TransformFX.INSTANCE.registerCallbacks();
        TransformOverlay.INSTANCE.init();
        save_timer = 0;

        // Reg potions
        RegCustomPotions.registerPotions();
        RegCustomPotions.registerPotionsRecipes();
        // Reg origins content

        // Reg custom entities model and renderer
        /*FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createTBatAttributes());
        EntityRendererRegistry.register(T_BAT, (context) -> {
            return new BatEntityRenderer(context);
        });*/
        // load and save attached
        // use PlayerEventHandler

        /*ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerEntity player = handler.player;
            boolean hasAttachment = loadCurrentAttachment(player);
            if(!hasAttachment) {
                resetAttachment(player);
            }
            else{
                LOGGER.info("Attachment loaded ");
            }
        });*/

        // do not reset effect when player respawn or enter hell

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ShapeShifterCurseCommand.register(dispatcher);
        });
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of(MOD_ID, "form_argument_type"),
                FormArgumentType.class,
                ConstantArgumentSerializer.of(FormArgumentType::new)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity player = handler.player;
            LOGGER.info("Player disconnect, save attachment");
            saveCurrentAttachment(server.getOverworld(), player);
            saveForm(player);
            saveInstinctComp(player);
            // save cursed moon data
            ShapeShifterCurseFabric.cursedMoonData.getInstance().save(server.getOverworld());
        });

        // Reg listeners
        ServerTickEvents.END_SERVER_TICK.register(this::onPlayerServerTick);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        EntitySleepEvents.STOP_SLEEPING.register((entity, world) -> {
            if (entity instanceof PlayerEntity) {
                onPlayerEndSleeping(entity, world);
            }
        });
        // allow sleep when status effect is active
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((entity, world, pos) -> {
            if (entity instanceof PlayerEntity) {
                if(RegTStatusEffect.hasAnyEffect((PlayerEntity) entity)) {
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

    private void onPlayerEndSleeping(LivingEntity entity, BlockPos world) {
        if (entity instanceof ServerPlayerEntity) {
            // handle transformative effects
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            //LOGGER.info(EffectManager.EFFECT_ATTACHMENT.toString());
            //PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
            //LOGGER.info(attachment == null? "attachment is null" : attachment.currentEffect.toString());
            // 不用检测诅咒之月状态--作为一个特性还挺有意思的
            if(/*!(CursedMoon.isCursedMoon() && CursedMoon.isNight())*/true){
                if (RegTStatusEffect.hasAnyEffect(player)) {
                    EffectManager.applyEffect(player);
                    // 触发自定义成就
                    ON_SLEEP_WHEN_HAVE_TRANSFORM_EFFECT.trigger((ServerPlayerEntity) player);
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_sleep_when_attached").formatted(Formatting.LIGHT_PURPLE));
                }
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
            TransformManager.update();
            TickManager.tickServerAll();

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
        }
    }

    private void onClientTick(MinecraftClient minecraftClient){
        TickManager.tickClientAll();
    }
}
