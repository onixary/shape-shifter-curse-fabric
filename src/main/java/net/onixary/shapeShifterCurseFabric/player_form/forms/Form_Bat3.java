package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Bat3 extends PlayerFormBase {
    public Form_Bat3(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_slow_falling = AnimationHolder.EMPTY;
    private static AnimationHolder anim_tool_swing = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attach_side = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attach_bottom = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_VEHICLE_IDLE:
                return anim_sneak_idle;
            case ANIM_RIDE_IDLE:
                return anim_ride;

            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;

            case ANIM_JUMP:
            case ANIM_SNEAK_JUMP:
                return anim_jump;
            case ANIM_WALK:
                return anim_walk;
            case ANIM_RUN:
                return anim_run;

            case ANIM_IDLE:
                return anim_idle;
            case ANIM_ATTACH_SIDE:
                return anim_attach_side;
            case ANIM_ATTACH_BOTTOM:
                return anim_attach_bottom;


            case ANIM_SLOW_FALL:
            case ANIM_CREATIVE_FLY:
                return anim_slow_falling;

            case ANIM_TOOL_SWING:
            case ANIM_SNEAK_TOOL_SWING:
                return anim_tool_swing;

            case ANIM_ATTACK_ONCE:
            case ANIM_SNEAK_ATTACK_ONCE:
                return anim_attack;

            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_idle"), true);
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "bat_3_riding"), true);
        anim_jump = new AnimationHolder(new Identifier(MOD_ID, "bat_3_jump"), true, 1.5f, 2);
        anim_slow_falling = new AnimationHolder(new Identifier(MOD_ID, "bat_2_slow_falling"), true);
        anim_tool_swing = new AnimationHolder(new Identifier(MOD_ID, "bat_3_digging"), true, 1.5f, 2);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attack"), true,1.5f, 2);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_3_idle"), true);
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "bat_3_walk"), true, 1.7f, 4);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "bat_3_sneak_walk"), true);
        anim_attach_side = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attach_side"), true);
        anim_attach_bottom = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attach_bottom"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "bat_3_walk"), true, 2.4f, 4);
    }

    @Override
    public Vec3d getCapeIdleLoc(AbstractClientPlayerEntity player) {
        if (player.isOnGround()) {
            return new Vec3d(0.0f, 0.7f, 0.2f);
        }
        else {
            return new Vec3d(0.0, 0.0, 0.125);
        }
    }

    @Override
    public float getCapeBaseRotateAngle(AbstractClientPlayerEntity player) {
        return 100.0f;
    }

    @Override
    public boolean NeedModifyXRotationAngle() {
        return true;
    }

    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_walk"), 1.7f, 4), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_sneak_walk")));
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_walk"), 2.4f, 4), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_sneak_walk")));
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_digging"), 1.5f, 2));
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_attack"), 1.5f, 2));
    public static final AbstractAnimStateController JUMP_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_jump"), 1.5f, 2));
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));
    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController FLYING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return JUMP_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
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
                case ANIM_STATE_USE_ITEM:
                    return IDLE_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }

    // 虽然已经在AnimRegistries注册过默认值了 但是按照标准来说应该在这里注册 默认值仅为备用

    private static AnimationHolder POWER_ANIM_ATTACH_SIDE = AnimationHolder.EMPTY;
    private static AnimationHolder POWER_ANIM_ATTACH_BOTTOM = AnimationHolder.EMPTY;

    @Override
    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        POWER_ANIM_ATTACH_SIDE = new AnimationHolder(ShapeShifterCurseFabric.identifier("bat_3_attach_side"), true);
        POWER_ANIM_ATTACH_BOTTOM = new AnimationHolder(ShapeShifterCurseFabric.identifier("bat_3_attach_bottom"), true);
        super.registerPowerAnim(player, animSystemData);
    }

    @Override
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        if (powerAnimID.equals(AnimRegistries.POWER_ANIM_ATTACH_SIDE)) {
            return new Pair<>(true, POWER_ANIM_ATTACH_SIDE);
        } else if (powerAnimID.equals(AnimRegistries.POWER_ANIM_ATTACH_BOTTOM)) {
            return new Pair<>(true, POWER_ANIM_ATTACH_BOTTOM);
        }
        return super.getPowerAnim(player, animSystemData, powerAnimID);
    }
}
