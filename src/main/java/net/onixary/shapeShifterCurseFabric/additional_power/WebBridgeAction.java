package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.blocks.TempWebBridgeBlock;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.minecraft.block.BlockState;
import net.onixary.shapeShifterCurseFabric.entity.projectile.WebBullet;

import java.util.function.Consumer;

public class WebBridgeAction {
    public record WebLadderConfig(int SideBlockNum, int BottomBlockNum, int TopBlockNum, boolean LargerLadder, float LargerLadderCountPercent) {}
    public record WebBridgeConfig(int Length, int Width) {}

    public static boolean SetWebBlock(World world, BlockPos pos, Block WebBlock, Direction facing) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.isOf(WebBlock)) {
            BlockState state = WebBlock.getDefaultState().with(TempWebBridgeBlock.HORIZONTAL_FACING, facing);
            world.setBlockState(pos, state);
            return true;
        }
        return false;
    }

    public static void BuildWebLadder(World world, BlockHitResult blockHitResult, WebLadderConfig config, Block LadderBlock) {
        BlockPos pos = blockHitResult.getBlockPos();
        Direction direction = blockHitResult.getSide();

        Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        Random random = world.getRandom();

        BlockPos NowPos = null;
        Direction LadderDirection = null;
        int Length = 0;
        boolean LargerLadder = config.LargerLadder;

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

        int LargerLadderCount = (int)(config.LargerLadderCountPercent * Length);

        for (int i = 0; i < Length; i++) {
            Direction randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
            if (!SetWebBlock(world, NowPos, LadderBlock, randomFacing)) {
                break;
            }
            if (LargerLadder && LargerLadderCount > 0) {
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, NowPos.east(), LadderBlock, randomFacing);
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, NowPos.west(), LadderBlock, randomFacing);
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, NowPos.north(), LadderBlock, randomFacing);
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, NowPos.south(), LadderBlock, randomFacing);
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
        // 预定义水平方向数组，用于随机选择
        Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        Random random = world.getRandom();
        
        for (int k = -config.Width; k <= config.Width; k++) {
            for (int m = -config.Width; m <= config.Width; m++) {
                Direction randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, pos.add(k, 0, m), WebBlock, randomFacing);
            }
        }
        for (int i = 0; i < config.Length; i++) {
            Direction randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
            SetWebBlock(world, NowPos, WebBlock, randomFacing);
            TempPos = NowPos;
            TempDirection = direction.rotateYClockwise();
            for (int j = 0; j < config.Width; j++) {
                TempPos = TempPos.offset(TempDirection);
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, TempPos, WebBlock, randomFacing);
            }
            TempPos = NowPos;
            TempDirection = direction.rotateYCounterclockwise();
            for (int j = 0; j < config.Width; j++) {
                TempPos = TempPos.offset(TempDirection);
                randomFacing = horizontalDirections[random.nextInt(horizontalDirections.length)];
                SetWebBlock(world, TempPos, WebBlock, randomFacing);
            }
            NowPos = NowPos.offset(direction);
        }
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("web_bridge"),
                new SerializableData()
                        .add("web_bridge_length", SerializableDataTypes.INT, 16)
                        .add("web_bridge_width", SerializableDataTypes.INT, 0),
                (data, entity) -> {
                    BlockPos pos = entity.getBlockPos();
                    Direction direction = entity.getHorizontalFacing();
                    BuildWebBridge(entity.getWorld(), pos, direction, new WebBridgeConfig(data.getInt("web_bridge_length"), data.getInt("web_bridge_width")), RegCustomBlock.TEMP_WEB_BRIDGE);
                }
        ));

        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("fire_web_bullet"),
                new SerializableData()
                        .add("tier", SerializableDataTypes.INT, 1)
                        .add("divergence", SerializableDataTypes.FLOAT, 1F)
                        .add("speed", SerializableDataTypes.FLOAT, 1.5F)
                        .add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null),
                (data, entity) -> {
                    if (entity instanceof LivingEntity livingEntity) {
                        WebBullet webBullet = new WebBullet(livingEntity, data.getInt("tier"));
                        webBullet.setVelocity(livingEntity, livingEntity.getPitch(), livingEntity.getYaw(), 0.0f, data.getFloat("speed"), data.getFloat("divergence"));
                        livingEntity.getWorld().spawnEntity(webBullet);
                        data.<Consumer<Entity>>ifPresent("projectile_action", projectileAction -> projectileAction.accept(webBullet));
                    }
                }
        ));
    }
}
