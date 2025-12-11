package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;

public class FSMUtils {
    public static @Nullable Identifier ProcessUniversalAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (player.isSleeping()) {
            return ANIM_STATE_SLEEP;
        }
        if (player.hasVehicle()) {
            Entity Vehicle = player.getVehicle();
            if (Vehicle instanceof BoatEntity || Vehicle instanceof MinecartEntity)  {
                return ANIM_STATE_RIDE_VEHICLE;
            }
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
