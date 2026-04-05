package net.onixary.shapeShifterCurseFabric.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TempWebBridgeBlock extends HorizontalFacingBlock {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    public static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape voxelShape = Block.createCuboidShape((double)0.0F, (double)14.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F);
    private static final VoxelShape voxelShape2 = Block.createCuboidShape((double)0.0F, (double)0.0F, (double)0.0F, (double)2.0F, (double)16.0F, (double)2.0F);
    private static final VoxelShape voxelShape3 = Block.createCuboidShape((double)14.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)2.0F);
    private static final VoxelShape voxelShape4 = Block.createCuboidShape((double)0.0F, (double)0.0F, (double)14.0F, (double)2.0F, (double)16.0F, (double)16.0F);
    private static final VoxelShape voxelShape5 = Block.createCuboidShape((double)14.0F, (double)0.0F, (double)14.0F, (double)16.0F, (double)16.0F, (double)16.0F);
    private static final VoxelShape NORMAL_OUTLINE_SHAPE = VoxelShapes.union(voxelShape, new VoxelShape[]{voxelShape2, voxelShape3, voxelShape4, voxelShape5});

    public TempWebBridgeBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.scheduledTick(state, world, pos, random);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // if ((random.nextInt(3) == 0 || this.canIncreaseAge(world, pos, 4)) && this.increaseAge(state, world, pos)) {
        //     BlockPos.Mutable mutable = new BlockPos.Mutable();
        //     for(Direction direction : Direction.values()) {
        //         mutable.set(pos, direction);
        //         BlockState blockState = world.getBlockState(mutable);
        //         if (blockState.isOf(this) && !this.increaseAge(blockState, world, mutable)) {
        //             world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
        //         }
        //     }
        // } else {
        //     world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
        // }
        boolean BlockRemoved = false;
        if (this.canIncreaseAge(world, pos, 3) && random.nextInt(12) == 0) {
            BlockRemoved = this.increaseAge(state, world, pos);
        } else if (this.canIncreaseAge(world, pos, 2) && random.nextInt(6) == 0) {
            BlockRemoved = this.increaseAge(state, world, pos);
        } else if (random.nextInt(3) == 0) {
            BlockRemoved = this.increaseAge(state, world, pos);
        }
        if (!BlockRemoved) {
            world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 150, 300));  // 7.5s~15s
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.set(pos, direction);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isOf(this)) {
                world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 150, 300));
            }
        }
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = state.get(AGE);
        if (i < 3) {
            world.setBlockState(pos, state.with(AGE, i + 1), 2);
            return false;
        } else {
            world.removeBlock(pos, false);
            return true;
        }
    }


    // 虽然这样做可以让破碎更美观 但是会严重加速垂直的破碎速度
    // public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    //     if (sourceBlock.getDefaultState().isOf(this) && this.canIncreaseAge(world, pos, 2)) {
    //         world.removeBlock(pos, false);
    //     }
    //     super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    // }

    private boolean canIncreaseAge(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(Direction direction : Direction.values()) {
            mutable.set(pos, direction);
            if (world.getBlockState(mutable).isOf(this)) {
                ++i;
                if (i >= maxNeighbors) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE, HORIZONTAL_FACING});
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending()) {
            return NORMAL_OUTLINE_SHAPE;
        } else {
            return VoxelShapes.empty();
        }
    }

    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
