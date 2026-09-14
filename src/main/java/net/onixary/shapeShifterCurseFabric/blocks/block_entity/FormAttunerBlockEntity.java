package net.onixary.shapeShifterCurseFabric.blocks.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;

public class FormAttunerBlockEntity extends BlockEntity {
    public FormAttunerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegCustomBlock.FORM_ATTUNER_BLOCK_ENTITY, blockPos, blockState);
    }
}
