package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.ClimbAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.NormalSubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.IPatronForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.ModifyCapeRender;
import net.onixary.shapeShifterCurseFabric.util.Verify.PatronDataSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Form_Bat3_Sub_Avali extends NormalSubForm implements IPatronForm, ModifyCapeRender {
    public Form_Bat3_Sub_Avali(Identifier formID) {
        super(formID, RegPlayerForms.BAT_3);
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

    // 以下暂时沿用 bat_3 的披风渲染参数

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

    // 以下动画暂时沿用 bat_3 的动画 后续替换为 avali 专属动画时只需修改各 AnimationHolderData 的 id

    public static final AnimUtils.AnimationHolderData ANIM_CLIMB =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_climb"), 1.25f, 2);
    public static final AnimUtils.AnimationHolderData ANIM_CLIMB_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_attach_side"));
    public static final AnimUtils.AnimationHolderData ANIM_SLEEP =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_sleep"));

    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_walk"), 1.7f, 4), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_sneak_walk")));
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_walk"), 2.4f, 4), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_sneak_walk")));
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_digging"), 1.5f, 2));
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_attack"), 1.5f, 2));
    public static final AbstractAnimStateController JUMP_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_jump"), 1.5f, 2));
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));
    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_3_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController FLYING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));
    public static final AbstractAnimStateController CLIMB_CONTROLLER = new ClimbAnimController(ANIM_CLIMB_IDLE, ANIM_CLIMB);
    public static final AbstractAnimStateController SLEEP_CONTROLLER = new OneAnimController(ANIM_SLEEP);

    @Override
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
                case ANIM_STATE_CLIMB:
                    return CLIMB_CONTROLLER;
                case ANIM_STATE_MINING:
                    return MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return ATTACK_CONTROLLER;
                case ANIM_STATE_FLYING:
                    return FLYING_CONTROLLER;
                case ANIM_STATE_USE_ITEM:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_SLEEP:
                    return SLEEP_CONTROLLER;
                case ANIM_STATE_CRAWL:
                    return FLYING_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }

    // attach_side / attach_bottom 等能力动画未重写 经 NormalSubForm 委托沿用主形态 bat_3 的注册
}
