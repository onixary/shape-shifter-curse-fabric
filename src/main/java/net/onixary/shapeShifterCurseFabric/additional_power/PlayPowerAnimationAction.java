package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;

import java.util.List;
import java.util.function.Consumer;

public class PlayPowerAnimationAction {
    public static void register(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("play_power_animation_with_time"),
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("animation_time", SerializableDataTypes.INT, 0)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                PlayPowerAnimationAction::playPowerAnimationWithTime
        ));
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("play_power_animation_with_count"),
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("animation_count", SerializableDataTypes.INT, 1)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                PlayPowerAnimationAction::playPowerAnimationWithCount
        ));
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("play_power_animation_loop"),
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                PlayPowerAnimationAction::playPowerAnimationLoop
        ));
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("stop_power_animation"),
                new SerializableData()
                        .add("anim_id_list", SerializableDataTypes.IDENTIFIERS, null)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                PlayPowerAnimationAction::stopPowerAnimation
        ));
    }

    private static AnimUtils.AnimationSendSideType getAnimationSendSideType(SerializableData.Instance data) {
        if (data.getBoolean("can_on_client") && data.getBoolean("can_on_server")) {
            return AnimUtils.AnimationSendSideType.BOTH_SIDE;
        } else if (data.getBoolean("can_on_client")) {
            return AnimUtils.AnimationSendSideType.ONLY_CLIENT;
        } else if (data.getBoolean("can_on_server")) {
            return AnimUtils.AnimationSendSideType.ONLY_SERVER;
        } else {
            return AnimUtils.AnimationSendSideType.NONE;
        }
    }

    public static void playPowerAnimationWithTime(SerializableData.Instance data, Entity entity) {
        int animationTime = data.getInt("animation_time");
        Identifier powerAnimationId = data.getId("power_animation_id");
        if (animationTime <= 0 || powerAnimationId == null) {
            return;
        }
        if (entity instanceof PlayerEntity player) {
            AnimUtils.playPowerAnimWithTime(player, powerAnimationId, animationTime, getAnimationSendSideType(data));
        }
    }

    public static void playPowerAnimationWithCount(SerializableData.Instance data, Entity entity) {
        int animationCount = data.getInt("animation_count");
        Identifier powerAnimationId = data.getId("power_animation_id");
        if (animationCount <= 0 || powerAnimationId == null) {
            return;
        }
        if (entity instanceof PlayerEntity player) {
            AnimUtils.playPowerAnimWithCount(player, powerAnimationId, animationCount, getAnimationSendSideType(data));
        }
    }

    public static void playPowerAnimationLoop(SerializableData.Instance data, Entity entity) {
        Identifier powerAnimationId = data.getId("power_animation_id");
        if (powerAnimationId == null) {
            return;
        }
        if (entity instanceof PlayerEntity player) {
            AnimUtils.playPowerAnimLoop(player, powerAnimationId, getAnimationSendSideType(data));
        }
    }

    public static void stopPowerAnimation(SerializableData.Instance data, Entity entity) {
        List<Identifier> animIdList = data.get("anim_id_list");
        if (entity instanceof PlayerEntity player) {
            if (animIdList == null || animIdList.isEmpty()) {
                AnimUtils.stopPowerAnim(player, getAnimationSendSideType(data));
            } else {
                AnimUtils.stopPowerAnimWithIDs(player, getAnimationSendSideType(data), animIdList);
            }
        }
    }
}
