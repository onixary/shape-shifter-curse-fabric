package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ITMob;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_AXOLOTL_0_EFFECT;

public class TransformativeAxolotlEntity extends AxolotlEntity implements Bucketable, ITMob {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super AxolotlEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, TAxolotlEntitySensor.T_AXOLOTL_ENTITY_SENSOR, SensorType.AXOLOTL_TEMPTATIONS);;

    public TransformativeAxolotlEntity(EntityType<? extends AxolotlEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, StaticParams.CUSTOM_MOB_DEFAULT_DAMAGE)  // 不改成1点伤害不行 无法攻击
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0);
    }

    public static boolean canCustomSpawn(EntityType<TransformativeAxolotlEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        // ~100% 刷新成功率
        float Chance = ShapeShifterCurseFabric.commonConfig.transformativeAxolotlSpawnChance;
        if (Chance <= 0.0f) { return false; }
        if (Chance >= 1.0f) { return true; }
        if (random.nextFloat() < Chance) { return true; }
        return canSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }

    public ItemStack getBucketItem() {
        return new ItemStack(RegCustomItem.TRANSFORMATIVE_AXOLOTL_BUCKET);
    }

    @Override
    public float getStatusChance() {
        return 0.7f;
    }

    @Override
    public BaseTransformativeStatusEffect getStatusEffect() {
        return TO_AXOLOTL_0_EFFECT;
    }

    @Override
    public void tick() {
        super.tick();
        this.TMob_Tick(this);
    }

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // 在applyStatusByChance里面已经判断形态了 无需在外面判断
        if (target instanceof PlayerEntity player) {
            ITMob.applyStatusByChance(this.getStatusChance(), player, this.getStatusEffect());
        }
    }

    @Override
    protected Brain.Profile<AxolotlEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }
}
