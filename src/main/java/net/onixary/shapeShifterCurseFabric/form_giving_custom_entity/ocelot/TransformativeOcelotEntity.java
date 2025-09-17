package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.TStatusApplier;

public class TransformativeOcelotEntity extends OcelotEntity {

    public TransformativeOcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
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

    public static boolean canCustomSpawn(EntityType<TransformativeOcelotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
//        return random.nextInt(3) != 0;  // ~67% 刷新成功率
        float Chance = ShapeShifterCurseFabric.commonConfig.transformativeOcelotSpawnChance;
        if (Chance <= 0.0f) { return false; }
        if (Chance >= 1.0f) { return true; }
        return random.nextFloat() < Chance;
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
                tryAttack(player);
                // 概率施加效果
                TStatusApplier.applyStatusFromTMob(this, player);
                // 重置冷却时间
                cooldown = ATTACK_COOLDOWN;
            }
        }

        // 生成粒子效果
        if (this.getWorld().isClient) {
            for (int i = 0; i < 1; i++) {
                this.getWorld().addParticle(StaticParams.CUSTOM_MOB_DEFAULT_PARTICLE,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getY() + this.random.nextDouble() * 0.5,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0);
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        // 只对玩家造成伤害
        if(target instanceof PlayerEntity) {
            PlayerFormBase currentForm = target.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
            if (currentForm.equals(RegPlayerForms.ORIGINAL_SHIFTER)) {
                boolean attacked = target.damage(this.getDamageSources().mobAttack(this), StaticParams.CUSTOM_MOB_DEFAULT_DAMAGE);
                if (attacked) {
                    this.applyDamageEffects(this, target);
                }
                return attacked;
            }
            return false;
        }
        return false;
    }
}
