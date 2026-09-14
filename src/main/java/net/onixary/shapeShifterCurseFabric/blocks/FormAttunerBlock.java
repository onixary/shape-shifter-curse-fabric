package net.onixary.shapeShifterCurseFabric.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.FormAttunerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FormAttunerBlock extends BlockWithEntity implements Stainable {
    protected FormAttunerBlock(Settings settings) {
        super(settings);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FormAttunerBlockEntity(pos, state);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.PURPLE;
    }
}
