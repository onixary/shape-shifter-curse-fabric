package net.onixary.shapeShifterCurseFabric.mixin.projectile;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.SnowballBlockTransformPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class) // 改为直接注入Entity类
public abstract class EntitySnowballTransformMixin {

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract BlockPos getBlockPos();


    @Unique
    private boolean hasTransformedFluid = false;

    // 在实体tick方法中检测流体
    @Inject(method = "tick", at = @At("HEAD"))
    private void checkFluidCollision(CallbackInfo ci) {
        // 检查当前实体是否为雪球
        if (!((Object) this instanceof SnowballEntity)) {
            return;
        }

        SnowballEntity snowball = (SnowballEntity) (Object) this;
        World world = snowball.getWorld();

        // 避免重复转换
        if (hasTransformedFluid || world.isClient) {
            return;
        }

        // 检查投掷者权限
        Entity owner = snowball.getOwner();
        if (!(owner instanceof PlayerEntity player)) {
            return;
        }

        boolean hasTransformPower = PowerHolderComponent.getPowers(player, SnowballBlockTransformPower.class)
                .stream()
                .anyMatch(power -> power.isActive());

        if (!hasTransformPower) {
            return;
        }

        // 获取雪球当前位置
        BlockPos currentPos = snowball.getBlockPos();
        FluidState fluidState = world.getFluidState(currentPos);

        // 检测是否在流体中
        if (!fluidState.isEmpty()) {
            transformFluidBlock(world, currentPos, fluidState);
            hasTransformedFluid = true;

            // 销毁雪球（模拟碰撞效果）
            if (!world.isClient) {
                snowball.getWorld().sendEntityStatus(snowball, (byte) 3); // 粒子效果
                snowball.discard();
            }
        }
    }

    // 在流体更新方法中检测
    @Inject(method = "updateWaterState", at = @At("HEAD"))
    private void onEnterWater(CallbackInfoReturnable<Boolean> cir) {
        // 检查当前实体是否为雪球
        if (!((Object) this instanceof SnowballEntity)) {
            return;
        }

        SnowballEntity snowball = (SnowballEntity) (Object) this;
        World world = snowball.getWorld();

        if (hasTransformedFluid || world.isClient) {
            return;
        }

        Entity owner = snowball.getOwner();
        if (!(owner instanceof PlayerEntity player)) {
            return;
        }

        boolean hasTransformPower = PowerHolderComponent.getPowers(player, SnowballBlockTransformPower.class)
                .stream()
                .anyMatch(power -> power.isActive());

        if (!hasTransformPower) {
            return;
        }

        // 检查雪球是否刚进入流体
        BlockPos pos = snowball.getBlockPos();
        FluidState fluidState = world.getFluidState(pos);

        if (!fluidState.isEmpty()) {
            transformFluidBlock(world, pos, fluidState);
            hasTransformedFluid = true;
        }
    }

    @Unique
    private void transformFluidBlock(World world, BlockPos pos, FluidState fluidState) {
        BlockState currentState = world.getBlockState(pos);

        // 处理水转冰
        if (fluidState.isIn(FluidTags.WATER)) {
            world.setBlockState(pos, Blocks.ICE.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW, SoundCategory.BLOCKS, 0.8f, 1.2f);
            world.playSound(null, pos, SoundEvents.BLOCK_SNOW_PLACE, SoundCategory.BLOCKS, 0.6f, 1.5f);
        }
        // 处理岩浆转换
        else if (fluidState.isIn(FluidTags.LAVA)) {
            BlockState newState;

            if (fluidState.isStill()) {
                newState = Blocks.OBSIDIAN.getDefaultState();
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 0.8f);
            } else {
                newState = Blocks.STONE.getDefaultState();
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8f, 1.0f);
            }

            world.setBlockState(pos, newState);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.6f, 1.5f);
        }
    }
}
