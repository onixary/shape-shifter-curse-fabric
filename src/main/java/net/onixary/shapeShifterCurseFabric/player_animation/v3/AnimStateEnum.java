package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.EmptyController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.UseOtherStateAnimController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

// 仅用于快速编写FSM动作控制器
public enum AnimStateEnum {
    ANIM_STATE_SLEEP,
    ANIM_STATE_RIDE,
    ANIM_STATE_CLIMB,
    ANIM_STATE_SWIM,
    ANIM_STATE_FLYING,
    ANIM_STATE_FALL_FLYING,
    ANIM_STATE_FALL,
    ANIM_STATE_JUMP,
    ANIM_STATE_USE_ITEM,
    ANIM_STATE_MINING,
    ANIM_STATE_ATTACK,
    ANIM_STATE_WALK,
    ANIM_STATE_SPRINT,
    ANIM_STATE_IDLE;

    public static final HashMap <Identifier, AnimStateEnum> stateMap = new HashMap<>();

    static {
        stateMap.put(AnimRegistries.ANIM_STATE_SLEEP, ANIM_STATE_SLEEP);
        stateMap.put(AnimRegistries.ANIM_STATE_RIDE, ANIM_STATE_RIDE);
        stateMap.put(AnimRegistries.ANIM_STATE_CLIMB, ANIM_STATE_CLIMB);
        stateMap.put(AnimRegistries.ANIM_STATE_SWIM, ANIM_STATE_SWIM);
        stateMap.put(AnimRegistries.ANIM_STATE_FLYING, ANIM_STATE_FLYING);
        stateMap.put(AnimRegistries.ANIM_STATE_FALL_FLYING, ANIM_STATE_FALL_FLYING);
        stateMap.put(AnimRegistries.ANIM_STATE_FALL, ANIM_STATE_FALL);
        stateMap.put(AnimRegistries.ANIM_STATE_JUMP, ANIM_STATE_JUMP);
        stateMap.put(AnimRegistries.ANIM_STATE_USE_ITEM, ANIM_STATE_USE_ITEM);
        stateMap.put(AnimRegistries.ANIM_STATE_MINING, ANIM_STATE_MINING);
        stateMap.put(AnimRegistries.ANIM_STATE_ATTACK, ANIM_STATE_ATTACK);
        stateMap.put(AnimRegistries.ANIM_STATE_WALK, ANIM_STATE_WALK);
        stateMap.put(AnimRegistries.ANIM_STATE_SPRINT, ANIM_STATE_SPRINT);
        stateMap.put(AnimRegistries.ANIM_STATE_IDLE, ANIM_STATE_IDLE);
    }

    public static @Nullable AnimStateEnum getStateEnum(Identifier stateID) {
        return stateMap.get(stateID);
    }
}
