package net.onixary.shapeShifterCurseFabric.player_animation;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.client.ClientPlayerStateManager;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class AnimationControllerInstance {
    // 动画控制器注册
    public static AnimationController Controller_Normal = new AnimationController((player, animDataHolder) -> PlayerAnimState.ANIM_IDLE);
    public static AnimationController Controller_Sneaking = new AnimationController((player, animDataHolder) -> PlayerAnimState.ANIM_SNEAK_IDLE);
    // 注册动画条件 越早的越优先
    public static Identifier AnimC_IsTransforming = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_is_transforming"),
            (playerEntity, animDataHolder) -> ShapeShifterCurseFabricClient.isClientTransforming(playerEntity.getUuid())
    );

    public static Identifier AnimC_Power = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_power"),
            (playerEntity, animDataHolder) -> true
    );  // 所有形态都有可能有 如果在这里检查Power会带来没必要的性能开销  如果没有Power则返回NOT_MATCH

    public static Identifier AnimC_Sleep = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_sleep"),
            (playerEntity, animDataHolder) -> playerEntity.isSleeping()
    );

    public static Identifier AnimC_Ride = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_ride"),
            (playerEntity, animDataHolder) -> playerEntity.hasVehicle()
    );  // 坐船和骑乘共用

    public static Identifier AnimC_Climb = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_climb"),
            (playerEntity, animDataHolder) -> {
                if (!playerEntity.isClimbing() || playerEntity.isOnGround() || playerEntity.getAbilities().flying || playerEntity.isFallFlying()) {
                    return false;
                }
                // 检测碰撞箱 防止出现身体与地面穿模 如果卡顿可以直接可以修改为 return true
                BlockPos down1pos = playerEntity.getBlockPos().down();
                BlockState down1block = playerEntity.getWorld().getBlockState(down1pos);
                Vec3d ClimbAnimTestPoint = playerEntity.getPos().add(0f, -0.6f, 0f);  // 检测点在身体中心下方0.6个方块是否有碰撞箱
                BlockHitResult HitResult = down1block.getCollisionShape(playerEntity.getWorld(), down1pos).raycast(playerEntity.getPos(), ClimbAnimTestPoint, down1pos);
                if (HitResult == null) { // 没有碰撞箱时
                    return true;
                }
                else {
                    return HitResult.getType() == BlockHitResult.Type.MISS;
                }
            }
    );  // 是否爬行 从PlayerEntityAnimOverrideMixin中复制CanClimbAnim

    public static Identifier AnimC_Swim = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_swim"),
            (playerEntity, animDataHolder) -> playerEntity.isTouchingWater()
    );  // 没必要判断是否奔跑

    public static Identifier AnimC_Flying = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_flying"),
            (playerEntity, animDataHolder) -> playerEntity.getAbilities().flying
    );  // 是否创造飞行

    public static Identifier AnimC_FallFlying = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_fall_flying"),
            (playerEntity, animDataHolder) -> playerEntity.isFallFlying()
    );  // 是否鞘翅飞行

    public static Identifier AnimC_Fall = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_fall"),
            (playerEntity, animDataHolder) -> !playerEntity.isOnGround() && playerEntity.getVelocity().y < 0
    );  // 与hasSlowFall合并 需要为Sneak单独实现

    public static Identifier AnimC_Jump = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_jump"),
            (playerEntity, animDataHolder) -> !playerEntity.isOnGround() && animDataHolder.lastOnGround && playerEntity.getVelocity().y > 0
    );  // 与RushJump 合并 需要为Sneak单独实现

    public static Identifier AnimC_UseItem = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_use_item"),
            (playerEntity, animDataHolder) -> playerEntity.isUsingItem()
    );  // 需要为Sneak单独实现

    public static Identifier AnimC_Mining = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_mining"),
            (playerEntity, animDataHolder) -> playerEntity.handSwinging && animDataHolder.continueSwingAnimCounter >= 10
    );  // 需要为Sneak单独实现

    public static Identifier AnimC_Attack = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_attack"),
            (playerEntity, animDataHolder) -> playerEntity.handSwinging
    );  // 由于已经在AnimC_UseTools中检查过，所以这里不需要检查continueSwingAnimCounter  需要为Sneak单独实现

    public static Identifier AnimC_Walk = AnimationController.RegisterAnimationStateCondition(
            ShapeShifterCurseFabric.identifier("anim_c_walk"),
            (playerEntity, animDataHolder) -> animDataHolder.IsWalk
    );  // 和奔跑合并 如果逻辑复杂 可以把奔跑分离  需要为Sneak单独实现

    public static void RegisterAnimCellToAllController(Identifier conditionID, BiFunction<PlayerEntity, AnimationController.PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> cell) {
        Controller_Normal.RegisterAnimControllerCell(conditionID, cell);
        Controller_Sneaking.RegisterAnimControllerCell(conditionID, cell);
    }

    public static void RegisterAnimCell() {
        RegisterAnimCellToAllController(AnimC_IsTransforming, (player, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_ON_TRANSFORM));
        RegisterAnimCellToAllController(AnimC_Power,
                (player, animDataHolder) -> {
                    AtomicReference<PlayerAnimState> currentState = new AtomicReference<>(PlayerAnimState.NONE);
                    AtomicBoolean foundLocalPower = new AtomicBoolean(false);
                    // 首先检查本地玩家的Power状态
                    PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                            .stream()
                            .filter(BatBlockAttachPower::isAttached)
                            .findFirst()
                            .ifPresent(attachPower -> {
                                if (attachPower.getAttachType() == BatBlockAttachPower.AttachType.SIDE) {
                                    currentState.set(PlayerAnimState.ANIM_ATTACH_SIDE);
                                } else if (attachPower.getAttachType() == BatBlockAttachPower.AttachType.BOTTOM) {
                                    currentState.set(PlayerAnimState.ANIM_ATTACH_BOTTOM);
                                }
                                foundLocalPower.set(true);
                            });

                    // 如果没有找到本地Power状态，检查客户端状态管理器（用于其他玩家）
                    if (!foundLocalPower.get()) {
                        ClientPlayerStateManager.PlayerAttachState otherPlayerState =
                                ClientPlayerStateManager.getPlayerAttachState(player.getUuid());
                        if (otherPlayerState != null && otherPlayerState.isAttached) {
                            if (otherPlayerState.attachType == BatBlockAttachPower.AttachType.SIDE) {
                                currentState.set(PlayerAnimState.ANIM_ATTACH_SIDE);
                            } else if (otherPlayerState.attachType == BatBlockAttachPower.AttachType.BOTTOM) {
                                currentState.set(PlayerAnimState.ANIM_ATTACH_BOTTOM);
                            }
                        }
                    }
                    if (currentState.get() == PlayerAnimState.NONE) {
                        return new Pair<>(AnimationControllerCellResult.NOT_MATCH, PlayerAnimState.NONE);
                    } else {
                        return new Pair<>(AnimationControllerCellResult.MATCH, currentState.get());
                    }
                }
        );  // 从PlayerEntityAnimOverrideMixin 中复制过来的
        RegisterAnimCellToAllController(AnimC_Sleep, (player, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SLEEP));
        RegisterAnimCellToAllController(AnimC_Ride, ((playerEntity, animDataHolder) -> {
            if (playerEntity.getVehicle() instanceof BoatEntity) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_BOAT_IDLE);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_RIDE_IDLE);
        }));
        RegisterAnimCellToAllController(AnimC_Climb, ((playerEntity, animDataHolder) -> {
            if (playerEntity.getVelocity().y > 0)
            {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_CLIMB);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_CLIMB_IDLE);
        }));
        RegisterAnimCellToAllController(AnimC_Swim, (playerEntity, animDataHolder) -> {
            if (playerEntity.isSwimming()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SWIM);
            }
            // 我认为应该修改为检测移动 或者在默认动画中设置 Sneak情况下应该会出BUG 毕竟这个动作没有进行游泳 有可能会出现Sneak情况
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SWIM_IDLE);
        });
        RegisterAnimCellToAllController(AnimC_Flying, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_CREATIVE_FLY));
        RegisterAnimCellToAllController(AnimC_FallFlying, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_ELYTRA_FLY));
        Controller_Normal.RegisterAnimControllerCell(AnimC_Fall, (playerEntity, animDataHolder) -> {
            if (animDataHolder.playerForm.getHasSlowFall()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SLOW_FALL);
            } else if (playerEntity.fallDistance > 0.6f) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_FALL);
            }
            return new Pair<>(AnimationControllerCellResult.NOT_MATCH, PlayerAnimState.NONE);
        });
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_Fall, (playerEntity, animDataHolder) -> {
            if (animDataHolder.playerForm.getHasSlowFall()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SLOW_FALL);
            } else if (playerEntity.fallDistance > 0.6f) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_FALL);
            }
            return new Pair<>(AnimationControllerCellResult.NOT_MATCH, PlayerAnimState.NONE);
        });
        Controller_Normal.RegisterAnimControllerCell(AnimC_Jump, (playerEntity, animDataHolder) -> {
            if ((Math.abs(playerEntity.getVelocity().z) > 0.15 || Math.abs(playerEntity.getVelocity().x) > 0.15) && animDataHolder.playerForm.getCanSneakRush()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_RUSH_JUMP);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_JUMP);
        });
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_Jump, (playerEntity, animDataHolder) -> {
            if ((Math.abs(playerEntity.getVelocity().z) > 0.15 || Math.abs(playerEntity.getVelocity().x) > 0.15) && animDataHolder.playerForm.getCanSneakRush()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_RUSH_JUMP);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_JUMP);
        });
        Controller_Normal.RegisterAnimControllerCell(AnimC_UseItem, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_IDLE));
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_UseItem, (playerEntity, animDataHolder) -> {
            // 需要新的动画 类似潜行行走但腿不动 由于ANIM_SNEAK_IDLE是坐下的动画 所以这里需要新的动画
            if (animDataHolder.IsWalk) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_WALK);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_IDLE);
        });
        Controller_Normal.RegisterAnimControllerCell(AnimC_Mining, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_TOOL_SWING));
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_Mining, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_TOOL_SWING));
        Controller_Normal.RegisterAnimControllerCell(AnimC_Attack, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_ATTACK_ONCE));
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_Attack, (playerEntity, animDataHolder) -> new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_ATTACK_ONCE));
        Controller_Normal.RegisterAnimControllerCell(AnimC_Walk, (playerEntity, animDataHolder) -> {
            if (playerEntity.isSprinting()) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_RUN);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_WALK);
        });
        Controller_Sneaking.RegisterAnimControllerCell(AnimC_Walk, (playerEntity, animDataHolder) -> {
            if (animDataHolder.playerForm.getCanSneakRush() && playerEntity.getHungerManager().getFoodLevel() >= 6) {
                return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_RUSH);
            }
            return new Pair<>(AnimationControllerCellResult.MATCH, PlayerAnimState.ANIM_SNEAK_WALK);
        });
    }

    // 使用这个方法来获取动画 animDataHolder需要在Mixin中存储 如果没有直接new一个
    public static AnimationHolder getAnim(PlayerEntity playerEntity, AnimationController.PlayerAnimDataHolder animDataHolder) {
        if (playerEntity.isSneaking()) {
            return Controller_Sneaking.getAnim(playerEntity, animDataHolder);
        }
        else {
            return Controller_Normal.getAnim(playerEntity, animDataHolder);
        }
    }
}
