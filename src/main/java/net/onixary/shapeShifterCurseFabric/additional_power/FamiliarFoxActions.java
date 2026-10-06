package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.FamiliarFoxContent;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FamiliarFoxActions {
    public static void register() {
        net.minecraft.registry.Registry.register(io.github.apace100.apoli.registry.ApoliRegistries.ENTITY_CONDITION,
                ShapeShifterCurseFabric.identifier("blocking"), new io.github.apace100.apoli.power.factory.condition.ConditionFactory<Entity>(
                        ShapeShifterCurseFabric.identifier("blocking"), new SerializableData(),
                        (data, entity) -> entity instanceof LivingEntity living && living.isBlocking()));
        AdditionalEntityActions.registerBIAction(new ActionFactory<Pair<Entity, Entity>>(
                ShapeShifterCurseFabric.identifier("fireball_at_target"), new SerializableData(), (data, pair) -> {
            if (!(pair.getLeft() instanceof LivingEntity actor) || !(actor.getWorld() instanceof ServerWorld world)) return;
            Vec3d direction = pair.getRight().getBoundingBox().getCenter().subtract(actor.getEyePos()).normalize();
            var fireball = new SmallFireballEntity(world, actor, direction.x, direction.y, direction.z);
            fireball.setPosition(actor.getEyePos().add(direction.multiply(0.6)));
            world.spawnEntity(fireball);
        }));
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("surface_area"),
                new SerializableData().add("distance", SerializableDataTypes.DOUBLE, 20.0)
                        .add("radius", SerializableDataTypes.DOUBLE, 4.0)
                        .add("preview", SerializableDataTypes.BOOLEAN, false)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null), FamiliarFoxActions::surfaceArea));
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("summon_mana_reservoir"),
                new SerializableData().add("food_cost", SerializableDataTypes.INT, 6), (data, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player) || !(player instanceof IPlayerEntityMinion minions)) return;
            int cost = data.getInt("food_cost");
            if (player.getHungerManager().getFoodLevel() < cost
                    || minions.shape_shifter_curse$getMinionsCount(ShapeShifterCurseFabric.identifier("mana_reservoir")) >= 1) return;
            var reservoir = FamiliarFoxContent.MANA_RESERVOIR.create(player.getServerWorld());
            if (reservoir == null) return;
            Vec3d position = player.getEyePos().add(player.getRotationVec(1).multiply(1.5));
            reservoir.setPosition(position);
            if (!player.getWorld().isSpaceEmpty(reservoir, reservoir.getBoundingBox())) return;
            reservoir.setOwner(player);
            if (player.getServerWorld().spawnEntity(reservoir)) {
                reservoir.InitMinion(player);
                player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel() - cost);
                player.getHungerManager().setSaturationLevel(Math.min(player.getHungerManager().getSaturationLevel(), player.getHungerManager().getFoodLevel()));
            }
        }));
    }

    private static void surfaceArea(SerializableData.Instance data, Entity actor) {
        if (!(actor.getWorld() instanceof ServerWorld world)) return;
        Vec3d eye = actor.getEyePos();
        var hit = world.raycast(new RaycastContext(eye, eye.add(actor.getRotationVec(1).multiply(data.getDouble("distance"))),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, actor));
        if (hit.getType() != HitResult.Type.BLOCK) return;
        Vec3d center = hit.getPos().add(Vec3d.of(hit.getSide().getVector()).multiply(0.05));
        if (data.getBoolean("preview")) {
            world.spawnParticles(ParticleTypes.FLAME, center.x, center.y, center.z, 2, 0.08, 0.08, 0.08, 0);
            return;
        }
        double radius = data.getDouble("radius");
        if (radius <= 0) return;
        Predicate<Pair<Entity, Entity>> condition = data.get("bientity_condition");
        Consumer<Pair<Entity, Entity>> action = data.get("bientity_action");
        for (var target : world.getEntitiesByClass(LivingEntity.class, new Box(center, center).expand(radius),
                target -> target != actor && target.isAlive() && !target.isSpectator() && target.squaredDistanceTo(center) <= radius * radius)) {
            var pair = new Pair<Entity, Entity>(actor, target);
            if (action != null && (condition == null || condition.test(pair))) action.accept(pair);
        }
        for (int i = 0; i < 64; i++) {
            double angle = i * Math.PI * 2 / 64;
            world.spawnParticles(ParticleTypes.FLAME, center.x + Math.cos(angle) * radius, center.y,
                    center.z + Math.sin(angle) * radius, 1, 0, 0.05, 0, 0.01);
        }
    }
}
