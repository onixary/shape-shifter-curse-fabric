package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RushJumpAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.SneakRushAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Ocelot2 extends PlayerFormBase {
    public Form_Ocelot2(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_rush = AnimationHolder.EMPTY;
    private static AnimationHolder anim_rush_jump = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_VEHICLE_IDLE:
                return anim_sneak_idle;
            case ANIM_RIDE_IDLE:
                return anim_ride;
            case ANIM_SNEAK_RUSH:
                return anim_sneak_rush;
            // case ANIM_RUSH_JUMP:
            case ANIM_SNEAK_FALL:  // TODO 先临时使用 rush_jump 的动作
            case ANIM_SNEAK_JUMP:
            case ANIM_SNEAK_RUSH_JUMP:
                return anim_rush_jump;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_idle"), true);
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_riding"), true);
        anim_sneak_rush = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_rush_2"), true, 3.3f);
        anim_rush_jump = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_rush_jump"), true);
    }

    private static final AnimUtils.AnimationHolderData SNEAK_RUSH_JUMP_ANIM = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_rush_jump"));

    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(null, new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_sneak_idle")));
    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_sneak_idle")));
    public static final AbstractAnimStateController SNEAK_RUSH_CONTROLLER = new SneakRushAnimController(null, null, new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_sneak_rush_2"), 3.3f));
    public static final AbstractAnimStateController RUSH_JUMP_CONTROLLER = new RushJumpAnimController(null, SNEAK_RUSH_JUMP_ANIM, null, SNEAK_RUSH_JUMP_ANIM);
    public static final AbstractAnimStateController FALL_CONTROLLER = new WithSneakAnimController(null, SNEAK_RUSH_JUMP_ANIM);

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                case ANIM_STATE_WALK:
                case ANIM_STATE_SPRINT:
                    return SNEAK_RUSH_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return RUSH_JUMP_CONTROLLER;
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }

}
