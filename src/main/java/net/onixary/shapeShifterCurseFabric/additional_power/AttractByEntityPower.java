package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class AttractByEntityPower extends Power{

    private final Predicate<Entity> entityCondition;
    private final ActionFactory<Entity>.Instance entityAction;
    private final ActionFactory<Entity>.Instance selfAction;
    private final float attractionSpeed;
    private final float attractionRadius;
    private final float stopRadius;
    private final float escapeAttractionSpeed;
    private final float escapeAngleThreshold;

    private Entity targetEntity;

    public AttractByEntityPower(PowerType<?> type, LivingEntity entity,
                                Predicate<Entity> entityCondition,
                                ActionFactory<Entity>.Instance entityAction,
                                ActionFactory<Entity>.Instance selfAction,
                                float attractionSpeed,
                                float attractionRadius,
                                float stopRadius,
                                float escapeAttractionSpeed,
                                float escapeAngleThreshold) {
        super(type, entity);
        this.entityCondition = entityCondition;
        this.entityAction = entityAction;
        this.selfAction = selfAction;
        this.attractionSpeed = attractionSpeed;
        this.attractionRadius = attractionRadius;
        this.stopRadius = stopRadius;
        this.escapeAttractionSpeed = escapeAttractionSpeed;
        this.escapeAngleThreshold = escapeAngleThreshold;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (!(entity instanceof PlayerEntity player) || player.isSpectator()) {
            return;
        }

        // 1. 检测范围内的所有实体
        Box searchBox = Box.from(player.getPos()).expand(attractionRadius);
        List<Entity> entities = player.getWorld().getOtherEntities(
                player,
                searchBox,
                e -> entityCondition.test(e)
        );

        // 2. 找到最近的符合条件的实体
        targetEntity = entities.stream()
                .filter(entity -> entity.isAlive() && !entity.isSpectator())
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)))
                .orElse(null);

        // 如果目标实体存在且距离小于停止半径，则不进行吸引
        if (targetEntity != null && player.squaredDistanceTo(targetEntity) < stopRadius * stopRadius) {
            targetEntity = null; // 重置目标实体
        }

        if (isPlayerInVehicle(player) || !player.isOnGround()) {
            targetEntity = null; // 清除目标
            return;
        }

        // 3. 应用吸引效果
        if (targetEntity != null) {
            Vec3d attractDirection = targetEntity.getPos()
                    .subtract(player.getPos())
                    .multiply(1, 0, 1) // 忽略Y轴
                    .normalize();

            // 计算当前速度方向
            Vec3d currentVelocity = player.getVelocity();
            Vec3d horizontalVelocity = new Vec3d(currentVelocity.x, 0, currentVelocity.z);
            // 计算角色面朝向
            Vec3d playerFacing = player.getRotationVector().multiply(1, 0, 1).normalize();

            // 计算实际应用的吸引力
            float effectiveSpeed = calculateEffectiveSpeed(attractDirection, horizontalVelocity, playerFacing);

            // 应用速度
            Vec3d finalVelocity = attractDirection.multiply(effectiveSpeed).add(0, currentVelocity.y, 0);
            player.setVelocity(finalVelocity);
            player.velocityModified = true;

            // 4. 执行实体动作（如果存在）
            if (entityAction != null) {
                entityAction.accept(targetEntity);
            }

            // 5. 执行自身动作（如果存在）
            if (selfAction != null) {
                selfAction.accept(player);
            }

            // 6. 同步状态
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }

    // 检查玩家是否在载具上
    private boolean isPlayerInVehicle(PlayerEntity player) {
        Entity vehicle = player.getVehicle();

        // 检查所有已知载具类型
        return vehicle != null && (
                vehicle instanceof BoatEntity ||
                        vehicle instanceof MinecartEntity ||
                        // 支持其他模组载具
                        vehicle.getType().getUntranslatedName().contains("vehicle") ||
                        vehicle.getType().getUntranslatedName().contains("mount")
        );
    }

    // 计算实际应用的吸引力速度（考虑逃脱机制）
    private float calculateEffectiveSpeed(Vec3d attractDirection, Vec3d playerVelocity, Vec3d playerFacing) {


        Vec3d faceDirection = playerFacing.normalize();

        double dotProduct = faceDirection.dotProduct(attractDirection);

        double angle = Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));

        // 5. 判断是否在逃脱状态（夹角大于阈值）
        if (dotProduct < 0) {
            // 应用逃脱衰减
            return escapeAttractionSpeed;
        }

        return attractionSpeed;
    }


    @Override
    public void onAdded() {
        super.onAdded();
        // 初始化时重置目标
        targetEntity = null;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        // 移除时重置目标
        targetEntity = null;
    }

    // 获取当前目标实体（可用于其他逻辑）
    public Entity getTargetEntity() {
        return targetEntity;
    }

    // 工厂方法
    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(
                Apoli.identifier("attract_by_entity"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("attraction_speed", SerializableDataTypes.FLOAT, 0.1f)
                        .add("attraction_radius", SerializableDataTypes.FLOAT, 8.0f)
                        .add("stop_radius", SerializableDataTypes.FLOAT, 1.0f)
                        .add("escape_attraction_speed", SerializableDataTypes.FLOAT, 0.025f)
                        .add("escape_angle", SerializableDataTypes.FLOAT, (float) Math.toRadians(80)),
                data -> (powerType, entity) -> new AttractByEntityPower(
                        powerType,
                        entity,
                        data.get("entity_condition"),
                        data.get("entity_action"),
                        data.get("self_action"),
                        data.getFloat("attraction_speed"),
                        data.getFloat("attraction_radius"),
                        data.getFloat("stop_radius"),
                        data.getFloat("escape_attraction_speed"),
                        data.getFloat("escape_angle")
                )
        ).allowCondition();
    }
}
