package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FrostClawEffect extends StatusEffect {
    public FrostClawEffect() { super(StatusEffectCategory.BENEFICIAL, 0xB9EDFF); }

    @Override public boolean canApplyUpdateEffect(int duration, int amplifier) { return true; }
    @Override public void applyUpdateEffect(LivingEntity entity, int amplifier) { freeze(entity, entity.getPos()); }

    public static void freeze(LivingEntity entity, Vec3d center) {
        World world = entity.getWorld();
        if (world.isClient) return;
        BlockState ice = Blocks.FROSTED_ICE.getDefaultState();
        BlockPos origin = BlockPos.ofFloored(center);
        // Vanilla level I: radius 2 + 1, exposed source water, scheduled melting.
        for (BlockPos pos : BlockPos.iterate(origin.add(-3, -1, -3), origin.add(3, -1, 3))) {
            if (pos.isWithinDistance(center, 3) && world.getBlockState(pos.up()).isAir()
                    && world.getBlockState(pos) == FrostedIceBlock.getMeltedState()
                    && ice.canPlaceAt(world, pos) && world.canPlace(ice, pos, ShapeContext.absent())
                    && world.getWorldBorder().contains(pos)) {
                world.setBlockState(pos, ice);
                world.scheduleBlockTick(pos, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
            }
        }
    }

    public static void beforeMove(LivingEntity entity, Vec3d movement) {
        // TODO 这个得优化一下运行速度 这AI怎么老整这种卡的要命的玩意
        if (entity.getWorld().isClient) return;
        freeze(entity, entity.getPos());
        if (movement.y >= 0) return;
        movement = Entity.adjustMovementForCollisions(entity, movement, entity.getBoundingBox(),
                entity.getWorld(), entity.getWorld().getEntityCollisions(entity, entity.getBoundingBox().stretch(movement)));
        int steps = Math.min(64, MathHelper.ceil(movement.length() * 2));
        for (int i = 1; i <= steps; i++) {
            freeze(entity, entity.getPos().add(movement.multiply((double) i / steps)));
        }
    }
}
