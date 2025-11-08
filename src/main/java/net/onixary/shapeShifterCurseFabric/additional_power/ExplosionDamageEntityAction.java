package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;

public class ExplosionDamageEntityAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        // 仅实现爆炸伤害实体的能力
        // 参数 power -> 威力 entity_condition -> 实体条件
        int Power = data.getInt("power");
        ConditionFactory<Entity>.Instance entityCondition = data.get("entity_condition");
        explosion(entity, Power, entityCondition);
    }

    private static void explosion(Entity entity, int power, ConditionFactory<Entity>.Instance entityCondition) {
        Vec3d ExplosionPos = entity.getPos();
        DamageSource source = entity.getWorld().getDamageSources().explosion(entity, entity);
        entity.getWorld().emitGameEvent(entity, GameEvent.EXPLODE, new Vec3d(ExplosionPos.getX(), ExplosionPos.getY(), ExplosionPos.getZ()));

        // 从net.minecraft.world.explosion.Explosion类中collectBlocksAndDamageEntities函数提取的代码
        float q = power * 2.0F;
        int k = MathHelper.floor(ExplosionPos.getX() - (double)q - 1.0);
        int l = MathHelper.floor(ExplosionPos.getX() + (double)q + 1.0);
        int r = MathHelper.floor(ExplosionPos.getY() - (double)q - 1.0);
        int s = MathHelper.floor(ExplosionPos.getY() + (double)q + 1.0);
        int t = MathHelper.floor(ExplosionPos.getZ() - (double)q - 1.0);
        int u = MathHelper.floor(ExplosionPos.getZ() + (double)q + 1.0);
        List<Entity> list = entity.getWorld().getOtherEntities(entity, new Box((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        for(int v = 0; v < list.size(); ++v) {
            Entity target_entity = (Entity) list.get(v);
            if (!target_entity.isImmuneToExplosion() && (entityCondition == null || entityCondition.test(target_entity)))  {
                double w = Math.sqrt(target_entity.squaredDistanceTo(ExplosionPos)) / (double)q;
                if (w <= 1.0) {
                    double x = target_entity.getX() - ExplosionPos.getX();
                    double y = (target_entity instanceof TntEntity ? target_entity.getY() : target_entity.getEyeY()) - ExplosionPos.getY();
                    double z = target_entity.getZ() - ExplosionPos.getZ();
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = (double) Explosion.getExposure(ExplosionPos, target_entity);
                        double ac = (1.0 - w) * ab;
                        target_entity.damage(source, (float)((int)((ac * ac + ac) / 2.0 * 7.0 * (double)q + 1.0)));
                        double ad;
                        if (target_entity instanceof LivingEntity livingEntity) {
                            ad = ProtectionEnchantment.transformExplosionKnockback(livingEntity, ac);
                        } else {
                            ad = ac;
                        }
                        x *= ad;
                        y *= ad;
                        z *= ad;
                        Vec3d vec3d2 = new Vec3d(x, y, z);
                        target_entity.setVelocity(target_entity.getVelocity().add(vec3d2));
                    }
                }
            }
        }
    }

    public static ActionFactory<Entity> createFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("explosion_damage_entity"),
                new SerializableData()
                        .add("power", SerializableDataTypes.INT, 0)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
                ExplosionDamageEntityAction::action
        );
    }
}
