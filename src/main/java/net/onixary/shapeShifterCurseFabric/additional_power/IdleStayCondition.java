package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.FSMUtils;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController.IdleStayAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

import java.util.HashMap;
import java.util.UUID;

// 判定玩家当前是否处于Idle Stay(静止Idle达到该形态配置的阈值) 服务端复算 与客户端动画逐帧一致
public class IdleStayCondition {
    // 每玩家静止Idle累计tick数 服务端复算
    private static final HashMap<UUID, Long> idleStayTickCounterMap = new HashMap<>();
    // 每玩家上一tick位置 用于计算IsWalking
    private static final HashMap<UUID, Vec3d> lastPositionMap = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tickIdleStayCounter(player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            idleStayTickCounterMap.remove(handler.player.getUuid());
            lastPositionMap.remove(handler.player.getUuid());
        });
    }

    private static void tickIdleStayCounter(ServerPlayerEntity player) {
        Vec3d lastPos = lastPositionMap.getOrDefault(player.getUuid(), player.getPos());
        AnimSystem.AnimSystemData data = new AnimSystem.AnimSystemData(player);
        data.playerForm = FormUtils.getPlayerForm(player);
        data.IsOnGround = AnimSystem.checkOnGroundSuper(player);
        data.IsWalking = !lastPos.equals(player.getPos());
        if (FSMUtils.IsIdleStayCondition(player, data)) {
            idleStayTickCounterMap.merge(player.getUuid(), 1L, Long::sum);
        } else {
            idleStayTickCounterMap.remove(player.getUuid());
        }
        lastPositionMap.put(player.getUuid(), player.getPos());
    }

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return false;
        }
        // 形态检测:当前形态是否为ANIM_STATE_IDLE配置了IdleStayAnimController 未注册则永远为false
        IForm form = FormUtils.getPlayerForm(player);
        AnimSystem.AnimSystemData animData = new AnimSystem.AnimSystemData(player);
        animData.playerForm = form;
        IdleStayAnimController idleStayController = FSMUtils.GetIdleStayController(player, animData);
        if (idleStayController == null) {
            return false;
        }
        long counter = idleStayTickCounterMap.getOrDefault(player.getUuid(), 0L);
        return counter >= idleStayController.getStayTickThreshold();
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                ShapeShifterCurseFabric.identifier("idle_stay"),
                new SerializableData(),
                IdleStayCondition::condition
        );
    }
}
