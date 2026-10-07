package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Small reusable gaps around Apoli actions; perk values remain in resource JSON. */
public final class PerkActions {
    private PerkActions() {}

    public static void setVelocity(Entity entity, Vec3d velocity) {
        entity.setVelocity(velocity);
        entity.velocityModified = true;
        if (entity instanceof ServerPlayerEntity player) player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
    }

    public static boolean isEnemy(Entity actor, Entity target) {
        if (!(target instanceof LivingEntity) || target.isSpectator()
                || actor == target || actor.isTeammate(target)) return false;
        if (target instanceof TameableEntity pet && actor.getUuid().equals(pet.getOwnerUuid())) return false;
        return target instanceof HostileEntity || target instanceof MobEntity mob && mob.getTarget() == actor;
    }

    public static void registerConditions() {
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, ShapeShifterCurseFabric.identifier("in_oriented_box"),
                new ConditionFactory<Pair<Entity, Entity>>(ShapeShifterCurseFabric.identifier("in_oriented_box"),
                        new SerializableData().add("width", SerializableDataTypes.DOUBLE, 4.0)
                                .add("length", SerializableDataTypes.DOUBLE, 7.0)
                                .add("height", SerializableDataTypes.DOUBLE, 3.0)
                                .add("centered", SerializableDataTypes.BOOLEAN, false),
                        (data, pair) -> inBox(data, pair.getLeft(), pair.getRight())));
        Registry.register(ApoliRegistries.ENTITY_CONDITION, ShapeShifterCurseFabric.identifier("airborne"),
                new ConditionFactory<Entity>(ShapeShifterCurseFabric.identifier("airborne"), new SerializableData(),
                        (data, entity) -> entity instanceof LivingEntity living && FrostDivePower.airborne(living)));
        Registry.register(ApoliRegistries.ENTITY_CONDITION, ShapeShifterCurseFabric.identifier("diving"),
                new ConditionFactory<Entity>(ShapeShifterCurseFabric.identifier("diving"), new SerializableData(),
                        (data, entity) -> FrostDivePower.isDiving(entity)));
        Registry.register(ApoliRegistries.ENTITY_CONDITION, ShapeShifterCurseFabric.identifier("charging"),
                new ConditionFactory<Entity>(ShapeShifterCurseFabric.identifier("charging"),
                        new SerializableData().add("power", SerializableDataTypes.IDENTIFIER), (data, entity) -> {
                    var power = PowerHolderComponent.KEY.get(entity).getPower(PowerTypeRegistry.get(data.getId("power")));
                    return power instanceof ChargePower charge && charge.isCharging();
                }));
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, ShapeShifterCurseFabric.identifier("enemy"),
                new ConditionFactory<Pair<Entity, Entity>>(ShapeShifterCurseFabric.identifier("enemy"),
                        new SerializableData(), (data, pair) -> isEnemy(pair.getLeft(), pair.getRight())));
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, ShapeShifterCurseFabric.identifier("raycast_target"),
                new ConditionFactory<Pair<Entity, Entity>>(ShapeShifterCurseFabric.identifier("raycast_target"),
                        new SerializableData().add("distance", SerializableDataTypes.DOUBLE, 10.0), (data, pair) -> {
                    Entity actor = pair.getLeft();
                    Vec3d from = actor.getEyePos();
                    Vec3d to = from.add(actor.getRotationVec(1).multiply(data.getDouble("distance")));
                    var block = actor.getWorld().raycast(new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, actor));
                    double max = block.getType() == HitResult.Type.MISS ? from.squaredDistanceTo(to) : from.squaredDistanceTo(block.getPos());
                    var hit = ProjectileUtil.raycast(actor, from, to, actor.getBoundingBox().stretch(to.subtract(from)).expand(1),
                            e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator(), max);
                    return hit != null && hit.getEntity() == pair.getRight();
                }));
    }

    public static void registerActions() {
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("recall_wolf_minions"),
                new SerializableData(), (data, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player)
                    || !(player instanceof net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion owner)) return;
            var id = net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity.MinionID;
            var minions = owner.shape_shifter_curse$getMinionsByMinionID(id);
            if (minions == null) return;
            for (var uuid : java.util.List.copyOf(minions)) {
                for (var world : player.getServer().getWorlds()) {
                    if (world.getEntity(uuid) instanceof net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity wolf
                            && player.getUuid().equals(wolf.getOwnerUuid()) && wolf.isAlive()) wolf.kill();
                }
            }
            // Unloaded wolves are retired by shouldExist when their chunk returns.
            owner.shape_shifter_curse$clearMinions(id);
        }));
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("change_food"),
                new SerializableData().add("amount", SerializableDataTypes.INT), (data, entity) -> {
            if (entity instanceof PlayerEntity player && !entity.getWorld().isClient) {
                var hunger = player.getHungerManager();
                hunger.setFoodLevel(Math.max(0, Math.min(20, hunger.getFoodLevel() + data.getInt("amount"))));
                hunger.setSaturationLevel(Math.min(hunger.getSaturationLevel(), hunger.getFoodLevel()));
            }
        }));
        AdditionalEntityActions.registerBIAction(new ActionFactory<Pair<Entity, Entity>>(ShapeShifterCurseFabric.identifier("damage_on_success"),
                new SerializableData().add("amount", SerializableDataTypes.FLOAT)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null), (data, pair) -> {
            if (!(pair.getLeft() instanceof LivingEntity actor) || actor.getWorld().isClient) return;
            var source = actor instanceof PlayerEntity player ? actor.getDamageSources().playerAttack(player) : actor.getDamageSources().mobAttack(actor);
            if (pair.getRight().damage(source, data.getFloat("amount"))) {
                Consumer<Pair<Entity, Entity>> action = data.get("bientity_action");
                if (action != null) action.accept(pair);
            }
        }));
        AdditionalEntityActions.registerBIAction(new ActionFactory<Pair<Entity, Entity>>(ShapeShifterCurseFabric.identifier("start_target_pounce"),
                new SerializableData().add("power", SerializableDataTypes.IDENTIFIER), (data, pair) -> {
            if (pair.getLeft().getWorld().isClient || !(pair.getRight() instanceof LivingEntity target)) return;
            var type = PowerTypeRegistry.get(data.getId("power"));
            var power = PowerHolderComponent.KEY.get(pair.getLeft()).getPower(type);
            if (power instanceof TargetPouncePower pounce) pounce.start(target);
        }));
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("fire_held_arrow"),
                new SerializableData().add("speed", SerializableDataTypes.FLOAT, 3f)
                        .add("cooldown", SerializableDataTypes.INT, 20), (data, entity) -> {
            if (!(entity instanceof PlayerEntity player) || player.getWorld().isClient) return;
            ItemStack stack = player.getMainHandStack();
            if (!(stack.getItem() instanceof ArrowItem item) || (player.getItemCooldownManager().isCoolingDown(item) || player.getItemCooldownManager().isCoolingDown(Items.ARROW))) return;
            PersistentProjectileEntity arrow = item.createArrow(player.getWorld(), stack, player);
            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0, data.getFloat("speed"), 0);
            arrow.setCritical(true);
            arrow.pickupType = player.isCreative() ? PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY : PersistentProjectileEntity.PickupPermission.ALLOWED;
            if (player.getWorld().spawnEntity(arrow)) {
                if (!player.isCreative()) stack.decrement(1);
                player.getItemCooldownManager().set(item, data.getInt("cooldown"));
                player.getItemCooldownManager().set(Items.ARROW, data.getInt("cooldown"));
            }
        }));
        AdditionalEntityActions.registerAction(new ActionFactory<Entity>(ShapeShifterCurseFabric.identifier("oriented_box"),
                new SerializableData().add("width", SerializableDataTypes.DOUBLE, 4.0)
                        .add("length", SerializableDataTypes.DOUBLE, 7.0)
                        .add("height", SerializableDataTypes.DOUBLE, 3.0)
                        .add("centered", SerializableDataTypes.BOOLEAN, false)
                        .add("particles", SerializableDataTypes.BOOLEAN, false)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION), PerkActions::boxAction));
    }

    private static void boxAction(SerializableData.Instance data, Entity actor) {
        if (!(actor.getWorld() instanceof ServerWorld world)) return;
        double width = data.getDouble("width"), length = data.getDouble("length"), height = data.getDouble("height");
        if (width <= 0 || length <= 0 || height <= 0) return;
        Vec3d forward = new Vec3d(-Math.sin(Math.toRadians(actor.getYaw())), 0, Math.cos(Math.toRadians(actor.getYaw())));
        Vec3d right = new Vec3d(forward.z, 0, -forward.x);
        double offset = data.getBoolean("centered") ? 0 : length / 2;
        Vec3d center = actor.getPos().add(forward.multiply(offset)).add(0, height / 2, 0);
        Predicate<Pair<Entity, Entity>> condition = data.get("bientity_condition");
        Consumer<Pair<Entity, Entity>> action = data.get("bientity_action");
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, new Box(center, center).expand(width + length, height, width + length),
                e -> e != actor && e.isAlive() && !e.isSpectator())) {
            if (!inBox(data, actor, target)) continue;
            Pair<Entity, Entity> pair = new Pair<>(actor, target);
            if (condition == null || condition.test(pair)) action.accept(pair);
        }
        if (data.getBoolean("particles")) {
            for (double z = -length / 2; z <= length / 2; z += 0.5) {
                for (double x = -width / 2; x <= width / 2; x += 0.5) {
                    Vec3d pos = center.add(forward.multiply(z)).add(right.multiply(x));
                    world.spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 1, 0, height / 4, 0, 0.03);
                }
            }
        }
    }

    private static boolean inBox(SerializableData.Instance data, Entity actor, Entity target) {
        if (actor == null || target == null || actor == target || !target.isAlive() || target.isSpectator()) return false;
        double width = data.getDouble("width"), length = data.getDouble("length"), height = data.getDouble("height");
        if (width <= 0 || length <= 0 || height <= 0) return false;
        Vec3d forward = new Vec3d(-Math.sin(Math.toRadians(actor.getYaw())), 0, Math.cos(Math.toRadians(actor.getYaw())));
        Vec3d right = new Vec3d(forward.z, 0, -forward.x);
        Vec3d center = actor.getPos().add(forward.multiply(data.getBoolean("centered") ? 0 : length / 2)).add(0, height / 2, 0);
        Box bounds = target.getBoundingBox();
        Vec3d relative = bounds.getCenter().subtract(center);
        double rx = (bounds.maxX - bounds.minX) / 2, rz = (bounds.maxZ - bounds.minZ) / 2;
        return Math.abs(relative.dotProduct(right)) <= width / 2 + Math.abs(right.x) * rx + Math.abs(right.z) * rz
                && Math.abs(relative.dotProduct(forward)) <= length / 2 + Math.abs(forward.x) * rx + Math.abs(forward.z) * rz
                && Math.abs(relative.y) <= height / 2 + (bounds.maxY - bounds.minY) / 2;
    }
}
