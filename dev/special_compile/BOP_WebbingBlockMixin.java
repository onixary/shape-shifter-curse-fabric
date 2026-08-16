package net.onixary.shapeShifterCurseFabric.mixin.integration;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.SlowdownPercentPower;
import org.spongepowered.asm.mixin.Mixin;
import biomesoplenty.block.WebbingBlock;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WebbingBlock.class)
public class BOP_WebbingBlockMixin {
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            List<SlowdownPercentPower> slowdownPower = PowerHolderComponent.getPowers(player, SlowdownPercentPower.class);
            float slowdownPercent = 1.0f;
            for (SlowdownPercentPower power : slowdownPower) {
                slowdownPercent *= power.Multiplier;
            }
            player.setVelocity(player.getVelocity().multiply(1D - (0.375D * slowdownPercent), 1D - (0.25D * slowdownPercent), 1D - (0.375D * slowdownPercent)));
            ci.cancel();
        }
    }
}
