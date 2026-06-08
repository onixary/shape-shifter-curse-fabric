package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.NormalForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_SnowFox3 extends NormalForm {
    public Form_SnowFox3(Identifier formID) {
        super(formID);
    }

    public static AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("snow_fox_3_riding")));
    public static AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_snow_fox_3_fall"), 1.0f, 4));

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SLEEP:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.SLEEP_CONTROLLER;
                case ANIM_STATE_CLIMB:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.CLIMB_CONTROLLER;
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.JUMP_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                case ANIM_STATE_SWIM:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.SWIM_CONTROLLER;
                case ANIM_STATE_USE_ITEM:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.USE_ITEM_CONTROLLER;
                case ANIM_STATE_WALK:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.WALK_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.SPRINT_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.IDLE_CONTROLLER;
                case ANIM_STATE_MINING:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.ATTACK_CONTROLLER;
                case ANIM_STATE_FLYING:
                case ANIM_STATE_FALL_FLYING:
                    return net.onixary.shapeShifterCurseFabric.player_form.old.forms.Form_FeralBase.FALL_FLYING_CONTROLLER;
                default:
                    return Form_FeralBase.IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
