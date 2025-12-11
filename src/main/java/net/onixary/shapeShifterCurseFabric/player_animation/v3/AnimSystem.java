package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// 每个玩家的动画系统
public class AnimSystem {
    public static class AnimSystemData {
        public PlayerFormBase playerForm;
        public boolean IsOnGround = true;
        public Vec3d LastPosition = Vec3d.ZERO;
        public long LastPosYChange = 0;  // 持续增长使用long防止溢出 顺便可以不用做最大值判断
        public long ContinueSwingAnimCounter = 0;  // 持续增长使用long防止溢出 顺便可以不用做最大值判断
        public NbtCompound customData;  // 用于存储其他拓展Mod的数据 在本模组中不使用

        public AnimSystemData(PlayerEntity player) {
            this.playerForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
            this.customData = new NbtCompound();
            this.LastPosition = player.getPos();
        }
    }
    public final PlayerEntity player;  // 玩家实体 理论上如果当前玩家实体被卸载了 那么这个AnimSystem也应该被卸载

    public static final Identifier defaultAnimFSMID = ShapeShifterCurseFabric.identifier("on_ground");

    public Identifier nowAnimFSMID = defaultAnimFSMID;

    public AnimSystemData data;

    public @NotNull AbstractAnimFSM getAnimFSM() {
        // 及时崩溃报错 省的找问题
        return Objects.requireNonNull(AnimRegistry.getAnimFSM(nowAnimFSMID));
    }

    public AnimSystem(PlayerEntity player) {
        this.player = player;
        this.data = new AnimSystemData(player);
    }

    private void PreProcessAnimSystemData() {
        this.data.playerForm = RegPlayerFormComponent.PLAYER_FORM.get(this.player).getCurrentForm();
        if (this.player.getPos().getY() == this.data.LastPosition.getY()) {
            this.data.LastPosYChange ++;
        }
        else {
            this.data.LastPosYChange = 0;
        }
        if (this.player.handSwinging) {
            this.data.ContinueSwingAnimCounter ++;
        }
        else {
            this.data.ContinueSwingAnimCounter = 0;
        }
        this.data.IsOnGround = (player.isOnGround() || (!player.getAbilities().flying && this.data.LastPosYChange > 10));
    }

    private void AfterProcessAnimSystemData() {
        this.data.LastPosition = this.player.getPos();
    }

    private void EndProcessAnimSystemData() {

    }

    private @Nullable Identifier getPowerAnimStateID() {
        if (this.player instanceof IPlayerAnimController iPlayerAnimController) {
            return iPlayerAnimController.shape_shifter_curse$getPowerAnimationStateID();
        }
        return null;
    }

    public AnimationHolder getAnimation() {
        this.PreProcessAnimSystemData();
        @Nullable Identifier powerAnimState = this.getPowerAnimStateID();
        Identifier animStateControllerID;
        if (powerAnimState == null) {
            Pair<@Nullable Identifier, @NotNull Identifier> result = this.getAnimFSM().update(this.player, this.data);
            if (result.getLeft() != null) {
                this.nowAnimFSMID = result.getLeft();
            }
            animStateControllerID = result.getRight();
        }
        else {
            animStateControllerID = powerAnimState;
        }
        this.AfterProcessAnimSystemData();
        AbstractAnimStateController animStateController = this.data.playerForm.getAnimStateController(this.player, animStateControllerID);
        if (animStateController == null) {
            AnimRegistry.AnimState resultAnimState = Objects.requireNonNull(AnimRegistry.getAnimState(animStateControllerID));
            animStateController = resultAnimState.defaultController;
        }
        if (!animStateController.isRegistered(this.player, this.data)) {
            animStateController.registerAnim(this.player, this.data);
        }
        AnimationHolder anim = animStateController.getAnimation(this.player, this.data);
        this.EndProcessAnimSystemData();
        return anim;
    }
}
