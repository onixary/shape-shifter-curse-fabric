package net.onixary.shapeShifterCurseFabric.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;
import org.jetbrains.annotations.Nullable;


// 渲染先用透明方案吧 BlockEntity类方块由BlockEntity动态渲染
public class AlterBlock extends BlockWithEntity {
    protected AlterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlterBlockEntity(pos, state);
    }
}
