package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class CachedBlockPositionData extends CachedBlockPosition {
    private final BlockState stateCache;
    private final BlockEntity blockEntityCache;

    public CachedBlockPositionData(WorldView world, BlockPos pos, boolean forceLoad, BlockState state, @Nullable BlockEntity blockEntity) {
        super(world, pos, forceLoad);
        this.stateCache = state;
        this.blockEntityCache = blockEntity;
    }

    public BlockState getBlockState() {
        return this.stateCache;
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        return this.blockEntityCache;
    }
}
