package net.onixary.shapeShifterCurseFabric.additional_power;

import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WebBridgeAction {
    public record WebLadderConfig(int SideBlockNum, int BottomBlockNum, int TopBlockNum, boolean LargerLadder, int LargerLadderCount) {}
    public record WebBridgeConfig(int Length, int Width) {}

    public static boolean SetWebBlock(World world, BlockPos pos, Block WebBlock) {
        if (world.getBlockState(pos).isAir()) {
            world.setBlockState(pos, WebBlock.getDefaultState());
            return true;
        }
        return false;
    }

    public static void BuildWebLadder(World world, BlockHitResult blockHitResult, WebLadderConfig config, Block LadderBlock) {
        BlockPos pos = blockHitResult.getBlockPos();
        Direction direction = blockHitResult.getSide();

        BlockPos NowPos = null;
        Direction LadderDirection = null;
        int Length = 0;
        boolean LargerLadder = config.LargerLadder;
        int LargerLadderCount = config.LargerLadderCount;

        switch (direction) {
            case UP -> {
                NowPos = pos.up();
                LadderDirection = Direction.UP;
                Length = config.TopBlockNum;
            }
            case DOWN -> {
                NowPos = pos.down();
                LadderDirection = Direction.DOWN;
                Length = config.BottomBlockNum;
            }
            case NORTH, WEST, EAST, SOUTH -> {
                NowPos = pos.offset(direction);
                LadderDirection = Direction.DOWN;
                Length = config.SideBlockNum;
            }
        }

        for (int i = 0; i < Length; i++) {
            if (!SetWebBlock(world, NowPos, LadderBlock)) {
                break;
            }
            if (LargerLadder && LargerLadderCount > 0) {
                SetWebBlock(world, NowPos.east(), LadderBlock);
                SetWebBlock(world, NowPos.west(), LadderBlock);
                SetWebBlock(world, NowPos.north(), LadderBlock);
                SetWebBlock(world, NowPos.south(), LadderBlock);
                LargerLadderCount--;
            }
            NowPos = NowPos.offset(LadderDirection);
        }
    }

    public static void BuildWebBridge(World world, BlockPos pos, Direction direction, WebBridgeConfig config, Block WebBlock) {
        BlockPos NowPos = pos;
        BlockPos TempPos = pos;
        Direction TempDirection = direction;
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return;
        }
        for (int i = 0; i < config.Length; i++) {
            SetWebBlock(world, NowPos, WebBlock);
            TempPos = NowPos;
            TempDirection = direction.rotateYClockwise();
            for (int j = 0; j < config.Width; j++) {
                TempPos = TempPos.offset(TempDirection);
                SetWebBlock(world, TempPos, WebBlock);
            }
            TempPos = NowPos;
            TempDirection = direction.rotateYCounterclockwise();
            for (int j = 0; j < config.Width; j++) {
                TempPos = TempPos.offset(TempDirection);
                SetWebBlock(world, TempPos, WebBlock);
            }
            NowPos = NowPos.offset(direction);
        }
    }
}
