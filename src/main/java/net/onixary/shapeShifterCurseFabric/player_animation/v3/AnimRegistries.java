package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.InAirFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.OnGroundFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.UseItemFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;

public class AnimRegistries {
    // 在这里注册的为样板AnimStateController由数据包作为样板使用
    public static Identifier CONTROLLER_EMPTY = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("empty_controller"), EmptyController::new);
    public static Identifier CONTROLLER_USE_OTHER_STATE_ANIM = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("use_other_state_anim_controller"), UseOtherStateAnimController::new);
    public static Identifier CONTROLLER_ONE_ANIM = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("one_anim_controller"), OneAnimController::new);
    public static Identifier CONTROLLER_SNEAK_ANIM = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("sneak_anim_controller"), WithSneakAnimController::new);
    public static Identifier CONTROLLER_SNEAK_RUSH_ANIM = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("sneak_rush_anim_controller"), SneakRushAnimController::new);

    // AnimState注册
    public static Identifier ANIM_STATE_SLEEP = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("sleep_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_RIDE = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("ride_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_CLIMB = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("climb_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_SWIM = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("swim_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_FLYING = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("flying_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_FALL_FLYING = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("fall_flying_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_FALL = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("fall_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_JUMP = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("jump_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_USE_ITEM = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("use_item_state"), new AnimRegistry.AnimState(new UseOtherStateAnimController(AnimRegistries.ANIM_STATE_USE_ITEM)));
    public static Identifier ANIM_STATE_MINING = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("mining_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_ATTACK = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("attack_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_WALK = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("walk_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_SPRINT = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("sprint_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_IDLE = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("idle_state"), new AnimRegistry.AnimState(new EmptyController()));

    // AnimFSM注册
    public static Identifier FSM_ON_GROUND = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("on_ground"), new OnGroundFSM());
    public static Identifier FSM_IN_AIR = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("in_air"), new InAirFSM());
    public static Identifier FSM_USE_ITEM = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("use_item"), new UseItemFSM());
}
