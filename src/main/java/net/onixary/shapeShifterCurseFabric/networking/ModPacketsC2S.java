package net.onixary.shapeShifterCurseFabric.networking;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnJumpPower;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnSprintingToSneakingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.IPlayerAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.Nullable;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.*;

// 应仅在服务器端注册
// This class should only be registered on the server side
public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                ModPackets.VALIDATE_START_BOOK_BUTTON,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onPressStartBookButton);
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_skin_setting"),
                (server, player, handler, buf, responseSender) -> {
                    boolean keepOriginalSkin = buf.readBoolean();
                    server.execute(() -> {
                        PlayerSkinComponent skinComp = RegPlayerSkinComponent.SKIN_SETTINGS.get(player);
                        skinComp.setKeepOriginalSkin(keepOriginalSkin);
                        // 同步到所有客户端，包括发送者自己
                        RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(JUMP_DETACH_REQUEST_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                        .stream()
                        .filter(BatBlockAttachPower::isAttached)
                        .findFirst()
                        .orElse(null);

                if (attachPower != null) {
                    attachPower.handleJump(player);
                }
            });
        });

        // jump_event condition handle
        ServerPlayNetworking.registerGlobalReceiver(JUMP_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端设置跳跃状态
                if (player.getUuid().equals(playerUuid)) {
                    JumpEventCondition.setJumping(player, true);
                }

                PowerHolderComponent.getPowers(player, ActionOnJumpPower.class)
                        .forEach(ActionOnJumpPower::executeAction);
            });
        });

        // SPRINTING_TO_SNEAKING_EVENT condition handle
        ServerPlayNetworking.registerGlobalReceiver(SPRINTING_TO_SNEAKING_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端处理疾跑转潜行事件
                if (player.getUuid().equals(playerUuid)) {
                    PowerHolderComponent.getPowers(player, ActionOnSprintingToSneakingPower.class)
                            .forEach(ActionOnSprintingToSneakingPower::executeAction);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(
                UPDATE_CUSTOM_SETTING,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onUpdatePlayerCustomConfig
        );

        ServerPlayNetworking.registerGlobalReceiver(
                SET_PATRON_FORM,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::receiveSetPatronForm
        );

        ServerPlayNetworking.registerGlobalReceiver(
                SET_FORM,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::receiveSetForm
        );

        ServerPlayNetworking.registerGlobalReceiver(
                UPDATE_POWER_ANIM_DATA_TO_SERVER,
                ModPacketsC2S::onUpdatePowerAnimationData
        );

        ServerPlayNetworking.registerGlobalReceiver(
                REQUEST_POWER_ANIM_DATA,
                ModPacketsC2S::onRequestPowerAnimationData
        );
    }

    private static void onPressStartBookButton(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        // 就凭这个网络Bug 我就可以做一个可以直接还原形态的作弊客户端 还可以给其他玩家还原 不知道为什么要往buf里写uuid
        // UUID playerUuid = packetByteBuf.readUuid();
        minecraftServer.execute(() -> {
            // 通过 UUID 获取玩家实例
            // ServerPlayerEntity targetPlayer = minecraftServer.getPlayerManager().getPlayer(playerUuid);
            ServerPlayerEntity targetPlayer = playerEntity;
            if (targetPlayer != null && RegPlayerForms.ORIGINAL_BEFORE_ENABLE.equals(RegPlayerFormComponent.PLAYER_FORM.get(targetPlayer).getCurrentForm())) {
                TransformManager.handleDirectTransform(targetPlayer, RegPlayerForms.ORIGINAL_SHIFTER, false);
                // 触发自定义成就
                ShapeShifterCurseFabric.ON_ENABLE_MOD.trigger(targetPlayer);
                // info
                targetPlayer.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod").formatted(Formatting.LIGHT_PURPLE));
            }
        });
    }

    public static void sendDetachRequest(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        // 不需要额外数据，只是一个解除吸附的信号

        ServerPlayNetworking.send(player, JUMP_DETACH_REQUEST_ID, buf);
    }

    private static void onUpdatePlayerCustomConfig(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        boolean keepOriginalSkin = packetByteBuf.readBoolean();
        boolean enableFormColor = packetByteBuf.readBoolean();
        int primaryColor = packetByteBuf.readInt();
        int accentColor1Color = packetByteBuf.readInt();
        int accentColor2Color = packetByteBuf.readInt();
        int eyeColorA = packetByteBuf.readInt();
        int eyeColorB = packetByteBuf.readInt();
        boolean primaryGreyReverse = packetByteBuf.readBoolean();
        boolean accent1GreyReverse = packetByteBuf.readBoolean();
        boolean accent2GreyReverse = packetByteBuf.readBoolean();
        boolean enableFormRandomSound = packetByteBuf.readBoolean();
        minecraftServer.execute(() -> {
            try {
                PlayerSkinComponent component = RegPlayerSkinComponent.SKIN_SETTINGS.get(playerEntity);
                component.setKeepOriginalSkin(keepOriginalSkin);
                component.setEnableFormColor(enableFormColor);
                component.setFormColor(new FormTextureUtils.ColorSetting(primaryColor, accentColor1Color, accentColor2Color, eyeColorA, eyeColorB, primaryGreyReverse, accent1GreyReverse, accent2GreyReverse));
                component.setEnableFormRandomSound(enableFormRandomSound);
                RegPlayerSkinComponent.SKIN_SETTINGS.sync(playerEntity);
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Error while updating player custom config", e);
            }
        });
    }

    private static void onUpdatePowerAnimationData(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        @Nullable Identifier animationId;
        if (packetByteBuf.readBoolean()) {
            animationId = packetByteBuf.readIdentifier();
        } else {
            animationId = null;
        }
        int animationCount = packetByteBuf.readInt();
        int animationLength = packetByteBuf.readInt();
        minecraftServer.execute(() -> {
            if (playerEntity instanceof IPlayerAnimController animPlayer) {
                if (animationId == null) {
                    animPlayer.shape_shifter_curse$stopAnimation();
                }
                else {
                    if (animationCount >= 0 && animationLength < 0) {  // >=0 / -1
                        animPlayer.shape_shifter_curse$playAnimationWithCount(animationId, animationCount);
                    } else if (animationCount < 0 && animationLength >= 0)  {  // -1 / >=0
                        animPlayer.shape_shifter_curse$playAnimationWithTime(animationId, animationLength);
                    } else if (animationCount < 0 && animationLength < 0) {  // -1 / -1
                        animPlayer.shape_shifter_curse$playAnimationLoop(animationId);
                    } else {
                        ShapeShifterCurseFabric.LOGGER.error("Invalid animation data received from player: " + playerEntity.getUuidAsString());
                    }
                }
            }
        });
    }

    private static void onRequestPowerAnimationData(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        UUID targetPlayerUuid = packetByteBuf.readUuid();
        PlayerEntity targetPlayer = minecraftServer.getPlayerManager().getPlayer(targetPlayerUuid);
        minecraftServer.execute(() -> {
            if (targetPlayer instanceof IPlayerAnimController animPlayer) {
                ModPacketsS2CServer.sendPowerAnimationDataToClient(playerEntity, targetPlayerUuid, animPlayer.shape_shifter_curse$getPowerAnimationID(), animPlayer.shape_shifter_curse$getPowerAnimationCount(), animPlayer.shape_shifter_curse$getPowerAnimationTime());
            }
        });
    }

    private static void receiveSetForm(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        Identifier formId = packetByteBuf.readIdentifier();
        PlayerFormBase form = RegPlayerForms.getPlayerForm(formId);
        // 网络包可以伪造 所以加个权限验证
        if (minecraftServer.getCommandSource().hasPermissionLevel(2) || playerEntity.getAbilities().creativeMode) {
            minecraftServer.execute(() -> {
                if (playerEntity == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetForm] Player is null");
                    return;
                }
                TransformManager.handleDirectTransform(playerEntity, form, false);
            });
            return;
        }
    }

    private static void receiveSetPatronForm(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        if (!PatronUtils.EnablePatronFeature) {
            ShapeShifterCurseFabric.LOGGER.error("Player {} tried to use patron form but patron feature is disabled", playerEntity.getDisplayName().getString());
            return;
        }
        Identifier formId = packetByteBuf.readIdentifier();
        PlayerFormBase form = RegPlayerForms.getPlayerForm(formId);

        if (minecraftServer.getCommandSource().hasPermissionLevel(2) || playerEntity.getAbilities().creativeMode) {
            // 权限等级2时跳过反作弊 毕竟可以用setForm了
            minecraftServer.execute(() -> {
                if (playerEntity == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetPatronForm] Player is null");
                    return;
                }
                TransformManager.handleDirectTransform(playerEntity, form, false);
            });
            return;
        }
        if (form instanceof PlayerFormDynamic pfd) {
            minecraftServer.execute(() -> {
                if (playerEntity == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetPatronForm] Player is null");
                    return;
                }
                if (pfd.IsPlayerCanUse(playerEntity)) {
                    TransformManager.handleDirectTransform(playerEntity, pfd, false);
                }
                else {
                    // 一般情况下，这里不会执行，因为客户端在发送请求前已经进行了检查 如果触发了这里，说明客户端和服务器之间的数据不同步(小概率 如果不同步早就掉线了) 或者是客户端作弊(大概率)
                    ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but they are not allowed", playerEntity.getDisplayName().getString(), formId.toString());
                }
            });
        }
        else if (form != null){
            // 如果是已发布版本 100% 是客户端作弊 一般只会在测试时触发(因为测试版需要填充所有表单用来测试UI)
            ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but it is not a dynamic form", playerEntity.getDisplayName().getString(), formId.toString());
        }
        else {
            // 可能是不同步问题
            ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but it does not exist", playerEntity.getDisplayName().getString(), formId.toString());
        }
        return;
    }
}
