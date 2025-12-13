package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController.ConditionAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.SwimAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Axolotl1 extends PlayerFormBase {
    public Form_Axolotl1(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_swimming_idle = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
    }

    public static final AbstractAnimStateController SWIM_CONTROLLER = new SwimAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_swimming_idle")), null);

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SWIM:
                    return SWIM_CONTROLLER;
                default:
                    return null;
            }
        }
        return null;
    }

}
