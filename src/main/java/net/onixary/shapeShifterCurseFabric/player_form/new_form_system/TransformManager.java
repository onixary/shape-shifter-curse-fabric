package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.effect.PlayerTransformEffectManager;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
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
    private static int transformTimer = -1;  // 处理 nauesaStrength 和 blackStrength

    private static PlayerTransformData getPlayerData(PlayerEntity player) {
        PlayerTransformData data = playerData.get(player.getUuid());
        if (data == null) {
            data = new PlayerTransformData();
            data.player = player;
            playerData.put(player.getUuid(), data);
        }
        return data;
    }

    public static void startTransform(PlayerEntity player, IForm form, @Nullable Consumer<PlayerTransformData> onTransformComplete) {
        PlayerFormComponent.COMPONENT.get(player).transformTargetForm = form;
        PlayerTransformData data = getPlayerData(player);
        data.transformTimer = 0;
        data.transformStartForm = FormUtils.getPlayerForm(player);
        data.transformEndForm = form;
        data.onTransformComplete = onTransformComplete;
        // 瞬间变形配置
        // if (XXXX) {
        //     data.transformTimer = -1;
        //     setForm(player);
        // }
    }

    public static void immediatelyTransform(PlayerEntity player, IForm form) {
        PlayerFormComponent.COMPONENT.get(player).transformTargetForm = form;
        PlayerTransformData data = getPlayerData(player);
        data.transformTimer = -1;
        data.transformStartForm = FormUtils.getPlayerForm(player);
        data.transformEndForm = form;
        data.onTransformComplete = null;
        setForm(player);
    }

    public static void setForm(PlayerEntity player) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.transformTargetForm = null;
        PlayerTransformData data = getPlayerData(player);
        IForm form = data.transformEndForm;
        data.transformTimer = -1;
        EffectManager.clearTransformativeEffect(player);
        FormUtils._setForm(player, form);
        FormUtils.updateFormHistory(player, data.transformStartForm, form);
    }

    private static void startPlayerTransform(PlayerEntity player) {
        // 向客户端同步数据
        PlayerTransformData data = getPlayerData(player);
        IForm nowForm = data.transformStartForm;
        IForm targetForm = data.transformEndForm;
        // 改成 Identifier
        // ModPacketsS2CServer.sendTransformState(player, true, nowForm.getFormID(), targetForm.getFormID());
        // 顺便把同步transformTimer挂在SYNC_TRANSFORM_STATE上
        // 顺便把playClientTransformEffect逻辑也挂上
        InstinctTicker.isPausing = true;
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformEffectManager.applyStartTransformEffect(serverPlayerEntity, StaticParams.TRANSFORM_FX_DURATION_IN);
        }
    }

    private static void middlePlayerTransform(PlayerEntity player) {
        PlayerTransformData data = getPlayerData(player);
        // 清空本能值挂在本能值触发变形时
        setForm(player);
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformEffectManager.applyEndTransformEffect(serverPlayerEntity, StaticParams.TRANSFORM_FX_DURATION_OUT);
        }
    }

    private static void endPlayerTransform(PlayerEntity player) {
        PlayerTransformData data = getPlayerData(player);
        IForm nowForm = data.transformStartForm;
        IForm targetForm = data.transformEndForm;
        // 改成 Identifier
        // ModPacketsS2CServer.sendTransformState(player, false, nowForm.getFormID(), nowForm.getFormID());
        // 顺便把executeClientTransformCompleteEffect逻辑挂上
        InstinctTicker.isPausing = false;
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PlayerTransformEffectManager.applyFinaleTransformEffect(serverPlayerEntity, 5);
        }
        if (data.onTransformComplete != null) {
            data.onTransformComplete.accept(data);
            data.onTransformComplete = null;
        }
    }

    public static void clientTick() {
        if (transformTimer < 0) {
            return;
        }
        float nauesaStrength = 0.0f;
        float blackStrength = 0.0f;
        if (transformTimer < StaticParams.TRANSFORM_FX_DURATION_IN) {
            nauesaStrength = 1.0f - (transformTimer / (float) StaticParams.TRANSFORM_FX_DURATION_IN);
            blackStrength = Math.max(nauesaStrength - 0.8f, 0.0f) * 5;
        } else if (transformTimer < StaticParams.TRANSFORM_FX_DURATION_IN + StaticParams.TRANSFORM_FX_DURATION_OUT) {
            nauesaStrength = 1.0f - ((transformTimer - StaticParams.TRANSFORM_FX_DURATION_IN) / (float) StaticParams.TRANSFORM_FX_DURATION_IN);
            blackStrength = Math.min(1.0f, nauesaStrength / 0.6f);
        } else {
            transformTimer = -1;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
        transformTimer++;
    }

    public static void serverTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IForm form = PlayerFormComponent.COMPONENT.get(player).transformTargetForm;
            if (form == null) {
                continue;
            }
            PlayerTransformData data = getPlayerData(player);
            int timer = data.transformTimer;
            if (timer < 0) {
                startTransform(player, form, null);
                continue;
            }
            if (timer == 0) {
                startPlayerTransform(player);
            } else if (timer == StaticParams.TRANSFORM_FX_DURATION_IN) {
                middlePlayerTransform(player);
            } else if (timer == StaticParams.TRANSFORM_FX_DURATION_IN + StaticParams.TRANSFORM_FX_DURATION_OUT) {
                endPlayerTransform(player);
                data.transformTimer = -1;
            }
            if (data.transformTimer >= 0) {
                data.transformTimer++;
            }
        }
    }
}
