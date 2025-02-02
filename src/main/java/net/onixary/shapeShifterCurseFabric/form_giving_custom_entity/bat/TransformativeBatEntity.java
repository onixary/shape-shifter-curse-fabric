package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;

import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;

public class TransformativeBatEntity extends BatEntity {

    public TransformativeBatEntity(EntityType<? extends BatEntity> entityType, World world) {
        super(entityType, world);
    }
    // 20 ticks = 1 second
    private static final float ATTACK_COOLDOWN = 100.0F;

    // 当前冷却时间
    private float cooldown = 0;

    @Override
    protected void initGoals() {
        super.initGoals();
        // 添加攻击目标（玩家）
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        // 更新冷却时间
        if (cooldown > 0) {
            cooldown--;
        }

        // 检查是否有目标玩家
        LivingEntity target = this.getTarget();
        if (target instanceof PlayerEntity && cooldown <= 0) {
            PlayerEntity player = (PlayerEntity) target;

            // 计算与玩家的距离
            double distance = this.squaredDistanceTo(player);
            if (distance <= StaticParams.CUSTOM_MOB_DEFAULT_ATTACK_RANGE * StaticParams.CUSTOM_MOB_DEFAULT_ATTACK_RANGE) {
                // 对玩家造成伤害
                player.damage(this.getDamageSources().mobAttack(this), StaticParams.CUSTOM_MOB_DEFAULT_DAMAGE);
                // 重置冷却时间
                cooldown = ATTACK_COOLDOWN;
            }
        }

        // 生成粒子效果
        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; i++) {
                this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getY() + this.random.nextDouble() * 1.5,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        0, 0.5, 0);
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        // 对目标造成伤害
        boolean attacked = target.damage(this.getDamageSources().mobAttack(this), StaticParams.CUSTOM_MOB_DEFAULT_DAMAGE);
        if (attacked) {
            this.applyDamageEffects(this, target);
        }
        return attacked;
    }
}
