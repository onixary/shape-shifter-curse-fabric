package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;
import java.util.function.Predicate;

public class ArrowDodgePower extends Power {
    private final ActionFactory<Entity>.Instance action;
    private final Predicate<Entity> entityCondition;
    private final double range;
    private final double dodgeSpeed;
    private final double triggerDistance;
    private final int cooldown;
    private int timer;
    private boolean dodgeRight;

    public ArrowDodgePower(PowerType<?> type, LivingEntity entity,
                           ActionFactory<Entity>.Instance action,
                           Predicate<Entity> entityCondition,
                           double range, double dodgeSpeed,
                           double triggerDistance, int cooldown) {
        super(type, entity);
        this.action = action;
        this.entityCondition = entityCondition;
        this.range = range;
        this.dodgeSpeed = dodgeSpeed;
        this.triggerDistance = triggerDistance;
        this.cooldown = cooldown;
        this.timer = 0;
        this.dodgeRight = true;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (!(this.entity instanceof PlayerEntity player) || player.isSpectator()) return;
        if (player.getWorld().isClient()) return;

        if(!entityCondition.test(player)) {return;}

        // 获取玩家周围的箭矢，只在水平方向扩展检测范围
        List<PersistentProjectileEntity> arrows = player.getWorld()
                .getEntitiesByClass(PersistentProjectileEntity.class,
                        player.getBoundingBox().expand(range),
                        arrow -> {
                            // 排除玩家自己的箭矢
                            if (arrow.getOwner() == player) {
                                return false;
                            }

                            // 使用多重条件检查箭矢是否在飞行中
                            if (arrow.isOnGround()) {
                                return false;
                            }

                            // 检查inGround字段(如果可能通过Mixin访问)
                            // 如果无法通过Mixin，则跳过此检查

                            // 检查箭矢的移动是否实际发生
                            Vec3d prevPos = arrow.prevX != 0 ? new Vec3d(arrow.prevX, arrow.prevY, arrow.prevZ) : null;
                            if (prevPos != null) {
                                double movement = prevPos.distanceTo(arrow.getPos());
                                if (movement < 0.1) {
                                    return false; // 箭矢没有移动，可能已经停止
                                }
                            }

                            //ShapeShifterCurseFabric.LOGGER.info("飞行中箭矢: " + arrow.getUuid());
                            return true;
                        });

        if (timer > 0) {
            timer--;
        }

        for (PersistentProjectileEntity arrow : arrows) {
            // 检查箭矢是否朝玩家飞来
            if (isArrowApproaching(arrow, player)) {
                // 执行躲避
                if (timer <= 0) {
                    performDodge(player, arrow);
                    timer = cooldown; // 设置冷却
                }
                break; // 一次只躲避一支箭
            }
        }
    }

    private boolean isArrowApproaching(PersistentProjectileEntity arrow, PlayerEntity player) {
        // 检查距离是否在触发范围内
        double distance = arrow.getPos().distanceTo(player.getPos());
        if (distance > triggerDistance) return false;

        // 计算箭矢到玩家的方向向量
        Vec3d toPlayer = player.getPos().subtract(arrow.getPos()).normalize();

        // 获取箭矢的速度向量并归一化
        Vec3d arrowVelocity = arrow.getVelocity().normalize();

        // 使用点积判断箭矢是否朝向玩家（值越大，方向越接近）
        double dot = toPlayer.dotProduct(arrowVelocity);

        // 如果点积大于0.7，表示箭矢大致朝向玩家（夹角小于约45度）
        return dot > 0.7;
    }

    private void performDodge(PlayerEntity player, PersistentProjectileEntity arrow) {
        // 获取箭矢速度的水平分量
        Vec3d arrowVel = arrow.getVelocity();
        Vec3d horizontalVel = new Vec3d(arrowVel.x, 0, arrowVel.z).normalize();

        // 随机选择躲避方向
        dodgeRight = !dodgeRight; // 交替方向，避免单一方向

        // 计算垂直方向向量（左右）
        Vec3d dodgeDirection;
        if (dodgeRight) {
            dodgeDirection = new Vec3d(-horizontalVel.z, 0, horizontalVel.x).normalize();
        } else {
            dodgeDirection = new Vec3d(horizontalVel.z, 0, -horizontalVel.x).normalize();
        }
        ShapeShifterCurseFabric.LOGGER.info("正在躲避箭矢: " + arrow.getUuid());
        // 应用躲避速度
        player.addVelocity(dodgeDirection.multiply(dodgeSpeed));
        player.velocityModified = true;
        if (action != null) {
            action.accept(player);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("arrow_dodge"),
                new SerializableData()
                        .add("action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("range", SerializableDataTypes.DOUBLE, 5.0)
                        .add("dodge_speed", SerializableDataTypes.DOUBLE, 1.0)
                        .add("trigger_distance", SerializableDataTypes.DOUBLE, 4.0)
                        .add("cooldown", SerializableDataTypes.INT, 20),
                data -> (powerType, entity) -> new ArrowDodgePower(
                        powerType,
                        entity,
                        data.get("action"),
                        data.get("entity_condition"),
                        data.getDouble("range"),
                        data.getDouble("dodge_speed"),
                        data.getDouble("trigger_distance"),
                        data.getInt("cooldown")
                )
        ).allowCondition();
    }
}

