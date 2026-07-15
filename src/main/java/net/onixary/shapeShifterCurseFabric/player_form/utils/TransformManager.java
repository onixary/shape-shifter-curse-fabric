package net.onixary.shapeShifterCurseFabric.player_form.utils;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.effect.PlayerTransformEffectManager;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class TransformManager {
    public static class PlayerTransformData {
        public @NotNull PlayerEntity player;
        public int transformTimer = -1;
        public @Nullable IForm transformStartForm = null;
        public @Nullable IForm transformEndForm = null;
        // 注意 当玩家退出重进时此逻辑失效
        public @Nullable Consumer<PlayerTransformData> onTransformComplete = null;
    }

    // Server Side
    private static final HashMap<UUID, PlayerTransformData> playerData = new HashMap<>();  // 默认值为-1 当大于等于0时开始每Tick递增 并且进入变形 当PlayerFormComponent.transformTargetForm != null且playerTransformTimer<0时开始变形
    // Client Side
    public static int transformTimer = -1;  // 处理 nauesaStrength 和 blackStrength


    public static void onServerInit() {
        playerData.clear();
        transformTimer = -1;
    }

    private static PlayerTransformData getPlayerData(PlayerEntity player) {
        PlayerTransformData data = playerData.get(player.getUuid());
        if (data == null) {
            data = new PlayerTransformData();
            data.player = player;
            playerData.put(player.getUuid(), data);
        }
        return data;
    }

    public static boolean startTransform(PlayerEntity player, IForm form, @Nullable Consumer<PlayerTransformData> onTransformComplete) {
        if (form.isPlayerForm(player) || !(player instanceof ServerPlayerEntity serverPlayerEntity)) {
            return true;
        }
        if (!FormUtils.isFormCanUse(player, form)) {
            return false;
        }
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.transformTargetForm = form;
        PlayerTransformData data = getPlayerData(player);
        data.transformTimer = 0;
        data.transformStartForm = FormUtils.getPlayerForm(player);
        data.transformEndForm = form;
        data.onTransformComplete = onTransformComplete;
        ShapeShifterCurseFabric.ON_TRANSFORM_FORM.trigger(serverPlayerEntity, form);
        if (ShapeShifterCurseFabric.commonConfig.immediatelyTransform) {
            data.transformTimer = -1;
            setForm(player);
            if (data.onTransformComplete != null) {
                data.onTransformComplete.accept(data);
                data.onTransformComplete = null;
            }
        }
        return true;
    }

    public static boolean immediatelyTransform(PlayerEntity player, IForm form) {
        if (form.isPlayerForm(player) || !(player instanceof ServerPlayerEntity serverPlayerEntity)) {
            return true;
        }
        if (!FormUtils.isFormCanUse(player, form)) {
            return false;
        }
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.transformTargetForm = form;
        PlayerTransformData data = getPlayerData(player);
        data.transformTimer = -1;
        data.transformStartForm = FormUtils.getPlayerForm(player);
        data.transformEndForm = form;
        data.onTransformComplete = null;
        ShapeShifterCurseFabric.ON_TRANSFORM_FORM.trigger(serverPlayerEntity, form);
        setForm(player);
        return true;
    }

    private static void setForm(PlayerEntity player) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.transformTargetForm = null;
        PlayerTransformData data = getPlayerData(player);
        IForm form = data.transformEndForm;
        EffectManager.clearTransformativeEffect(player);
        FormUtils._setForm(player, form);
        FormUtils.updateFormHistory(player, data.transformStartForm, form);
        sendClientFirstPersonReset(player);
    }

    private static void startPlayerTransform(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformData data = getPlayerData(player);
            IForm nowForm = data.transformStartForm;
            IForm targetForm = data.transformEndForm;
            ModPacketsS2CServer.sendTransformState(serverPlayerEntity, true, nowForm == null ? null : nowForm.getFormID(), targetForm == null ? null : targetForm.getFormID());
            InstinctUtils.playerInstinctLock.put(player.getUuid(), true);
            PlayerTransformEffectManager.applyStartTransformEffect(serverPlayerEntity, StaticParams.TRANSFORM_FX_DURATION_IN);
        }
    }

    private static void middlePlayerTransform(PlayerEntity player) {
        // PlayerTransformData data = getPlayerData(player);
        setForm(player);
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformEffectManager.applyEndTransformEffect(serverPlayerEntity, StaticParams.TRANSFORM_FX_DURATION_OUT);
        }
    }

    private static void endPlayerTransform(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformData data = getPlayerData(player);
            IForm nowForm = data.transformStartForm;
            IForm targetForm = data.transformEndForm;
            ModPacketsS2CServer.sendTransformState(serverPlayerEntity, false, nowForm == null ? null : nowForm.getFormID(), targetForm == null ? null : targetForm.getFormID());
            InstinctUtils.playerInstinctLock.put(player.getUuid(), false);
            PlayerTransformEffectManager.applyFinaleTransformEffect(serverPlayerEntity, 5);
            if (data.onTransformComplete != null) {
                data.onTransformComplete.accept(data);
                data.onTransformComplete = null;
            }
        }
    }

    public static void clientTick() {
        if (transformTimer < 0) {
            return;
        }
        float nauesaStrength = 0.0f;
        float blackStrength = 0.0f;
        if (transformTimer < StaticParams.TRANSFORM_FX_DURATION_IN) {
            nauesaStrength = transformTimer / (float) StaticParams.TRANSFORM_FX_DURATION_IN;
            blackStrength = Math.max(nauesaStrength - 0.8f, 0.0f) * 5;
        } else if (transformTimer < (StaticParams.TRANSFORM_FX_DURATION_IN + StaticParams.TRANSFORM_FX_DURATION_OUT)) {
            nauesaStrength = 1.0f - ((transformTimer - StaticParams.TRANSFORM_FX_DURATION_IN) / (float) StaticParams.TRANSFORM_FX_DURATION_IN);
            blackStrength = Math.min(1.0f, nauesaStrength / 0.6f);
        } else {
            transformTimer = -1;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
        if (transformTimer >= 0) {
            transformTimer++;
        }
    }

    public static void serverTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IForm form = PlayerFormComponent.COMPONENT.get(player).transformTargetForm;
            PlayerTransformData data = getPlayerData(player);
            int timer = data.transformTimer;
            if (form == null && timer < 0) {
                continue;
            }
            if (timer < 0) {
                startTransform(player, form, null);
                continue;
            }
            if (timer == 0) {
                startPlayerTransform(player);
            } else if (timer == StaticParams.TRANSFORM_FX_DURATION_IN) {
                middlePlayerTransform(player);
            } else if (timer == (StaticParams.TRANSFORM_FX_DURATION_IN + StaticParams.TRANSFORM_FX_DURATION_OUT)) {
                endPlayerTransform(player);
                data.transformTimer = -1;
            }
            if (data.transformTimer >= 0) {
                data.transformTimer++;
            }
        }
    }

    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");

    public static void executeClientFirstPersonReset() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        if (IS_FIRST_PERSON_MOD_LOADED && ShapeShifterCurseFabric.clientConfig.enableChangeFPMConfig) {
            FirstPersonModelCore fpm = FirstPersonModelCore.instance;
            fpm.getConfig().xOffset = 0;
            fpm.getConfig().sitXOffset = 0;
            fpm.getConfig().sneakXOffset = 0;
            new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) { }
                    fpm.getConfig().xOffset = 0;
                    fpm.getConfig().sitXOffset = 0;
                    fpm.getConfig().sneakXOffset = 0;
                }
            }).start();
        }
    }

    private static void sendClientFirstPersonReset(PlayerEntity player) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            executeClientFirstPersonReset();
        } else if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send(serverPlayerEntity, ModPackets.RESET_FIRST_PERSON, buf);
        }
    }
}
