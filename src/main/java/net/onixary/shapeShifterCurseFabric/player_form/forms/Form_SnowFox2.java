package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_SnowFox2 extends PlayerFormBase {
    public Form_SnowFox2(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_VEHICLE_IDLE:
                return anim_sneak_idle;
            case ANIM_RIDE_IDLE:
                return anim_ride;

            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_idle"), true);
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "snow_fox_2_riding"), true);
    }

    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("snow_fox_2_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("ocelot_2_sneak_idle")));

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_IDLE:
                    return Form_FamiliarFox2.IDLE_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
