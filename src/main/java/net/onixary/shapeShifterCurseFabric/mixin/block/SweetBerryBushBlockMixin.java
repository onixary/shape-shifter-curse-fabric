package net.onixary.shapeShifterCurseFabric.mixin.block;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.PreventBerryEffectPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修改甜浆果丛方块行为
@Mixin(value = SweetBerryBushBlock.class, priority = 1001)
public abstract class SweetBerryBushBlockMixin {
    @Inject(
            method = "onEntityCollision",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            cancellable = true
    )
    private void preventBerryDamage(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (PowerHolderComponent.hasPower(player, PreventBerryEffectPower.class)) {
                ci.cancel();
            }
        }
    }

    @Redirect(
            method = "onEntityCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;slowMovement(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    private void preventBerrySlowdown(Entity entity, BlockState state, Vec3d multiplier) {
        // 如果是玩家则跳过减速
        if ((entity instanceof PlayerEntity)) {
            if (!PowerHolderComponent.hasPower((PlayerEntity)entity, PreventBerryEffectPower.class)) {
                entity.slowMovement(state, multiplier);
            }
        }
        else{
            entity.slowMovement(state, multiplier);
        }
    }
}

