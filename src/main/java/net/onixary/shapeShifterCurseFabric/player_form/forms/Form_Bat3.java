package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Form_Bat3 extends PlayerFormBase {
    public Form_Bat3(Identifier formID) {
        super(formID);
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
