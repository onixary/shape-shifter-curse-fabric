package net.onixary.shapeShifterCurseFabric.blocks;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WebComposterBlock extends Block implements InventoryProvider {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 4);
    private static final VoxelShape RAYCAST_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = (VoxelShape[]) Util.make(new VoxelShape[5], (shapes) -> {
        for(int i = 0; i < 4; ++i) {
            shapes[i] = VoxelShapes.combineAndSimplify(RAYCAST_SHAPE, Block.createCuboidShape((double)2.0F, (double)Math.max(2, 1 + i * 4), (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F), BooleanBiFunction.ONLY_FIRST);
        }

        shapes[4] = shapes[3];
    });

    public static Item ResultItem = RegCustomItem.SPIDER_FLUID_COCOON;
    public static Function<Random, Integer> ResultCount = (random) -> 2 + random.nextInt(3);

    public static boolean canIncrease(ItemStack itemStack) {
        if (itemStack.isIn(ModTags.Meat_Tag)) {
            return true;
        }
        FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
        if (foodComponent != null && foodComponent.isMeat()) {
            return true;
        }
        return false;
    }

    public static float getIncreaseChance(ItemStack itemStack) {
        FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
        if (foodComponent != null) {
            // 统一概率即可
            //return Math.min(1.0f, foodComponent.getHunger() / 6.0f);
            return 0.55f;
        }
        return 0.5f;
    }

    public WebComposterBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, MIN_LEVEL));
    }

    public static void playEffects(World world, BlockPos pos, boolean fill) {
        BlockState blockState = world.getBlockState(pos);
        world.playSoundAtBlockCenter(pos, fill ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        double d = blockState.getOutlineShape(world, pos).getEndingCoord(Direction.Axis.Y, (double)0.5F, (double)0.5F) + (double)0.03125F;
        double e = (double)0.13125F;
        double f = (double)0.7375F;
        Random random = world.getRandom();

        for(int i = 0; i < 10; ++i) {
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), (double)pos.getY() + d + (double)random.nextFloat() * ((double)1.0F - d), (double)pos.getZ() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), g, h, j);
        }

    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LEVEL_TO_COLLISION_SHAPE[(Integer)state.get(LEVEL)];
    }

    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LEVEL_TO_COLLISION_SHAPE[0];
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if ((Integer)state.get(LEVEL) == MAX_LEVEL) {
            world.scheduleBlockTick(pos, state.getBlock(), 20);
        }

    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = (Integer)state.get(LEVEL);
        ItemStack itemStack = player.getStackInHand(hand);
        if (i < MAX_LEVEL + 1 && canIncrease(itemStack)) {
            if (i < MAX_LEVEL && !world.isClient) {
                BlockState blockState = addToComposter(player, state, world, pos, itemStack);
                world.syncWorldEvent(1500, pos, state != blockState ? 1 : 0);
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }

            return ActionResult.success(world.isClient);
        } else if (i == MAX_LEVEL + 1) {
            emptyFullComposter(player, state, world, pos);
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    public static BlockState compost(Entity user, BlockState state, ServerWorld world, ItemStack stack, BlockPos pos) {
        int i = (Integer)state.get(LEVEL);
        if (i < MAX_LEVEL && canIncrease(stack)) {
            BlockState blockState = addToComposter(user, state, world, pos, stack);
            stack.decrement(1);
            return blockState;
        } else {
            return state;
        }
    }

    public static BlockState emptyFullComposter(Entity user, BlockState state, World world, BlockPos pos) {
        if (!world.isClient) {
            Vec3d vec3d = Vec3d.add(pos, (double)0.5F, 1.01, (double)0.5F).addRandom(world.random, 0.7F);
            ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack(ResultItem, ResultCount.apply(user.getWorld().getRandom())));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }

        BlockState blockState = emptyComposter(user, state, world, pos);
        world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return blockState;
    }

    static BlockState emptyComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos) {
        BlockState blockState = (BlockState)state.with(LEVEL, 0);
        world.setBlockState(pos, blockState, 3);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
        return blockState;
    }

    static BlockState addToComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack) {
        int i = (Integer)state.get(LEVEL);
        float f = getIncreaseChance(stack);
        if ((i != 0 || !(f > 0.0F)) && !(world.getRandom().nextDouble() < (double)f)) {
            return state;
        } else {
            int j = i + 1;
            BlockState blockState = (BlockState)state.with(LEVEL, j);
            world.setBlockState(pos, blockState, 3);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
            if (j == MAX_LEVEL) {
                world.scheduleBlockTick(pos, state.getBlock(), 20);
            }

            return blockState;
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Integer)state.get(LEVEL) == MAX_LEVEL) {
            world.setBlockState(pos, (BlockState)state.cycle(LEVEL), 3);
            world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (Integer)state.get(LEVEL);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LEVEL});
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        int i = (Integer)state.get(LEVEL);
        if (i == MAX_LEVEL + 1) {
            return new WebComposterBlock.FullComposterInventory(state, world, pos, new ItemStack(ResultItem, ResultCount.apply(world.getRandom())));
        } else {
            return (SidedInventory)(i < MAX_LEVEL ? new WebComposterBlock.ComposterInventory(state, world, pos) : new WebComposterBlock.DummyInventory());
        }
    }

    static class DummyInventory extends SimpleInventory implements SidedInventory {
        public DummyInventory() {
            super(0);
        }

        public int[] getAvailableSlots(Direction side) {
            return new int[0];
        }

        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }
    }

    static class FullComposterInventory extends SimpleInventory implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public FullComposterInventory(BlockState state, WorldAccess world, BlockPos pos, ItemStack outputItem) {
            super(new ItemStack[]{outputItem});
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        public int getMaxCountPerStack() {
            return 1;
        }

        public int[] getAvailableSlots(Direction side) {
            return side == Direction.DOWN ? new int[]{0} : new int[0];
        }

        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return !this.dirty && dir == Direction.DOWN && stack.isOf(ResultItem);
        }

        public void markDirty() {
            WebComposterBlock.emptyComposter((Entity)null, this.state, this.world, this.pos);
            this.dirty = true;
        }
    }

    static class ComposterInventory extends SimpleInventory implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public ComposterInventory(BlockState state, WorldAccess world, BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        public int getMaxCountPerStack() {
            return 1;
        }

        public int[] getAvailableSlots(Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return !this.dirty && dir == Direction.UP && WebComposterBlock.canIncrease(stack);
        }

        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }

        public void markDirty() {
            ItemStack itemStack = this.getStack(0);
            if (!itemStack.isEmpty()) {
                this.dirty = true;
                BlockState blockState = WebComposterBlock.addToComposter((Entity)null, this.state, this.world, this.pos, itemStack);
                this.world.syncWorldEvent(1500, this.pos, blockState != this.state ? 1 : 0);
                this.removeStack(0);
            }

        }
    }
}
