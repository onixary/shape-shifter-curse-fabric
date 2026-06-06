package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;

import java.util.HashMap;
import java.util.UUID;

public class TransformManager {
    // Server Side
    public HashMap<UUID, Integer> playerTransformTimer = new HashMap<>();  // 默认值为-1 当大于等于0时开始每Tick递增 并且进入变形 当PlayerFormComponent.transformTargetForm != null且playerTransformTimer<0时开始变形
    // Client Side
    public int transformTimer = -1;  // 处理 nauesaStrength 和 blackStrength

    public void startTransform(PlayerEntity player, IForm form) {
        PlayerFormComponent.COMPONENT.get(player).transformTargetForm = form;
        playerTransformTimer.put(player.getUuid(), 0);
    }

    public void setForm(PlayerEntity player, IForm form) {
        PlayerFormComponent.COMPONENT.get(player).transformTargetForm = null;
        playerTransformTimer.put(player.getUuid(), -1);
        // TODO
    }

    private void startPlayerTransform(PlayerEntity player) {

    }

    private void middlePlayerTransform(PlayerEntity player) {

    }

    private void endPlayerTransform(PlayerEntity player) {

    }

    public void clientTick() {
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

    public void serverTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IForm form = PlayerFormComponent.COMPONENT.get(player).transformTargetForm;
            if (form == null) {
                continue;
            }
            int timer = playerTransformTimer.getOrDefault(player.getUuid(), -1);
            if (timer < 0) {
                continue;
            }
            if (timer == 0) {
                startPlayerTransform(player);  // 需要写一个同步包到客户端
            } else if (timer == StaticParams.TRANSFORM_FX_DURATION_IN) {
                middlePlayerTransform(player);
            } else if (timer == StaticParams.TRANSFORM_FX_DURATION_IN + StaticParams.TRANSFORM_FX_DURATION_OUT) {
                endPlayerTransform(player);  // 此处调用 setForm 会清空playerTransformTimer
            }
            timer = playerTransformTimer.getOrDefault(player.getUuid(), -1);
            if (timer >= 0) {
                playerTransformTimer.put(player.getUuid(), timer + 1);
            }
        }
    }

}
