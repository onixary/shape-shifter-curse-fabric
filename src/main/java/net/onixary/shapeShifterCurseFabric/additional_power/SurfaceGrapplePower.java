package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;

/** Server-owned reservation, surface preview and collision-aware straight flight. */
public class SurfaceGrapplePower extends Power implements Active {
    private final double distance, speed, manaCost;
    private final int cooldown;
    private final Identifier manaType;
    private Key key;
    private boolean charging, paid, savedGravity, restoreGravity;
    private int ticks, lastUse, flightTicks;
    private long readyAt;
    private Vec3d destination;

    public SurfaceGrapplePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        distance = Math.max(0, data.getDouble("distance"));
        speed = Math.max(0.01, data.getDouble("speed"));
        manaCost = Math.max(0, data.getDouble("mana_cost"));
        cooldown = Math.max(0, data.getInt("cooldown"));
        manaType = data.getId("mana_type");
        key = data.get("key");
        setTicking(true);
    }

    @Override public void onUse() {
        if (!(entity instanceof ServerPlayerEntity player) || !isActive() || !entity.isAlive()
                || entity.hasVehicle() || destination != null || player.getWorld().getTime() < readyAt) return;
        if (!charging) {
            if (!manaType.equals(ManaUtils.getManaComponent(player).getManaTypeID())
                    || ManaUtils.getManaComponent(player).getMana() < manaCost) return;
            ManaUtils.getManaComponent(player).consumeMana(manaCost);
            charging = paid = true;
        }
        lastUse = ticks;
    }

    public Vec3d findDestination() {
        Vec3d eye = entity.getEyePos();
        var hit = entity.getWorld().raycast(new RaycastContext(eye,
                eye.add(entity.getRotationVec(1).multiply(distance)), RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, entity));
        if (hit.getType() != HitResult.Type.BLOCK) return null;
        Vec3d point = hit.getPos().add(Vec3d.of(hit.getSide().getVector()));
        var box = entity.getBoundingBox().offset(point.subtract(entity.getPos()));
        return entity.getWorld().getWorldBorder().contains(box) && entity.getWorld().isSpaceEmpty(entity, box) ? point : null;
    }

    public void release() {
        if (!charging || entity.getWorld().isClient) return;
        charging = false;
        Vec3d point = isActive() && entity.isAlive() && !entity.hasVehicle() ? findDestination() : null;
        if (point == null) { refund(); return; }
        paid = false;
        destination = point;
        readyAt = entity.getWorld().getTime() + cooldown;
        savedGravity = entity.hasNoGravity();
        entity.setNoGravity(true);
        flightTicks = (int) Math.ceil(entity.getPos().distanceTo(point) / speed) + 20;
    }

    @Override public void tick() {
        if (entity.getWorld().isClient) return;
        if (restoreGravity) { entity.setNoGravity(savedGravity); restoreGravity = false; }
        ticks++;
        if (!isActive() || !entity.isAlive() || entity.hasVehicle()) { cancel(); return; }
        if (paid && !charging) refund(); // Reloaded reservations never resume an unattended charge.
        if (charging) {
            if (ticks - lastUse > 2) release();
            else if (entity instanceof ServerPlayerEntity player) {
                Vec3d point = findDestination();
                if (point != null) player.getServerWorld().spawnParticles(player, ParticleTypes.CLOUD, true,
                        point.x, point.y, point.z, 2, 0.08, 0.08, 0.08, 0);
            }
        }
        if (destination == null) return;
        Vec3d delta = destination.subtract(entity.getPos());
        if (delta.lengthSquared() < 0.04 || flightTicks-- <= 0) { stopFlight(); return; }
        Vec3d step = delta.normalize().multiply(Math.min(speed, delta.length()));
        var swept = entity.getBoundingBox().stretch(step);
        if (!entity.getWorld().getWorldBorder().contains(swept)) { stopFlight(); return; }
        for (var collision : entity.getWorld().getBlockCollisions(entity, swept)) {
            if (!collision.isEmpty()) { stopFlight(); return; }
        }
        entity.fallDistance = 0;
        PerkActions.setVelocity(entity, step);
    }

    private void refund() {
        if (!paid) return;
        paid = false;
        if (entity instanceof PlayerEntity player && manaType.equals(ManaUtils.getManaComponent(player).getManaTypeID())) {
            ManaUtils.getManaComponent(player).gainMana(manaCost);
        }
    }
    private void stopFlight() {
        if (destination == null) return;
        entity.setNoGravity(savedGravity);
        PerkActions.setVelocity(entity, Vec3d.ZERO);
        destination = null;
    }
    private void cancel() { charging = false; refund(); stopFlight(); }
    @Override public void onRemoved() { if (!entity.getWorld().isClient) cancel(); }
    @Override public Key getKey() { return key; }
    @Override public void setKey(Key key) { this.key = key; }
    @Override public NbtElement toTag() {
        var tag = new NbtCompound();
        tag.putLong("readyAt", readyAt);
        tag.putBoolean("paid", paid);
        tag.putBoolean("inFlight", destination != null);
        tag.putBoolean("savedGravity", savedGravity);
        return tag;
    }
    @Override public void fromTag(NbtElement nbt) {
        if (nbt instanceof NbtCompound tag) {
            readyAt = tag.getLong("readyAt"); paid = tag.getBoolean("paid");
            restoreGravity = tag.getBoolean("inFlight"); savedGravity = tag.getBoolean("savedGravity");
        }
    }
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("surface_grapple"),
                new SerializableData().add("distance", SerializableDataTypes.DOUBLE, 12.0)
                        .add("speed", SerializableDataTypes.DOUBLE, 1.5)
                        .add("mana_cost", SerializableDataTypes.DOUBLE, 10.0)
                        .add("mana_type", SerializableDataTypes.IDENTIFIER, ShapeShifterCurseFabric.identifier("web_resource"))
                        .add("cooldown", SerializableDataTypes.INT, 100)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
                data -> (type, entity) -> new SurfaceGrapplePower(type, entity, data)).allowCondition();
    }
}
