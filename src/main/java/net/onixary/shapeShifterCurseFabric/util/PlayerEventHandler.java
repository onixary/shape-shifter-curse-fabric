package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;
import net.onixary.shapeShifterCurseFabric.team.MobTeamManager;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.cursedMoonData;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker.loadInstinct;

public class PlayerEventHandler {
    public static void register() {
        // join event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.getWorld().isClient()) return;

            // 初始化 PlayerDataStorage（只在第一次有服务器实例时执行）
            PlayerDataStorage.initialize(server);

            ServerPlayerEntity player = handler.player;

            // load form first
            FormAbilityManager.getServerWorld(player.getServerWorld());

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
                // 还原到默认值 根据Wiki描述 如果删除data/shape-shifter-curse/{uuid}_*.dat则玩家会回到启用Mod之前的状态
                formComponent.clear();
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
            // 同步动态Form
            server.execute(() -> {
                try {
                    ModPacketsS2CServer.updateDynamicForm(player);
                } catch (Exception e) {
                    ShapeShifterCurseFabric.LOGGER.error("Error sending update dynamic form: ", e);
                }
            });
            server.execute(() -> {
                try {
                    FormAbilityManager.loadForm(player);
                } catch (Exception e) {
                    ShapeShifterCurseFabric.LOGGER.error("Error loading player form: ", e);
                }
            });

            /* 重构后不需要了 仅用于参考旧实现逻辑
            // load attachment
            boolean hasAttachment = loadCurrentAttachment(server.getOverworld(), player);
            if(!hasAttachment) {
                resetAttachment(player);
            }
            else{
                ShapeShifterCurseFabric.LOGGER.info("Attachment loaded ");
            }
            ModPacketsS2CServer.sendSyncEffectAttachment(player, EffectManager.getOrCreateAttachment(player));
             */
            // 将 StatusEffectInstance 转换为 TransformativeStatusInstance
            EffectManager.ReloadPlayerEffect(player);

            // load instinct
            InstinctManager.getServerWorld(server.getOverworld());
            loadInstinct(player);

            // load cursed moon data
            ShapeShifterCurseFabric.LOGGER.info("Cursed moon enabled");
            cursedMoonData.getInstance().load(server.getOverworld());
            cursedMoonData.getInstance().enableCursedMoon(server.getOverworld());
            // 修改为使用新的月相判定系统
            ServerWorld world = server.getOverworld();
            boolean currentIsCursedMoon = CursedMoon.isCursedMoon(world); // 使用新的月相判定
            boolean currentIsNight = CursedMoon.isNight(world);

            // 立即同步当前状态给玩家
            ModPacketsS2CServer.sendCursedMoonData(player, world.getTimeOfDay(), CursedMoon.getDay(world),
                    currentIsCursedMoon, currentIsNight);

            ShapeShifterCurseFabric.LOGGER.info("向玩家同步诅咒之月状态: " + currentIsCursedMoon + ", 月相: " + world.getMoonPhase());
            // 添加延迟同步，确保客户端完全加载后再次发送状态
            server.execute(() -> {
                // 延迟40个tick（2秒）再次同步
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // 在主线程中执行同步
                    server.execute(() -> {
                        if (player.networkHandler != null && !player.isDisconnected()) {
                            ServerWorld currentWorld = player.getServerWorld();
                            boolean delayedIsCursedMoon = CursedMoon.isCursedMoonByPhase(currentWorld); // 直接使用月相判定
                            boolean delayedIsNight = CursedMoon.isNight(currentWorld);

                            ModPacketsS2CServer.sendCursedMoonData(player, currentWorld.getTimeOfDay(), CursedMoon.getDay(currentWorld),
                                    delayedIsCursedMoon, delayedIsNight);

                            ShapeShifterCurseFabric.LOGGER.info("延迟同步诅咒之月状态: " + delayedIsCursedMoon +
                                    ", 月相: " + currentWorld.getMoonPhase() +
                                    ", 玩家: " + player.getName().getString());
                        }
                    });
                }).start();
            });


            cursedMoonData.getInstance().loadPlayerStates(server.getOverworld(), player);
            cursedMoonData.getInstance().save(server.getOverworld());

            // reset moon effect
            CursedMoon.resetMoonEffect(player);

            // Set doDaylightCycle to true forced
            //server.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);

            // update team
            //PlayerTeamHandler.updatePlayerTeam(player);

            // 清空玩家召唤冷却
            MinionRegister.ResetPlayerCoolDown(player);

            // 清空玩家召唤物
            MinionRegister.DisSpawnAllMinion(player);
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

                /* 重构后不需要了 仅用于参考旧实现逻辑
                // load attachment
                boolean hasAttachment = loadCurrentAttachment(server.getOverworld(), player);
                if(!hasAttachment) {
                    resetAttachment(player);
                }
                else{
                    ShapeShifterCurseFabric.LOGGER.info("Attachment loaded ");
                }
                ModPacketsS2CServer.sendSyncEffectAttachment(player, EffectManager.getOrCreateAttachment(player));
                 */
                // 将 StatusEffectInstance 转换为 TransformativeStatusInstance
                EffectManager.ReloadPlayerEffect(player);

                // load instinct
                InstinctManager.getServerWorld(server.getOverworld());
                loadInstinct(player);

                // load cursed moon data
                ShapeShifterCurseFabric.LOGGER.info("Cursed moon enabled");
                cursedMoonData.getInstance().load(server.getOverworld());
                cursedMoonData.getInstance().enableCursedMoon(server.getOverworld());

                // 修改为使用新的月相判定系统
                boolean currentIsCursedMoon = CursedMoon.isCursedMoon(world); // 使用新的月相判定
                boolean currentIsNight = CursedMoon.isNight(world);

                // 立即同步当前状态给玩家
                ModPacketsS2CServer.sendCursedMoonData(player, world.getTimeOfDay(), CursedMoon.getDay(world),
                        currentIsCursedMoon, currentIsNight);

                ShapeShifterCurseFabric.LOGGER.info("向玩家同步诅咒之月状态: " + currentIsCursedMoon + ", 月相: " + world.getMoonPhase());
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

        ServerTickEvents.END_SERVER_TICK.register(server -> JumpEventCondition.tick());
    }

    private static void copyTransformativeEffect(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        /* 重构后不需要了 仅用于参考旧实现逻辑
        // transformative effect attachment
        PlayerEffectAttachment oldAttachment = oldPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);
        newPlayer.setAttached(EffectManager.EFFECT_ATTACHMENT, new PlayerEffectAttachment());
        PlayerEffectAttachment newAttachment = newPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);

        if (oldAttachment != null)  {
            newAttachment.currentToForm = oldAttachment.currentToForm;
            newAttachment.remainingTicks = oldAttachment.remainingTicks;
            newAttachment.currentEffect = oldAttachment.currentEffect;
        }

        // reapply potion effect
        // 从 oldAttachment 中获取要重新施加的效果
        StatusEffect effectToApply = oldAttachment != null ? oldAttachment.currentEffect : null;

        if (effectToApply != null) {
            ShapeShifterCurseFabric.LOGGER.info("re-apply potion effect here: {}", effectToApply.getName());
            // 我不建议在复制效果时先移除，除非有特殊视觉问题
            removeVisualEffects(newPlayer);

            newPlayer.addStatusEffect(new StatusEffectInstance(
                    effectToApply,          // 使用从旧玩家获取的效果
                    newAttachment.remainingTicks
            ));
        }
         */
        TransformativeStatusInstance transformativeStatusInstance = EffectManager.getTransformativeEffect(oldPlayer);
        if (transformativeStatusInstance == null) {
            return;
        }
        EffectManager.clearTransformativeEffect(newPlayer);
        newPlayer.addStatusEffect(transformativeStatusInstance);
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
