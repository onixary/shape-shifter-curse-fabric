package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.PlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.RegPlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import net.onixary.shapeShifterCurseFabric.team.MobTeamManager;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.cursedMoonData;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.savePlayerInstinctComponent;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker.loadInstinct;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeVisualEffects;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;

public class PlayerEventHandler {
    public static void register() {
        // join event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.getWorld().isClient()) return;

            // 初始化 PlayerDataStorage（只在第一次有服务器实例时执行）
            PlayerDataStorage.initialize(server);

            ServerPlayerEntity player = handler.player;

            // load form first
            FormAbilityManager.getServerWorld(server.getOverworld());

            // check if first join with mod using PlayerFormComponent
            //PlayerFormComponent formComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);

            PlayerSkinComponent skinComponent = RegPlayerSkinComponent.SKIN_SETTINGS.get(player);
            RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);

            // 检查是否存在保存的数据来判断是否首次加入
            PlayerFormComponent savedComponent = PlayerNbtStorage.loadPlayerFormComponent(server.getOverworld(), player.getUuid().toString());

            if (savedComponent != null) {
                // 如果有保存的数据，说明不是首次加入，使用保存的数据
                //formComponent.readFromNbt(savedComponent.writeToNbt(new net.minecraft.nbt.NbtCompound()));
                RegPlayerFormComponent.PLAYER_FORM.sync(player);
                ShapeShifterCurseFabric.LOGGER.info("Loaded existing player data, not first join");
            } else {
                // 如果没有保存的数据，说明是首次加入
                ShapeShifterCurseFabric.LOGGER.info("No saved data found, this is first join with mod");
                PlayerFormComponent formComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);
                // 确保 firstJoin 为 true
                formComponent.setFirstJoin(true);
                // 触发首次加入成就
                ShapeShifterCurseFabric.ON_FIRST_JOIN_WITH_MOD.trigger(player);
                // 设置为 false 并保存
                formComponent.setFirstJoin(false);
                RegPlayerFormComponent.PLAYER_FORM.sync(player);
                // 立即保存以防止重复触发
                PlayerNbtStorage.savePlayerFormComponent(server.getOverworld(), player.getUuid().toString(), formComponent);
            }
            FormAbilityManager.loadForm(player);

            // load attachment
            boolean hasAttachment = loadCurrentAttachment(server.getOverworld(), player);
            if(!hasAttachment) {
                resetAttachment(player);
            }
            else{
                ShapeShifterCurseFabric.LOGGER.info("Attachment loaded ");
            }
            ModPacketsS2C.sendSyncEffectAttachment(player, player.getAttached(EffectManager.EFFECT_ATTACHMENT));

            // load instinct
            InstinctManager.getServerWorld(server.getOverworld());
            loadInstinct(player);

            // load cursed moon data
            ShapeShifterCurseFabric.LOGGER.info("Cursed moon enabled");
            cursedMoonData.getInstance().load(server.getOverworld());
            cursedMoonData.getInstance().enableCursedMoon(server.getOverworld());
            boolean currentIsCursedMoon = cursedMoonData.getInstance().isTonightCursedMoon;
            boolean currentIsNight = CursedMoon.isNight();

            // 立即同步当前状态给玩家
            ModPacketsS2C.sendCursedMoonData(player, CursedMoon.day_time, CursedMoon.day,
                    currentIsCursedMoon, currentIsNight);

            ShapeShifterCurseFabric.LOGGER.info("向玩家同步诅咒之月状态: " + currentIsCursedMoon);
            cursedMoonData.getInstance().loadPlayerStates(server.getOverworld(), player);
            cursedMoonData.getInstance().save(server.getOverworld());

            // reset moon effect
            CursedMoon.resetMoonEffect(player);

            // Set doDaylightCycle to true forced
            server.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);

            // update team
            //PlayerTeamHandler.updatePlayerTeam(player);
        });
        // copy event
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            // 仅在服务端执行
            if (newPlayer.getWorld().isClient()) return;

            copyTransformativeEffect(oldPlayer, newPlayer);
            copyFormAndAbility(oldPlayer, newPlayer);
            PlayerNbtStorage.saveAll(newPlayer.getServerWorld(), newPlayer);
            //PlayerTeamHandler.updatePlayerTeam(newPlayer);
        });

        //load event
        ServerWorldEvents.LOAD.register((server, world) -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                FormAbilityManager.loadForm(player);

                // load attachment
                boolean hasAttachment = loadCurrentAttachment(server.getOverworld(), player);
                if(!hasAttachment) {
                    resetAttachment(player);
                }
                else{
                    ShapeShifterCurseFabric.LOGGER.info("Attachment loaded ");
                }
                ModPacketsS2C.sendSyncEffectAttachment(player, player.getAttached(EffectManager.EFFECT_ATTACHMENT));

                // load instinct
                InstinctManager.getServerWorld(server.getOverworld());
                loadInstinct(player);

                // load cursed moon data
                ShapeShifterCurseFabric.LOGGER.info("Cursed moon enabled");
                cursedMoonData.getInstance().load(server.getOverworld());
                cursedMoonData.getInstance().enableCursedMoon(server.getOverworld());
                boolean currentIsCursedMoon = cursedMoonData.getInstance().isTonightCursedMoon;
                boolean currentIsNight = CursedMoon.isNight();

                // 立即同步当前状态给玩家
                ModPacketsS2C.sendCursedMoonData(player, CursedMoon.day_time, CursedMoon.day,
                        currentIsCursedMoon, currentIsNight);

                ShapeShifterCurseFabric.LOGGER.info("向玩家同步诅咒之月状态: " + currentIsCursedMoon);
                cursedMoonData.getInstance().loadPlayerStates(server.getOverworld(), player);
                cursedMoonData.getInstance().save(server.getOverworld());

                // reset moon effect
                CursedMoon.resetMoonEffect(player);

                // Set doDaylightCycle to true forced
                server.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);
                MobTeamManager.registerTeam(world);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                if (world.getRegistryKey() == World.OVERWORLD) {
                    ShapeShifterCurseFabric.LOGGER.info("Cursed moon data saved by server stop");
                    cursedMoonData.getInstance().save(world);
                    // 保存所有玩家状态
                    for (ServerPlayerEntity player : world.getPlayers()) {
                        cursedMoonData.getInstance().savePlayerStates(world, player);
                        PlayerNbtStorage.saveAll(world, player);
                    }
                }
            }
        });
    }

    private static void copyTransformativeEffect(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        // transformative effect attachment
        PlayerEffectAttachment oldAttachment = oldPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);
        newPlayer.setAttached(EffectManager.EFFECT_ATTACHMENT, new PlayerEffectAttachment());
        PlayerEffectAttachment newAttachment = newPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);

        newAttachment.currentToForm = oldAttachment.currentToForm;
        newAttachment.remainingTicks = oldAttachment.remainingTicks;
        newAttachment.currentEffect = oldAttachment.currentEffect;

        // reapply potion effect
        if (newAttachment.currentEffect != null && currentRegEffect != null) {
            ShapeShifterCurseFabric.LOGGER.info("re-apply potion effect here");
            removeVisualEffects(newPlayer);
            newPlayer.addStatusEffect(new StatusEffectInstance(
                    currentRegEffect,
                    newAttachment.remainingTicks
            ));
        }
    }

    private static void copyFormAndAbility(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        PlayerFormComponent oldComponent = RegPlayerFormComponent.PLAYER_FORM.get(oldPlayer);
        PlayerFormComponent newComponent = RegPlayerFormComponent.PLAYER_FORM.get(newPlayer);
        newComponent.setCurrentForm(oldComponent.getCurrentForm());
        newComponent.setCurrentForm(oldComponent.getCurrentForm());
        FormAbilityManager.applyForm(newPlayer, newComponent.getCurrentForm());
    }

    private static void handleEntityTeam(ServerWorld world){
        Scoreboard scoreboard = world.getScoreboard();
        Team sorceryTeam = scoreboard.getTeam(MobTeamManager.SORCERY_TEAM_NAME);
        for (Entity entity : world.iterateEntities()) {
            // Sorcery Team
            if (entity.getType() == EntityType.EVOKER
            || entity.getType() == EntityType.WITCH
            || entity.getType() == EntityType.VINDICATOR
            || entity.getType() == EntityType.PILLAGER
            || entity.getType() == EntityType.RAVAGER)
            {
                if (sorceryTeam != null) {
                    scoreboard.addPlayerToTeam(entity.getEntityName(), sorceryTeam);
                }
            }
        }
    }
}
