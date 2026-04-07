package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Allay extends PlayerFormBase {
    public Form_Allay(Identifier formID) {
        super(formID);
    }

    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_moving")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_sneaking_walk")));
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_run")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_sneaking_walk")));
    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_sneaking")));
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_digging")));
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_attack")));
    public static final AbstractAnimStateController FLYING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("allay_sp_fly")));

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_WALK:
                    return WALK_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return SPRINT_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_MINING:
                    return MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return ATTACK_CONTROLLER;
                case ANIM_STATE_JUMP:
                case ANIM_STATE_FALL:
                case ANIM_STATE_FALL_FLYING:
                case ANIM_STATE_FLYING:
                    return FLYING_CONTROLLER;
                default:
                    return WALK_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
