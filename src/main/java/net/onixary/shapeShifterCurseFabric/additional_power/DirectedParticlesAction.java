package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

/** Initial velocity only: the selected particle retains its vanilla physics and lifetime. */
public final class DirectedParticlesAction {
    public enum Direction { ACTOR_TO_TARGET, TARGET_TO_ACTOR }

    static ParticleS2CPacket packet(SerializableData.Instance data, Entity actor, Entity target) {
        Vec3d a = actor.getBoundingBox().getCenter().add(data.<Vec3d>get("actor_offset"));
        Vec3d b = target.getBoundingBox().getCenter().add(data.<Vec3d>get("target_offset"));
        boolean reverse = data.<Direction>get("direction") == Direction.TARGET_TO_ACTOR;
        Vec3d start = reverse ? b : a;
        Vec3d direction = (reverse ? a.subtract(b) : b.subtract(a)).normalize();
        // Count zero means ONE directed particle in the vanilla packet protocol.
        return new ParticleS2CPacket(data.get("particle"), data.getBoolean("force"), start.x, start.y, start.z,
                (float) direction.x, (float) direction.y, (float) direction.z,
                Math.max(0, data.getFloat("speed")), 0);
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(ShapeShifterCurseFabric.identifier("directed_particles"), new SerializableData()
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("direction", SerializableDataType.enumValue(Direction.class), Direction.ACTOR_TO_TARGET)
                .add("actor_offset", SerializableDataTypes.VECTOR, Vec3d.ZERO)
                .add("target_offset", SerializableDataTypes.VECTOR, Vec3d.ZERO)
                .add("speed", SerializableDataTypes.FLOAT, 0.3f)
                .add("count", SerializableDataTypes.INT, 1)
                .add("force", SerializableDataTypes.BOOLEAN, false), (data, pair) -> {
            Entity actor = pair.getLeft(), target = pair.getRight();
            if (actor == null || target == null || !(actor.getWorld() instanceof ServerWorld world)
                    || target.getWorld() != world || actor.isRemoved() || target.isRemoved()) return;
            int count = Math.max(0, data.getInt("count"));
            if (count == 0) return;
            var packet = packet(data, actor, target);
            for (var viewer : world.getPlayers()) {
                for (int i = 0; i < count; i++) {
                    if (!world.sendToPlayerIfNearby(viewer, data.getBoolean("force"), packet.getX(), packet.getY(), packet.getZ(), packet)) break;
                }
            }
        });
    }
}
