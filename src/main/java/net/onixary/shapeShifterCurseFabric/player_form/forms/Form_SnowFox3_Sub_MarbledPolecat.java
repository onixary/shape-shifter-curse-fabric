package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController.IdleStayAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.NormalSubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.IPatronForm;
import net.onixary.shapeShifterCurseFabric.util.Verify.PatronDataSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Form_SnowFox3_Sub_MarbledPolecat extends NormalSubForm implements IPatronForm {
    public Form_SnowFox3_Sub_MarbledPolecat(Identifier formID) {
        super(formID, RegPlayerForms.SNOW_FOX_3);
        this.addPower(ShapeShifterCurseFabric.identifier("sub_form_marbled_polecat_idle_stay_eye_height"));

    }

    @Override
    public @Nullable Pair<Identifier, Identifier> getRenderLayerOverride() {
        return new Pair<>(Identifier.of("origins", "origin"), Identifier.of(this.getFormID().getNamespace(), "form_" + this.getFormID().getPath()));
    }

    @Override
    public boolean checkCanUse(@Nullable PlayerEntity player, @Nullable UUID playerUUID, @Nullable PatronDataSegment patronData) {
        if (patronData == null || player == null) {
            return false;
        }
        return patronData.getLevel() >= 5;
    }

    private static final AnimUtils.AnimationHolderData ANIM_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_idle"));
    private static final AnimUtils.AnimationHolderData ANIM_IDLE_STAY =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_idle_stay"),1.0f,4);
    private static final AnimUtils.AnimationHolderData ANIM_SNEAK_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_sneak_idle"));
    private static final AnimUtils.AnimationHolderData ANIM_WALK =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_walk"), 2.0f * 1.6f, 4);
    private static final AnimUtils.AnimationHolderData ANIM_SNEAK_WALK =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_sneak_walk"));
    public static final AnimUtils.AnimationHolderData ANIM_RUN =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_run"), 3.5f, 4);
    public static final AnimUtils.AnimationHolderData ANIM_JUMP =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_jump"), 1.0f, 2);
    public static final AnimUtils.AnimationHolderData ANIM_FALL =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_fall"), 1.0f, 4);
    public static final AnimUtils.AnimationHolderData ANIM_SWIM =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_swim"));
    public static final AnimUtils.AnimationHolderData ANIM_FLOAT =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_float"));
    public static final AnimUtils.AnimationHolderData ANIM_ATTACK =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_attack"));
    public static final AnimUtils.AnimationHolderData ANIM_DIG =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_dig"));
    public static final AnimUtils.AnimationHolderData ANIM_CLIMB =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_climb"));
    public static final AnimUtils.AnimationHolderData ANIM_CLIMB_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_climb_idle"));
    public static final AnimUtils.AnimationHolderData ANIM_ELYTRA_FLY =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_elytra_fly"));
    public static final AnimUtils.AnimationHolderData ANIM_SLEEP =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("weasel_sleep"));






    public static final AbstractAnimStateController IDLE_CONTROLLER = new IdleStayAnimController(
            new WithSneakAnimController(ANIM_IDLE, ANIM_SNEAK_IDLE),  // 保留原潜行 idle
            new OneAnimController(ANIM_IDLE_STAY), // 停留动画
            100  // 5秒
    );
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(ANIM_WALK, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(ANIM_RUN, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController JUMP_CONTROLLER = new OneAnimController(ANIM_JUMP);
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(ANIM_FALL);
    public static final AbstractAnimStateController SWIM_CONTROLLER = new SwimAnimController(ANIM_FLOAT, ANIM_SWIM);
    public static final AbstractAnimStateController USE_ITEM_CONTROLLER = new UseItemAnimControllerPro(ANIM_IDLE, ANIM_IDLE, ANIM_SNEAK_IDLE, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(ANIM_DIG);
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(ANIM_ATTACK);
    public static final AbstractAnimStateController CLIMB_CONTROLLER = new ClimbAnimController(ANIM_CLIMB_IDLE, ANIM_CLIMB);
    public static final AbstractAnimStateController FALL_FLYING_CONTROLLER = new OneAnimController(ANIM_ELYTRA_FLY);
    public static final AbstractAnimStateController SLEEP_CONTROLLER = new OneAnimController(ANIM_SLEEP);


    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SLEEP:
                    return SLEEP_CONTROLLER;
                case ANIM_STATE_CLIMB:
                    return CLIMB_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return JUMP_CONTROLLER;
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                case ANIM_STATE_SWIM:
                    return SWIM_CONTROLLER;
                case ANIM_STATE_USE_ITEM:
                    return USE_ITEM_CONTROLLER;
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
                case ANIM_STATE_FLYING:
                case ANIM_STATE_FALL_FLYING:
                    return FALL_FLYING_CONTROLLER;
                default:
                    return IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
