package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.onixary.shapeShifterCurseFabric.additional_power.BatAttachEventHandler.getBatAttachPower;

public class BatBlockAttachPower extends Power {

    private final Predicate<CachedBlockPosition> blockCondition;
    private final Consumer<LivingEntity> sideAttachAction;
    private final Consumer<LivingEntity> bottomAttachAction;
    private final int bottomAttachInterval;

    // 吸附状态
    private boolean isAttached = false;
    private AttachType attachType = AttachType.NONE;
    private BlockPos attachedBlockPos = null;
    private Direction attachedSide = null;
    private int bottomAttachTimer = 0;

    public enum AttachType {
        NONE, SIDE, BOTTOM
    }

    public BatBlockAttachPower(PowerType<?> type, LivingEntity entity,
                               Predicate<CachedBlockPosition> blockCondition,
                               Consumer<LivingEntity> sideAttachAction,
                               Consumer<LivingEntity> bottomAttachAction,
                               int bottomAttachInterval) {
        super(type, entity);
        this.blockCondition = blockCondition;
        this.sideAttachAction = sideAttachAction;
        this.bottomAttachAction = bottomAttachAction;
        this.bottomAttachInterval = bottomAttachInterval;
        this.setTicking(true);
        this.shouldTickWhenInactive();
    }

    public static void syncClientState(PlayerEntity player, boolean isAttached, int attachTypeOrdinal,
                                       BlockPos attachedPos, Direction attachedSide) {
        // 获取玩家的BatBlockAttachPower实例
        BatBlockAttachPower power = getBatAttachPower(player);
        if (power != null) {
            power.isAttached = isAttached;
            power.attachType = AttachType.values()[attachTypeOrdinal];
            power.attachedBlockPos = attachedPos;
            power.attachedSide = attachedSide;
            power.bottomAttachTimer = 0;

            // 如果是吸附状态，立即设置客户端位置
            if (isAttached && attachedPos != null && attachedSide != null) {
                Vec3d targetPos;
                if (power.attachType == AttachType.SIDE) {
                    targetPos = Vec3d.ofCenter(attachedPos).add(Vec3d.of(attachedSide.getVector()).multiply(0.75d)).add(0, -0.5, 0);
                } else {
                    targetPos = Vec3d.ofCenter(attachedPos).add(0, -1.5, 0);
                }

                player.setPosition(targetPos.x, targetPos.y, targetPos.z);
                player.setVelocity(Vec3d.ZERO);
                //System.out.println("Debug: CLIENT position synced to " + targetPos);
            }

            //System.out.println("Debug: Client state synced - isAttached=" + isAttached +
            //        ", attachType=" + power.attachType);
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // 只在服务端处理方块检查和detach逻辑
        if (!player.getWorld().isClient()) {
            if (isAttached && attachedBlockPos != null) {
                World world = entity.getWorld();
                BlockState blockState = world.getBlockState(attachedBlockPos);

                //System.out.println("Debug: SERVER Tick checking block at " + attachedBlockPos + ", isAir: " + blockState.isAir());

                if (blockState.isAir()) {
                    //System.out.println("Debug: SERVER Block is air, force detaching");
                    detach(player,false);
                    return;
                }
            }

            // 服务端处理定时动作
            if (isAttached && attachType == AttachType.BOTTOM && bottomAttachAction != null) {
                bottomAttachTimer++;
                if (bottomAttachTimer >= bottomAttachInterval) {
                    bottomAttachAction.accept(entity);
                    bottomAttachTimer = 0;
                }
            }
        }

        // 客户端和服务端都执行位置维持
        if (isAttached) {
            maintainAttachPosition(player);

            if (attachType == AttachType.SIDE && attachedSide != null) {
                maintainFacingDirection(player);
            }
        }
    }

    public boolean tryAttach(PlayerEntity player, BlockHitResult hitResult) {
        //System.out.println("Debug: tryAttach called - isAttached=" + isAttached +
        //        ", hasMainHandItem=" + !player.getMainHandStack().isEmpty());

        if (isAttached || !player.getMainHandStack().isEmpty() || player.isOnGround()) {
            return false;
        }

        BlockPos blockPos = hitResult.getBlockPos();

        if (blockCondition != null && !blockCondition.test(new CachedBlockPosition(entity.getWorld(), blockPos, true))) {
            //System.out.println("Debug: Block condition failed");
            return false;
        }

        Direction hitSide = hitResult.getSide();

        boolean attached = false;
        if (hitSide == Direction.DOWN) {
            //System.out.println("Debug: Attaching to bottom");
            attachToBottom(player, blockPos);
            attached = true;
        } else if (hitSide.getAxis().isHorizontal()) {
            //System.out.println("Debug: Attaching to side");
            attachToSide(player, blockPos, hitSide);
            attached = true;
        }

        // 在设置位置后发送同步包
        if (attached && !player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            // 添加小延迟确保位置设置生效
            player.getWorld().getServer().execute(() -> {
                ModPacketsS2C.sendBatAttachState(serverPlayer, isAttached, attachType.ordinal(),
                        attachedBlockPos, attachedSide);
                //System.out.println("Debug: Sent sync packet to client");
            });
        }

        return attached;
    }

    private void attachToSide(PlayerEntity player, BlockPos blockPos, Direction side) {
        isAttached = true;
        attachType = AttachType.SIDE;
        attachedBlockPos = blockPos;
        attachedSide = side;

        // 立即在服务端设置位置
        Vec3d attachPos = Vec3d.ofCenter(blockPos).add(Vec3d.of(side.getVector()).multiply(0.75d)).add(0, -0.5, 0);
        player.setPosition(attachPos.x, attachPos.y, attachPos.z);
        player.setVelocity(Vec3d.ZERO);
        player.setOnGround(true);
        player.velocityDirty = true;
        player.velocityModified = true;

    }

    private void attachToBottom(PlayerEntity player, BlockPos blockPos) {
        isAttached = true;
        attachType = AttachType.BOTTOM;
        attachedBlockPos = blockPos;
        attachedSide = Direction.DOWN;
        bottomAttachTimer = 0;

        // 立即在服务端设置位置
        Vec3d attachPos = Vec3d.ofCenter(blockPos).add(0, -1.5f, 0);
        player.setPosition(attachPos.x, attachPos.y, attachPos.z);
        player.setVelocity(Vec3d.ZERO);
        player.setOnGround(true);
        player.velocityDirty = true;
        player.velocityModified = true;
    }


    public void detach(PlayerEntity player, boolean isByJump) {
        if (!isAttached) {
            return;
        }

        //System.out.println("Debug: detached on SERVER");

        // 如果是侧面吸附并且有跳跃动作，执行动作
        if (attachType == AttachType.SIDE && sideAttachAction != null) {
            sideAttachAction.accept(entity);
        }

        // 重置状态
        isAttached = false;
        AttachType oldAttachType = attachType;
        attachType = AttachType.NONE;
        attachedBlockPos = null;
        attachedSide = null;
        bottomAttachTimer = 0;

        // 重置物理状态
        player.setOnGround(false);
        player.setVelocity(Vec3d.ZERO);
        player.addVelocity(0, 0.2, 0);
        if(isByJump){
            // 获取玩家面向方向的水平向量
            float yaw = player.getYaw();
            double yawRadians = Math.toRadians(yaw);

            // 计算水平方向向量（忽略Y轴）
            double dirX = -Math.sin(yawRadians);
            double dirZ = Math.cos(yawRadians);

            // 设置推进速度（可以调整0.5这个数值来改变推进力度）
            double jumpSpeed = 1.25f;
            player.addVelocity(dirX * jumpSpeed, 0, dirZ * jumpSpeed);
        }
        player.velocityDirty = true;
        player.velocityModified = true;

        // 同步到客户端
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPacketsS2C.sendBatAttachState(serverPlayer, false, AttachType.NONE.ordinal(), null, null);
        }
    }


