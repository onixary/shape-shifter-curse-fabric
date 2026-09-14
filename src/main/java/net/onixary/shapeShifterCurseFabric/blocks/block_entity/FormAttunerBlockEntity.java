package net.onixary.shapeShifterCurseFabric.blocks.block_entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;

import java.util.Arrays;
import java.util.List;

public class FormAttunerBlockEntity extends BlockEntity {
    public static final int MAX_LEVEL = 10;  // 大约一次检查1770个方块 再大别整成卡服务机了 这玩意性能消耗指数级上升(但物资消耗会逐渐成为线性) 而且绿宝石块当前版本有掠夺塔(不止掠夺塔可以整 但这应该是效率较高的方式 其他方法感觉更卡服 比如超多核心刷铁塔(这个效率不太高) 双维度猪人塔(这个劲大 我以前试过)) 真要卡爆服务器很简单
    public int level = 0;
    private int minY = 0;
    private List<BeamSegment> beams = Lists.newArrayList();
    private List<BeamSegment> beamSegments = Lists.newArrayList();
    private final PropertyDelegate propertyDelegate;

    public FormAttunerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegCustomBlock.FORM_ATTUNER_BLOCK_ENTITY, blockPos, blockState);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                if (index == 0) {
                    return FormAttunerBlockEntity.this.level;
                }
                return 0;
            }

            public void set(int index, int value) {
                if (index == 0) {
                    FormAttunerBlockEntity.this.level = value;
                }
            }

            public int size() {
                return 1;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, FormAttunerBlockEntity blockEntity) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos blockPos;
        if (blockEntity.minY < j) {
            blockPos = pos;
            blockEntity.beams = Lists.newArrayList();
            blockEntity.minY = pos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, blockEntity.minY + 1, k);
        }
        BeamSegment beamSegment = blockEntity.beams.isEmpty() ? null : (BeamSegment)blockEntity.beams.get(blockEntity.beams.size() - 1);
        int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        for(int m = 0; m < 10 && blockPos.getY() <= l; ++m) {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block instanceof Stainable) {
                float[] fs = ((Stainable)block).getColor().getColorComponents();
                if (blockEntity.beams.size() <= 1) {
                    beamSegment = new BeamSegment(fs);
                    blockEntity.beams.add(beamSegment);
                } else if (beamSegment != null) {
                    if (Arrays.equals(fs, beamSegment.color)) {
                        beamSegment.increaseHeight();
                    } else {
                        beamSegment = new BeamSegment(new float[]{(beamSegment.color[0] + fs[0]) / 2.0F, (beamSegment.color[1] + fs[1]) / 2.0F, (beamSegment.color[2] + fs[2]) / 2.0F});
                        blockEntity.beams.add(beamSegment);
                    }
                }
            } else {
                if (beamSegment == null || blockState.getOpacity(world, blockPos) >= 15 && !blockState.isOf(Blocks.BEDROCK)) {
                    blockEntity.beams.clear();
                    blockEntity.minY = l;
                    break;
                }

                beamSegment.increaseHeight();
            }

            blockPos = blockPos.up();
            ++blockEntity.minY;
        }

        int m = blockEntity.level;
        if (world.getTime() % 80L == 0L) {
            if (!blockEntity.beamSegments.isEmpty()) {
                blockEntity.level = updateLevel(world, i, j, k);
            }
            if (blockEntity.level > 0 && !blockEntity.beamSegments.isEmpty()) {
                // 这里可以写加 Buff
                playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }
        if (blockEntity.minY >= l) {
            blockEntity.minY = world.getBottomY() - 1;
            boolean bl = m > 0;
            blockEntity.beamSegments = blockEntity.beams;
            if (!world.isClient) {
                boolean bl2 = blockEntity.level > 0;
                if (!bl && bl2) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                } else if (bl && !bl2) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateLevel(World world, int x, int y, int z) {
        // 设定上需要诅咒之月的力量 So 仅主世界可用
        if (world.getRegistryKey() != World.OVERWORLD) {
            return 0;
        }
        if (!CursedMoon.isInCursedMoon(world)) {
            return 0;
        }
        int i = 0;
        for(int j = 1; j <= MAX_LEVEL; i = j++) {
            int k = y - j;
            if (k < world.getBottomY()) {
                break;
            }
            boolean bl = true;
            for(int l = x - j; l <= x + j && bl; ++l) {
                for(int m = z - j; m <= z + j; ++m) {
                    if (!world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                        bl = false;
                        break;
                    }
                }
            }
            if (!bl) {
                break;
            }
        }
        return i;
    }

    public List<FormAttunerBlockEntity.BeamSegment> getBeamSegments() {
        return this.level == 0 ? ImmutableList.of() : this.beamSegments;
    }
    
    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound((PlayerEntity)null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
    
    @Override
    public void markRemoved() {
        playSound(this.world, this.pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
        super.markRemoved();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.minY = world.getBottomY() - 1;
    }

    public static class BeamSegment {
        final float[] color;
        private int height;
        public BeamSegment(float[] color) {
            this.color = color;
            this.height = 1;
        }
        
        public void increaseHeight() {
            ++this.height;
        }
        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
