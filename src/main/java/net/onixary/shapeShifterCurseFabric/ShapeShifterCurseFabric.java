package net.onixary.shapeShifterCurseFabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegEntitySpawnEgg;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.TEntitySpawnHandler;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.item.CustomItems;
import net.onixary.shapeShifterCurseFabric.player_animation.RegPlayerAnimation;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctDebugHUD;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.RegPlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.data.PlayerEventHandler;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager.saveForm;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.saveInstinctComp;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;


public class ShapeShifterCurseFabric implements ModInitializer {

    public static final String MOD_ID = "shape-shifter-curse";
    // 用于调试的 UUID，打包中需要将其设为 null
    public static final String DEBUG_UUID = "testUUID-3d9ab571-1ea5-360b-bc9d-77cd0b2f72a9";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigSSC CONFIG = ConfigSSC.createAndLoad();

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

    private int save_timer = 0;


    @Override
    public void onInitialize() {
        PlayerDataStorage.initialize();
        CustomItems.initialize();
        RegEntitySpawnEgg.initialize();
        RegTStatusEffect.initialize();
        PlayerEventHandler.register();
        TEntitySpawnHandler.register();
        RegFormConfig.register();
        RegPlayerAnimation.register();
        save_timer = 0;
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


        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity player = handler.player;
            LOGGER.info("Player disconnect, save attachment");
            saveCurrentAttachment(server.getOverworld(), player);
            saveForm(player);
            saveInstinctComp(player);
        });

        // Reg listeners
        ServerTickEvents.END_SERVER_TICK.register(this::onPlayerServerTick);
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
            }
            return null;
        });

        // Debug instinct
        InstinctDebugHUD.register();

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
            if (RegTStatusEffect.hasAnyEffect(player)) {
                EffectManager.applyEffect(player);
            }
        }
    }

    private void onPlayerServerTick(MinecraftServer minecraftServer) {
        List<ServerPlayerEntity> players = minecraftServer.getPlayerManager().getPlayerList();
        if (players.isEmpty()) return;

        for(ServerPlayerEntity player : players) {
            // handle instinct tick
            InstinctTicker.tick(player);

            // handle transformative effects tick
            PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
            if (attachment != null && attachment.currentEffect != null) {
                //LOGGER.info("Effect tick");
                attachment.remainingTicks--;
                if (attachment.remainingTicks <= 0) {
                    //LOGGER.info("Effect tick over");
                    EffectManager.applyEffect(player);
                }
            }

            // save every 5 sec
            save_timer += 1;
            if(save_timer >= 100) {
                LOGGER.info("Player paused, save attachment");
                saveCurrentAttachment(minecraftServer.getOverworld(), player);
                saveForm(player);
                save_timer = 0;
            }
        }
    }
}