    private void maintainAttachPosition(PlayerEntity player) {
        if (attachedBlockPos == null || attachedSide == null) {
            return;
        }

        Vec3d targetPos;
        if (attachType == AttachType.SIDE) {
            targetPos = Vec3d.ofCenter(attachedBlockPos).add(Vec3d.of(attachedSide.getVector()));
        } else {
            targetPos = Vec3d.ofCenter(attachedBlockPos).add(0, -1.5f, 0);
        }

        // 类似蜂蜜块的完全停止效果
        Vec3d currentVelocity = player.getVelocity();

        // 完全重置速度，类似蜘蛛网效果
        player.setVelocity(0, 0, 0);

        // 强制设置位置
        player.setPosition(targetPos.x, targetPos.y, targetPos.z);

        // 模拟蜂蜜块的粘性效果
        player.slowMovement(player.getBlockStateAtPos(), new Vec3d(0.0, 0.0, 0.0));

        // 设置为在地面状态避免摔落伤害
        player.setOnGround(true);
        player.fallDistance = 0;

        // 直接操作移动相关字段，类似原版的处理方式
        player.horizontalSpeed = 0;
        player.distanceTraveled = 0;
        player.speed = 0;

        // 重置移动输入相关字段
        player.sidewaysSpeed = 0;
        player.upwardSpeed = 0;
        player.forwardSpeed = 0;

        // 标记速度已变更
        player.velocityDirty = true;
        player.velocityModified = true;

    }

    private void maintainFacingDirection(PlayerEntity player) {
        if (attachedSide == null) {
            return;
        }

        // 计算面向吸附表面的角度
        float targetYaw = getTargetYaw();

        player.setBodyYaw(targetYaw);
        player.prevBodyYaw = targetYaw;
    }

    // Getter 方法
    public float getTargetYaw() {
        if (attachedSide == null) {
            return entity.getYaw();
        }
        return switch (attachedSide) {
            case NORTH -> 0.0f;
            case SOUTH -> 180.0f;
            case WEST -> -90.0f;
            case EAST -> 90.0f;
            default -> entity.getYaw();
        };
    }

    public boolean isAttached() {
        return isAttached;
    }

    public AttachType getAttachType() {
        return attachType;
    }

    public BlockPos getAttachedBlockPos() {
        return attachedBlockPos;
    }

    public Direction getAttachedSide() {
        return attachedSide;
    }

    // 用于处理跳跃取消吸附
    public void handleJump(PlayerEntity player) {
        if (isAttached) {
            detach(player, true);
        }
    }

    // 用于处理右键取消吸附
    public void handleRightClick(PlayerEntity player) {
        if (isAttached) {
            detach(player, false);
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("bat_block_attach"),
                new SerializableData()
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("side_attach_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bottom_attach_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bottom_attach_interval", SerializableDataTypes.INT, 20),
                data -> (type, entity) -> new BatBlockAttachPower(
                        type,
                        entity,
                        data.get("block_condition"),
                        data.get("side_attach_action"),
                        data.get("bottom_attach_action"),
                        data.getInt("bottom_attach_interval"))
        ).allowCondition();
    }
}