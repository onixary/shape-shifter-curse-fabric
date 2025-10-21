package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

// 由于当前apoli版本太旧 不支持最新的支持direction和space组合的raycast 只能用这个方法
public class MustCrawlingCondition {
    private static boolean IsHeadCollide(Entity e) {
        // 检测碰撞箱 防止出现身体与地面穿模 如果卡顿可以直接可以修改为 return true
        BlockPos up1pos = e.getBlockPos().up();
        BlockState up1block = e.getWorld().getBlockState(up1pos);
        
        // 排除脚手架方块
        if (up1block.getBlock() instanceof ScaffoldingBlock) {
            return true;
        }
        
        Vec3d CollideTestPoint = e.getPos().add(0f, 1.5f, 0f); // 蹲下高度为1.5
        BlockHitResult HitResult = up1block.getCollisionShape(e.getWorld(), up1pos).raycast(e.getPos(), CollideTestPoint, up1pos);
        if (HitResult == null) { // 没有碰撞箱时
            return true;
        }
        else {
            return HitResult.getType() == BlockHitResult.Type.MISS;
        }
    }

    public static boolean condition(SerializableData.Instance data, Entity e) {
        // 由于Crawling的能力为修改碰撞箱 所以不能使用这个方法
        // return (t.wouldPoseNotCollide(EntityPose.SWIMMING)) && (!t.wouldPoseNotCollide(EntityPose.CROUCHING));
        return !IsHeadCollide(e);
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("must_crawling"),
                new SerializableData(),
                MustCrawlingCondition::condition
        );
    }
}