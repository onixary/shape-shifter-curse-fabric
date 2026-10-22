package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ArrowAimUtils {
    private ArrowAimUtils() {}

    /** Low arc for vanilla arrows in air, including drag and discrete tick movement. */
    public static @Nullable Vec3d launchDirection(Vec3d offset, float speed) {
        if (speed <= 0 || !Double.isFinite(offset.lengthSquared())) return null;
        double previousTime = 0;
        // The first reachable flight time selects the low arc. Fall back for unreachable targets.
        for (int tick = 1; tick <= 120; tick++) {
            if (velocityAt(offset, tick).lengthSquared() <= (double) speed * speed) {
                double low = previousTime, high = tick;
                for (int i = 0; i < 24; i++) {
                    double mid = (low + high) / 2;
                    if (velocityAt(offset, mid).lengthSquared() > (double) speed * speed) low = mid;
                    else high = mid;
                }
                return velocityAt(offset, high);
            }
            previousTime = tick;
        }
        return null;
    }

    private static Vec3d velocityAt(Vec3d offset, double time) {
        double drag = (double) 0.99F;
        int ticks = (int) time;
        double remainingVelocity = Math.pow(drag, ticks);
        double travel = (1 - remainingVelocity) / (1 - drag) + (time - ticks) * remainingVelocity;
        double drop = (double) 0.05F * (time - travel) / (1 - drag);
        return new Vec3d(offset.x / travel, (offset.y + drop) / travel, offset.z / travel);
    }
}
