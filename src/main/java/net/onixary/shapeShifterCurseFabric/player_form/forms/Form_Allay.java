package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Allay extends PlayerFormBase {
    public Form_Allay(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_digging = AnimationHolder.EMPTY;
    private static AnimationHolder anim_flying = AnimationHolder.EMPTY;
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;

    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_WALK:
                return anim_walk;

            case ANIM_RUN:
                return anim_run;

            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;

            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;

            case ANIM_IDLE:
                return anim_idle;

            case ANIM_FLY:
            case ANIM_JUMP:
            case ANIM_FALL:
            case ANIM_SNEAK_FALL:
            case ANIM_SLOW_FALL:
                return anim_flying;

            case ANIM_TOOL_SWING:
                return anim_digging;

            case ANIM_ATTACK_ONCE:
                return anim_attack;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_moving"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_run"), true);
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking"), true);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking_walk"), true);
        anim_digging = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_digging"), true);
        anim_flying = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_fly"), true);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_idle"), true);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_attack"), true);
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
                case ANIM_STATE_FLYING:
                    return FLYING_CONTROLLER;
                default:
                    return null;
            }
        }
        return null;
    }
}
