package net.onixary.shapeShifterCurseFabric.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.VirtualTotemPower;
import net.onixary.shapeShifterCurseFabric.client.ClientPlayerStateManager;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.custom_ui.FormColorSelectMenu;
import net.onixary.shapeShifterCurseFabric.custom_ui.NormalFormSelectScreen;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.IPlayerAnimController;
import net.onixary.shapeShifterCurseFabric.custom_ui.PatronFormSelectScreen;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.util.FormColorData;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import net.onixary.shapeShifterCurseFabric.util.Interface.IJumpController;
import org.jetbrains.annotations.Nullable;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.*;

// 应仅在客户端注册
// This class should only be registered on the client side
// 纯客户端类，所有的receive方法都只在这里调用
// This is a pure client-side class, all receive methods are called only here
public class ModPacketsS2C {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TRANSFORM_EFFECT_ID, ModPacketsS2C::receiveTransformEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.INSTINCT_THRESHOLD_EFFECT_ID, ModPacketsS2C::receiveInstinctThresholdEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_CURSED_MOON_DATA, ModPacketsS2C::receiveCursedMoonData);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_FORM_CHANGE, ModPacketsS2C::receiveFormChange);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_TRANSFORM_STATE, ModPacketsS2C::receiveTransformState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_BAT_ATTACH_STATE, ModPacketsS2C::receiveBatAttachState);
        // ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_EFFECT_ATTACHMENT, ModPacketsS2C::handleSyncEffectAttachment);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_OVERLAY_EFFECT, ModPacketsS2C::receiveUpdateOverlayEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_OVERLAY_FADE_EFFECT, ModPacketsS2C::receiveUpdateOverlayFadeEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TRANSFORM_COMPLETE_EFFECT, ModPacketsS2C::receiveTransformCompleteEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.RESET_FIRST_PERSON, ModPacketsS2C::receiveResetFirstPerson);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_OTHER_PLAYER_BAT_ATTACH_STATE, ModPacketsS2C::receiveOtherPlayerBatAttachState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_FORCE_SNEAK_STATE, ModPacketsS2C::receiveForceSneakState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_DYNAMIC_FORM, ModPacketsS2C::handleUpdateDynamicForm);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.REMOVE_DYNAMIC_FORM_EXCEPT, ModPacketsS2C::handleRemoveDynamicExcept);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LOGIN_PACKET, ModPacketsS2C::onPlayerConnectServer);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.ACTIVE_VIRTUAL_TOTEM, ModPacketsS2C::receiveActiveVirtualTotem);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_POWER_ANIM_DATA_TO_CLIENT, ModPacketsS2C::receivePowerAnimationData);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_PATRON_LEVEL, ModPacketsS2C::receiveUpdatePatronLevel);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_PATRON_FORM_SELECT_MENU, ModPacketsS2C::receiveOpenPatronFormSelectMenu);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_FORM_SELECT_MENU, ModPacketsS2C::receiveOpenFormSelectMenu);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SET_NO_JUMP_TICK, ModPacketsS2C::receiveSetNoJumpTick);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_FORM_COLOR_SELECT_MENU, ModPacketsS2C::receiveOpenFCSMenu);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.MODIFY_FCD_DATA, ModPacketsS2C::receiveModifyFCDData);
    }

    /* 重构后不需要了 仅用于参考旧实现逻辑
    public static void handleSyncEffectAttachment(
		MinecraftClient client,
		ClientPlayNetworkHandler handler,
		PacketByteBuf buf,
		PacketSender sender
	) {
        // 从数据包读取NBT
        NbtCompound nbt = buf.readNbt();
        client.execute(() -> {
            // 更新客户端缓存
            ClientEffectAttachmentCache.update(nbt);
        });
    }
     */

    public static void receiveTransformEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                              PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            // 当客户端收到这个数据包时，调用TransformManager中的新方法来播放特效
            TransformManager.playClientTransformEffect();
        });
    }

    public static void receiveInstinctThresholdEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                      PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            // 当客户端收到这个数据包时，调用TransformManager中的新方法来播放特效
            ShapeShifterCurseFabricClient.applyInstinctThresholdEffect();
        });
    }

    public static void receiveCursedMoonData(MinecraftClient client, ClientPlayNetworkHandler handler,
                                           PacketByteBuf buf, PacketSender responseSender) {
        long dayTime = buf.readLong();
        int day = buf.readInt();
        boolean isCursedMoon = buf.readBoolean();
        boolean isNight = buf.readBoolean();

        client.execute(() -> {
            // 更新客户端的CursedMoon状态
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.day_time = dayTime;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.day = day;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.clientIsCursedMoon = isCursedMoon;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.clientIsNight = isNight;
        });
    }

    // 接收形态变化同步包
    public static void receiveFormChange(MinecraftClient client, ClientPlayNetworkHandler handler,
                                       PacketByteBuf buf, PacketSender responseSender) {
        String newFormName = buf.readString();

        client.execute(() -> {
            // 强制客户端重新注册动画（如果需要）
            if (client.player != null) {
                ShapeShifterCurseFabric.LOGGER.info("Client received form change: " + newFormName);
                // 触发动画重新初始化
                net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient.refreshPlayerAnimations();

                // 更新 formColorData 的数据(其实是FormColorSelectMenu的数据) 如果启动了自动切换 那么还会自动切换颜色数据
                ShapeShifterCurseFabricClient.formColorData.onClientFormChange(Identifier.tryParse(newFormName));
            }
        });
    }

    // 接收变身状态同步包
    public static void receiveTransformState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                           PacketByteBuf buf, PacketSender responseSender) {
        UUID playerUuid = buf.readUuid();
        boolean isTransforming = buf.readBoolean();
        String fromForm = buf.readString();
        String toForm = buf.readString();

        client.execute(() -> {
            if (client.player != null) {
                ShapeShifterCurseFabric.LOGGER.info("Client received transform state: isTransforming=" + isTransforming);
                // 更新客户端的变身状态
                net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient.updateTransformState(
                        playerUuid, isTransforming, fromForm.isEmpty() ? null : fromForm, toForm.isEmpty() ? null : toForm);
            }
        });
    }

    // 接收Overlay效果更新包
    public static void receiveUpdateOverlayEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                 PacketByteBuf buf, PacketSender responseSender) {
        float nauseaStrength = buf.readFloat();
        int ticks = buf.readInt();

        client.execute(() -> {
            TransformManager.handleClientOverlayUpdate(nauseaStrength, ticks);
        });
    }

    // 接收Overlay淡出效果更新包
    public static void receiveUpdateOverlayFadeEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                     PacketByteBuf buf, PacketSender responseSender) {
        float nauseaStrength = buf.readFloat();
        int ticks = buf.readInt();

        client.execute(() -> {
            TransformManager.handleClientOverlayFadeUpdate(nauseaStrength, ticks);
        });
    }

    // 接收变身完成效果包
    public static void receiveTransformCompleteEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                     PacketByteBuf buf, PacketSender responseSender) {
        client.execute(TransformManager::executeClientTransformCompleteEffect);
    }

    // 接收FirstPerson重置包
    public static void receiveResetFirstPerson(MinecraftClient client, ClientPlayNetworkHandler handler,
                                             PacketByteBuf buf, PacketSender responseSender) {
        client.execute(TransformManager::executeClientFirstPersonReset);
    }

    // 接收蝙蝠吸附状态同步包
    public static void receiveBatAttachState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                             PacketByteBuf buf, PacketSender responseSender) {
        boolean isAttached = buf.readBoolean();
        int attachTypeOrdinal = buf.readInt();

        BlockPos attachedPos;
        if (buf.readBoolean()) {
            attachedPos = buf.readBlockPos();
        } else {
            attachedPos = null;
        }

        Direction attachedSide;
        if (buf.readBoolean()) {
            attachedSide = Direction.byId(buf.readInt());
        } else {
            attachedSide = null;
        }

        client.execute(() -> {
            if (client.player != null) {
                // 获取客户端的BatBlockAttachPower并同步状态
                BatBlockAttachPower.syncClientState(client.player, isAttached, attachTypeOrdinal, attachedPos, attachedSide);
            }
        });
    }

    // 接收其他玩家的蝙蝠吸附状态同步包
    public static void receiveOtherPlayerBatAttachState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                        PacketByteBuf buf, PacketSender responseSender) {
        UUID targetPlayerUuid = buf.readUuid();
        boolean isAttached = buf.readBoolean();
        int attachType = buf.readInt();

        BlockPos attachedPos;
        Direction attachedSide;

        if (buf.readBoolean()) {
            attachedPos = buf.readBlockPos();
        } else {
            attachedPos = null;
        }

        if (buf.readBoolean()) {
            attachedSide = Direction.byId(buf.readInt());
        } else {
            attachedSide = null;
        }

        client.execute(() -> {
            ClientPlayerStateManager.updatePlayerAttachState(targetPlayerUuid, isAttached,
                    attachType, attachedPos, attachedSide);
        });
    }

    private static void receiveForceSneakState(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean shouldForce = buf.readBoolean();
        client.execute(() -> {
            ClientPlayerStateManager.shouldForceSneak = shouldForce;
        });
    }

    private static void handleUpdateDynamicForm(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // 读取String -> JsonObject
        JsonObject allFrom = new JsonObject();
        int formCount = buf.readInt();
        for (int i = 0; i < formCount; i++) {
            String formName = buf.readString();
            String jsonStr = buf.readString();
            JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
            allFrom.add(formName, jsonObject);
        }
        client.execute(() -> {
            RegPlayerForms.ApplyDynamicPlayerForms(allFrom);
        });
    }

    private static void handleRemoveDynamicExcept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // 读取String -> JsonObject
        List<Identifier> except = new ArrayList<>();
        int formCount = buf.readInt();
        for (int i = 0; i < formCount; i++) {
            String formName = buf.readString();
            except.add(Identifier.tryParse(formName));
        }
        client.execute(() -> {
            RegPlayerForms.removeDynamicPlayerFormsExcept(except);
        });
    }

    public static void onPlayerConnectServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // 还原FPM设置 或许可以通过注入式修改配置来减少此类Bug 比如在FPM读取offset时修改返回值
        TransformManager.executeClientFirstPersonReset();
        new Thread(() -> {
            // 延时5s, 等待服务器component加载完成 重复12次 共计1min
            for (int i = 0; i < 60; i++) {
                try {
                    Thread.sleep(1000);
                    sendUpdateCustomSetting();
                    return;
                } catch (Exception ignored) {
                }
            }
            ShapeShifterCurseFabric.LOGGER.error("Failed to send custom setting to server after 60 seconds");
        }).start();
    }

    public static void sendUpdateCustomColor(FormTextureUtils.ColorSetting colorSetting, boolean sendRAW) {
        PacketByteBuf buf = PacketByteBufs.create();
        if (sendRAW) {
            buf.writeInt(colorSetting.getPrimaryColor());
            buf.writeInt(colorSetting.getAccentColor1());
            buf.writeInt(colorSetting.getAccentColor2());
            buf.writeInt(colorSetting.getEyeColorA());
            buf.writeInt(colorSetting.getEyeColorB());
        } else {
            buf.writeInt(FormTextureUtils.ARGB2ABGR(colorSetting.getPrimaryColor()));
            buf.writeInt(FormTextureUtils.ARGB2ABGR(colorSetting.getAccentColor1()));
            buf.writeInt(FormTextureUtils.ARGB2ABGR(colorSetting.getAccentColor2()));
            buf.writeInt(FormTextureUtils.ARGB2ABGR(colorSetting.getEyeColorA()));
            buf.writeInt(FormTextureUtils.ARGB2ABGR(colorSetting.getEyeColorB()));
        }
        buf.writeBoolean(colorSetting.getPrimaryGreyReverse());
        buf.writeBoolean(colorSetting.getAccent1GreyReverse());
        buf.writeBoolean(colorSetting.getAccent2GreyReverse());
        ClientPlayNetworking.send(UPDATE_CUSTOM_COLOR, buf);
    }

    // 临时先放这里，以后再整理
    public static void sendUpdateCustomSetting(boolean ForceUpdate) {
        PacketByteBuf buf = PacketByteBufs.create();
        boolean autoSyncConfig = ShapeShifterCurseFabric.playerCustomConfig.auto_sync_config;
        if (!ForceUpdate && !autoSyncConfig) {
            return;
        }
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.keep_original_skin);
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.enable_form_color);
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.enable_form_random_sound);
        ClientPlayNetworking.send(UPDATE_CUSTOM_SETTING, buf);
        boolean autoSyncColorConfig = ShapeShifterCurseFabric.playerCustomConfig.auto_sync_color_config;
        if (!ForceUpdate && !autoSyncColorConfig) {
            return;
        }
        buf = PacketByteBufs.create();
        int AGBRInt = 0;
        AGBRInt = FormTextureUtils.ARGB2ABGR(ShapeShifterCurseFabric.playerCustomConfig.primaryColor);
        buf.writeInt(AGBRInt);
        AGBRInt = FormTextureUtils.ARGB2ABGR(ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color);
        buf.writeInt(AGBRInt);
        AGBRInt = FormTextureUtils.ARGB2ABGR(ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color);
        buf.writeInt(AGBRInt);
        AGBRInt = FormTextureUtils.ARGB2ABGR(ShapeShifterCurseFabric.playerCustomConfig.eyeColorA);
        buf.writeInt(AGBRInt);
        AGBRInt = FormTextureUtils.ARGB2ABGR(ShapeShifterCurseFabric.playerCustomConfig.eyeColorB);
        buf.writeInt(AGBRInt);
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse);
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse);
        buf.writeBoolean(ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse);
    }

    public static void sendUpdateCustomSetting() {
        sendUpdateCustomSetting(false);
    }

    public static void receiveActiveVirtualTotem(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // LivingEntity entity, int virtualTotemType, ItemStack totemStack
        if (client.world == null) {
            ShapeShifterCurseFabric.LOGGER.error("World is null when receiving active virtual totem packet");
            return;
        }
        PlayerEntity playerEntity = client.world.getPlayerByUuid(buf.readUuid());
        if (playerEntity == null) {
            ShapeShifterCurseFabric.LOGGER.warn("Can't find player entity when receiving active virtual totem packet");
            return;
        }
        Identifier virtualTotemType = buf.readIdentifier();
        ItemStack totemStack = buf.readItemStack();
        // ConcurrentModificationException 需要把这个操作放到Client线程而非Network线程
        client.execute(() -> VirtualTotemPower.process_virtual_totem_type(playerEntity, virtualTotemType, totemStack));
    }

    public static void receivePowerAnimationData(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID playerUuid = buf.readUuid();
        @Nullable Identifier animationId;
        if (buf.readBoolean()) {
            animationId = buf.readIdentifier();
        }
        else {
            animationId = null;
        }
        int animationCount = buf.readInt();
        int animationLength = buf.readInt();
        if (client.world == null) {
            ShapeShifterCurseFabric.LOGGER.error("World is null when receiving update power anim data packet");
            return;
        }
        PlayerEntity playerEntity = client.world.getPlayerByUuid(playerUuid);
        // ShapeShifterCurseFabric.LOGGER.info("Received power animation data for player " + playerUuid + " animationId " + animationId + " animationCount " + animationCount + " animationLength " + animationLength);
        client.execute(() -> {
            if (playerEntity instanceof IPlayerAnimController animPlayer) {
                animPlayer.shape_shifter_curse$setAnimationData(animationId, animationCount, animationLength);
            } else {
                ShapeShifterCurseFabric.LOGGER.error("Player {} is not a IPlayerAnimController when receiving update power anim data packet", playerEntity.getName());
            }
        });
    }

    public static void sendPowerAnimationDataToServer(@Nullable Identifier animationId, int animationCount, int animationLength) {
        PacketByteBuf buf = PacketByteBufs.create();
        if (animationId != null) {
            buf.writeBoolean(true);
            buf.writeIdentifier(animationId);
        }
        else {
            buf.writeBoolean(false);
        }
        buf.writeInt(animationCount);
        buf.writeInt(animationLength);
        ClientPlayNetworking.send(UPDATE_POWER_ANIM_DATA_TO_SERVER, buf);
    }

    public static void sendRequestPlayerAnimationData(UUID targetPlayerUUID) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetPlayerUUID);
        ClientPlayNetworking.send(REQUEST_POWER_ANIM_DATA, buf);
    }

    public static void receiveUpdatePatronLevel(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int PairCount = buf.readInt();
        HashMap<UUID, Integer> map = new HashMap<>();
        for (int i = 0; i < PairCount; i++) {
            UUID uuid = buf.readUuid();
            int level = buf.readInt();
            map.put(uuid, level);
        }
        client.execute(() -> {
            PatronUtils.ApplyPatronLevel(map);
        });
    }

    public static void receiveOpenPatronFormSelectMenu(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            Screen screen = new PatronFormSelectScreen(Text.literal("PatronFromSelectScreen"), client.player);
            client.setScreen(screen);
        });
    }

    public static void receiveOpenFormSelectMenu(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String targetName = buf.readString();
        UUID targetUUID = buf.readUuid();
        client.execute(() -> {
            Screen screen = new NormalFormSelectScreen(Text.literal("FormSelectScreen"), targetName, targetUUID);
            client.setScreen(screen);
        });
    }

    public static void sendSetPatronForm(Identifier formID) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(formID);
        ClientPlayNetworking.send(SET_PATRON_FORM, buf);
    }

    public static void sendSetForm(Identifier formID, UUID target) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(target);
        buf.writeIdentifier(formID);
        ClientPlayNetworking.send(SET_FORM, buf);
    }

    public static void receiveSetNoJumpTick(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int tick = buf.readInt();
        client.execute(() -> {
            if (client.player instanceof IJumpController jumpController) {
                jumpController.shape_shifter_curse$setNoJumpTick(tick);
            }
        });
    }

    public static void receiveOpenFCSMenu(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            if (FormColorSelectMenu.instance == null) {
                Screen screen = new FormColorSelectMenu(Text.literal("text.shape-shifter-curse.config.form_color_select_menu"));
                client.setScreen(screen);
            }
        });
    }

    public static void receiveModifyFCDData(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String commandType = buf.readString();
        Identifier formID = buf.readIdentifier();
        String arg1 = buf.readString();
        String arg2 = buf.readString();
        String arg3 = buf.readString();
        String arg4 = buf.readString();
        if (!ShapeShifterCurseFabric.playerCustomConfig.enable_server_modify_FCD_config) {
            return;
        }
        // commandType ->
        // save ->
        //     formID
        //     arg1 -> slot_type [form, global, form_default]
        //     arg2 -> slot_name
        // load ->
        //     formID
        //     arg1 -> slot_type [form, global, form_default]
        //     arg2 -> slot_name
        // delete ->
        //     formID
        //     arg1 -> slot_type [form, global, form_default]
        //     arg2 -> slot_name
        // config ->
        //     formID -> not used
        //     arg1 -> config_type [enable_default_color]
        //     arg2 -> config_value -> not used only toggle
        // list ->
        //     formID
        //     arg1 -> slot_type [form, global, form_default]
        switch (commandType) {
            case "save" -> {
                FormTextureUtils.ColorSetting nowColorSetting = FormColorData.getPlayerColorSetting(false);
                if (nowColorSetting == null) {
                    return;
                }
                switch (arg1) {
                    case "form" -> {
                        ShapeShifterCurseFabricClient.formColorData.customSettingByForm.computeIfAbsent(formID, k -> new HashMap<>()).put(arg2, nowColorSetting);
                    }
                    case "global" -> {
                        ShapeShifterCurseFabricClient.formColorData.customSetting.put(arg2, nowColorSetting);
                    }
                    case "form_default" -> {
                        ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.put(formID, nowColorSetting);
                    }
                }
                ShapeShifterCurseFabricClient.formColorData.writeToConfig();
            }
            case "load" -> {
                FormTextureUtils.ColorSetting colorSetting = null;
                switch (arg1) {
                    case "form" -> {
                        colorSetting = ShapeShifterCurseFabricClient.formColorData.customSettingByForm.getOrDefault(formID, new HashMap<>()).getOrDefault(arg2, null);
                    }
                    case "global" -> {
                        colorSetting = ShapeShifterCurseFabricClient.formColorData.customSetting.getOrDefault(arg2, null);
                    }
                    case "form_default" -> {
                        colorSetting = ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.getOrDefault(formID, null);
                    }
                }
                if (colorSetting != null) {
                    ModPacketsS2C.sendUpdateCustomColor(colorSetting, false);
                }
            }
            case "delete" -> {
                switch (arg1) {
                    case "form" -> {
                        ShapeShifterCurseFabricClient.formColorData.customSettingByForm.computeIfAbsent(formID, k -> new HashMap<>()).remove(arg2);
                    }
                    case "global" -> {
                        ShapeShifterCurseFabricClient.formColorData.customSetting.remove(arg2);
                    }
                    case "form_default" -> {
                        ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.remove(formID);
                    }
                }
                ShapeShifterCurseFabricClient.formColorData.writeToConfig();
            }
            case "config" -> {
                switch (arg1) {
                    case "enable_default_color" -> {
                        ShapeShifterCurseFabricClient.formColorData.enableDefaultFormColor = !ShapeShifterCurseFabricClient.formColorData.enableDefaultFormColor;
                        if (client.player != null) {
                            client.player.sendMessage(Text.translatable("message.shape-shifter-curse.enable_default_color", ShapeShifterCurseFabricClient.formColorData.enableDefaultFormColor), true);
                        }
                    }
                }
                ShapeShifterCurseFabricClient.formColorData.writeToConfig();
            }
            case "list" -> {
                switch (arg1) {
                    case "form" -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("All Custom Form Color Settings For %s:\n|".formatted(formID));
                        ShapeShifterCurseFabricClient.formColorData.customSettingByForm.getOrDefault(formID, new HashMap<>()).forEach((k, v) -> stringBuilder.append(" %s |".formatted(k)));
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal(stringBuilder.toString()), false);
                        }
                    }
                    case "global" -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("All Custom Global Color Settings:\n|");
                        ShapeShifterCurseFabricClient.formColorData.customSetting.forEach((k, v) -> stringBuilder.append(" %s |".formatted(k)));
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal(stringBuilder.toString()), false);
                        }
                    }
                    case "form_default" -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("All Default Form Color Settings:\n|");
                        ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.forEach((k, v) -> stringBuilder.append(" %s |".formatted(k)));
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal(stringBuilder.toString()), false);
                        }
                    }
                }
            }
        }
    }
}
