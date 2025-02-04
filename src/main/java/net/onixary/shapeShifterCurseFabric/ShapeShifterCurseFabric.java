package net.onixary.shapeShifterCurseFabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.profiler.Profiler;
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegEntitySpawnEgg;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.item.CustomItems;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;


public class ShapeShifterCurseFabric implements ModInitializer {

    public static final String MOD_ID = "shape-shifter-curse";

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
        save_timer = 0;
        // Reg custom entities model and renderer
        FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createTBatAttributes());
        EntityRendererRegistry.register(T_BAT, (context) -> {
            return new BatEntityRenderer(context);
        });
        // load and save attached
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerEntity player = handler.player;
            boolean hasAttachment = loadCurrentAttachment(player);
            if(!hasAttachment) {
                resetAttachment(player);
            }
            else{
                LOGGER.info("Attachment loaded ");
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity player = handler.player;
            LOGGER.info("Player disconnect, save attachment");
            saveCurrentAttachment(player);
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

        EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);
        // entity spawn replacer
        /*ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof BatEntity) {
                // 50% 概率替换为自定义蝙蝠
                if (world.getRandom().nextFloat() < 0.5f) {
                    TransformativeBatEntity customBat = new TransformativeBatEntity(
                            RegTransformativeEntity.T_BAT, world
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


        LOGGER.info(CONFIG.keepOriginalSkin() ? "Original skin will be kept." : "Override skin");
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

        // handle transformative effects
        //LOGGER.info("onPlayerTick");
        for(ServerPlayerEntity player : players) {
            PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
            if (attachment != null && attachment.currentEffect != null) {
                //LOGGER.info("Effect tick");
                attachment.remainingTicks--;
                if (attachment.remainingTicks <= 0) {
                    //LOGGER.info("Effect tick over");
                    EffectManager.applyEffect(player);
                }
            }

            // save attachment every 5 sec
            save_timer += 1;
            if(save_timer >= 100) {
                LOGGER.info("Player paused, save attachment");
                saveCurrentAttachment(player);
                save_timer = 0;
            }
        }
    }
}
