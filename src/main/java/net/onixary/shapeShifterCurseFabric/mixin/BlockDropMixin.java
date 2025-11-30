package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyBlockDropPower;
import net.onixary.shapeShifterCurseFabric.util.CachedBlockPositionData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.block.Block.dropStack;

@Mixin(Block.class)
public abstract class BlockDropMixin {
    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void dropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (world instanceof ServerWorld && entity instanceof PlayerEntity player) {
            CachedBlockPositionData cachedBlockPosition = new CachedBlockPositionData(world, pos, false, state, blockEntity);
            for (ModifyBlockDropPower power : PowerHolderComponent.getPowers(player, ModifyBlockDropPower.class)) {
                if (power.CanApply(cachedBlockPosition)) {
                    List<ItemStack> stackList = power.Apply(player.getRandom());
                    if (stackList != null) {
                        stackList.forEach((stack) -> {
                            dropStack(world, pos, stack.copy());
                        });
                        state.onStacksDropped((ServerWorld)world, pos, tool, true);
                        ci.cancel();
                    }
                    // 满足条件但没中概率 执行原掉落物
                    return;
                }
            }
        }
    }
}
