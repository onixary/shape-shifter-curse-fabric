package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController.IdleStayAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;

public class FSMUtils {
    public static @Nullable Identifier ProcessUniversalAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (player.isSleeping()) {
            return ANIM_STATE_SLEEP;
        }
        if (player.hasVehicle()) {
            return ANIM_STATE_RIDE;
        }
        if (!animSystemData.IsOnGround && IsPlayerClimbing(player, animSystemData)) {
            return ANIM_STATE_CLIMB;
        }
        if (player.isTouchingWater()) {
            return ANIM_STATE_SWIM;
        }
        return null;
    }

    public static @Nullable IdleStayAnimController GetIdleStayController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        AbstractAnimStateController idleController = animSystemData.playerForm.getAnimStateController(
                player,
                animSystemData,
                AnimRegistries.ANIM_STATE_IDLE
        );
        if (idleController instanceof IdleStayAnimController idleStayController) {
            return idleStayController;
        }
        return null;
    }

    // 判定玩家是否处于"静止Idle"状态(在地面且无任何动作) 用于Idle停留动画 潜行时不触发
    public static boolean IsIdleStayCondition(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        // 只为实际配置了停留动画的形态计时，避免从其他形态继承已经累计的静止时间
        if (GetIdleStayController(player, animSystemData) == null) {
            return false;
        }
        if (!animSystemData.IsOnGround) {
            return false;
        }
        if (ProcessUniversalAnim(player, animSystemData) != null) {  // Sleep/Ride/Climb/Swim
            return false;
        }
        if (player.isCrawling()) {
            return false;
        }
        if (player.isSneaking()) {  // 潜行时不触发停留动画
            return false;
        }
        if (animSystemData.IsWalking) {
            return false;
        }
        if (player.isUsingItem() || player.handSwinging) {
            return false;
        }
        return true;
    }

    // 祖传代码 从第1代修改攀爬条件时就开始使用了 从v2上复制的
    public static boolean IsPlayerClimbing(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (!player.isClimbing() || player.isOnGround() || player.getAbilities().flying || player.isFallFlying()) {
            return false;
        }
        // 检测碰撞箱 防止出现身体与地面穿模 如果卡顿可以直接可以修改为 return true
        BlockPos down1pos = player.getBlockPos().down();
        BlockState down1block = player.getWorld().getBlockState(down1pos);
        Vec3d ClimbAnimTestPoint = player.getPos().add(0f, -0.6f, 0f);  // 检测点在身体中心下方0.6个方块是否有碰撞箱
        BlockHitResult HitResult = down1block.getCollisionShape(player.getWorld(), down1pos).raycast(player.getPos(), ClimbAnimTestPoint, down1pos);
        if (HitResult == null) { // 没有碰撞箱时
            return true;
        }
        else {
            return HitResult.getType() == BlockHitResult.Type.MISS;
        }
    }
}
