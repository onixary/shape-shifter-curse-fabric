package net.onixary.shapeShifterCurseFabric.additional_power;

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
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;

public class SneakingJumpClashPower extends Power {

    private final Consumer<Pair<Entity, Entity>> bientityAction;
    private final int checkDuration;
    private final double expansionDistance;
    
    private boolean isActive = false;
    private int activeTicks = 0;
    private boolean wasOnGround = true;

    public SneakingJumpClashPower(PowerType<?> type, LivingEntity entity,
                                  Consumer<Pair<Entity, Entity>> bientityAction,
                                 int checkDuration, double expansionDistance) {
        super(type, entity);
        this.bientityAction = bientityAction;
        this.checkDuration = checkDuration;
        this.expansionDistance = expansionDistance;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (!(entity instanceof PlayerEntity player) || entity.getWorld().isClient()) {
            return;
        }

        // 检查是否重新接触地面
        if (player.isOnGround()) {
            wasOnGround = true;
            // 如果之前处于激活状态，则重置状态
            if (isActive) {
                isActive = false;
                activeTicks = 0;
            }
        } else if (wasOnGround && player.isSneaking() && player.getVelocity().y > 0) {
            // 从地面潜行跳跃时触发
            isActive = true;
            activeTicks = 0;
            wasOnGround = false;
        }

        // 如果power处于激活状态
        if (isActive) {
            activeTicks++;
            
            // 检查是否超过持续时间
            if (activeTicks > checkDuration) {
                isActive = false;
                activeTicks = 0;
                return;
            }
            
            // 检查碰撞
            if (checkForCollision(player)) {
                isActive = false;
                activeTicks = 0;
            }
        }
    }

    private boolean checkForCollision(PlayerEntity player) {
        // 获取玩家面向方向
        Direction facing = player.getHorizontalFacing();
        Vec3d facingVec = Vec3d.of(facing.getVector());
        
        // 扩展玩家碰撞箱向前方
        Box expandedBox = player.getBoundingBox().stretch(facingVec.multiply(expansionDistance)).expand(0.5);
        
        // 查找碰撞的生物实体
        for (LivingEntity target : player.getWorld().getEntitiesByClass(
                LivingEntity.class, expandedBox, 
                e -> e != player && e.isAlive() && !e.isRemoved())) {
            
            // 触发碰撞action
            if (bientityAction != null) {
                this.bientityAction.accept(new Pair<>(player, target));
            }
            return true; // 发现碰撞，返回true
        }
        
        return false; // 未发现碰撞
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("sneaking_jump_clash"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("check_duration", SerializableDataTypes.INT, 20)
                        .add("expansion_distance", SerializableDataTypes.DOUBLE, 1.0),
                data -> (type, entity) -> new SneakingJumpClashPower(
                        type,
                        entity,
                        data.get("bientity_action"),
                        data.getInt("check_duration"),
                        data.getDouble("expansion_distance")
                )
        ).allowCondition();
    }
}
