package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Frost Walker I placement/melt rules, without the grounded requirement. */
public class FrostClawEffect extends StatusEffect {
    public FrostClawEffect() { super(StatusEffectCategory.BENEFICIAL, 0xB9EDFF); }

    @Override public boolean canApplyUpdateEffect(int duration, int amplifier) { return true; }
    @Override public void applyUpdateEffect(LivingEntity entity, int amplifier) { freeze(entity, entity.getPos()); }

    public static void freeze(LivingEntity entity, Vec3d center) {
        var world = entity.getWorld();
        if (world.isClient) return;
        var ice = Blocks.FROSTED_ICE.getDefaultState();
        var origin = BlockPos.ofFloored(center);
        // Vanilla level I: radius 2 + 1, exposed source water, scheduled melting.
        for (var pos : BlockPos.iterate(origin.add(-3, -1, -3), origin.add(3, -1, 3))) {
            if (pos.isWithinDistance(center, 3) && world.getBlockState(pos.up()).isAir()
                    && world.getBlockState(pos) == FrostedIceBlock.getMeltedState()
                    && ice.canPlaceAt(world, pos) && world.canPlace(ice, pos, ShapeContext.absent())
                    && world.getWorldBorder().contains(pos)) {
                world.setBlockState(pos, ice);
                world.scheduleBlockTick(pos, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
            }
        }
    }

    /** Sample before collision resolution so fast falls cannot skip the surface layer. */
    public static void beforeMove(LivingEntity entity, Vec3d movement) {
        if (entity.getWorld().isClient) return;
        freeze(entity, entity.getPos());
        if (movement.y >= 0) return;
        // Do not freeze along a path through an existing solid floor or wall.
        movement = net.minecraft.entity.Entity.adjustMovementForCollisions(entity, movement, entity.getBoundingBox(),
                entity.getWorld(), entity.getWorld().getEntityCollisions(entity, entity.getBoundingBox().stretch(movement)));
        int steps = Math.min(64, MathHelper.ceil(movement.length() * 2));
        for (int i = 1; i <= steps; i++) {
            freeze(entity, entity.getPos().add(movement.multiply((double) i / steps)));
        }
    }
}
