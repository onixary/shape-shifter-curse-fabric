package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class MustCrawlingCondition {
    private static boolean IsHeadNotCollide(Entity e, float width, float height) {
        if (e.noClip || e.isSpectator()) {
            return true;
        }
        Vec3d vec3d = e.getPos();
        return e.getWorld().isSpaceEmpty(e, new Box(vec3d.x - width / 2.0, vec3d.y, vec3d.z - width / 2.0, vec3d.x + width / 2.0, vec3d.y + height, vec3d.z + width / 2.0).contract(1.0E-7));
    }

    public static boolean condition(SerializableData.Instance data, Entity e) {
        return !IsHeadNotCollide(e, data.getFloat("width"), data.getFloat("height"));
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("must_crawling"),
                new SerializableData()
                        .add("width", SerializableDataTypes.FLOAT, 0.6f)
                        .add("height", SerializableDataTypes.FLOAT, 1.5f),
                MustCrawlingCondition::condition
        );
    }
}