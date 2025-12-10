package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.InAirFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.OnGroundFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM.UseItemFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.EmptyController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.UseOtherStateAnimController;

public class AnimRegistries {
    // 在这里注册的为样板AnimStateController由数据包作为样板使用
    public static Identifier CONTROLLER_EMPTY = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("empty_controller"), EmptyController::new);
    public static Identifier CONTROLLER_USE_OTHER_STATE_ANIM = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("use_other_state_anim_controller"), UseOtherStateAnimController::new);

    public static Identifier ANIM_STATE_POWER = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("power_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_SLEEP = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("sleep_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_RIDE = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("ride_state"), new AnimRegistry.AnimState(new EmptyController()));
    public static Identifier ANIM_STATE_RIDE_VEHICLE = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("ride_vehicle_state"), new AnimRegistry.AnimState(new UseOtherStateAnimController(ANIM_STATE_RIDE)));

    public static Identifier FSM_ON_GROUND = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("on_ground"), new OnGroundFSM());
    public static Identifier FSM_IN_AIR = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("in_air"), new InAirFSM());
    public static Identifier FSM_USE_ITEM = AnimRegistry.registerAnimFSM(ShapeShifterCurseFabric.identifier("use_item"), new UseItemFSM());
}
