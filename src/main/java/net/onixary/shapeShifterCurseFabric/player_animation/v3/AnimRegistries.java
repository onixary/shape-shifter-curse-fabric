package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.EmptyController;

public class AnimRegistries {
    public static Identifier CONTROLLER_EMPTY = AnimRegistry.registerAnimStateController(ShapeShifterCurseFabric.identifier("empty_controller"), EmptyController::new);

    public static Identifier ANIM_STATE_POWER = AnimRegistry.registerAnimState(ShapeShifterCurseFabric.identifier("power_state"), new AnimRegistry.AnimState(new EmptyController()));
}
