package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

public class TransformativeWolfEntity extends WolfEntity {
    public TransformativeWolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
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

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    public static boolean canCustomSpawn(EntityType<TransformativeWolfEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos NowCheckPos = pos;
        // 脚下如果藏有TNT 则不生成 防止沙漠神殿自爆
        for (int i = 0; i < 5; i++) {
            if (world.getBlockState(NowCheckPos).getBlock() == Blocks.TNT) {
                return false;
            }
            NowCheckPos = NowCheckPos.down();
        }
        float Chance = ShapeShifterCurseFabric.commonConfig.transformativeWolfSpawnChance;
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

    // 禁止与此生物交互 防止使用Wolf的驯服逻辑
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void setOwner(PlayerEntity player) {
        return;
    }
}