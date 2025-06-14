package net.onixary.shapeShifterCurseFabric.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 修改甜浆果丛方块行为
@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin {
    @Redirect(
            method = "onEntityCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private boolean preventBerryDamage(Entity entity, DamageSource source, float amount) {
        // 如果是玩家则跳过伤害
        if (entity instanceof PlayerEntity) {
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(entity).getCurrentForm();
            if(curForm == PlayerForms.FAMILIAR_FOX_2){
                return false;
            }
        }
        return entity.damage(source, amount);
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
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(entity).getCurrentForm();
            if(curForm != PlayerForms.FAMILIAR_FOX_2 && curForm != PlayerForms.FAMILIAR_FOX_3){
                entity.slowMovement(state, multiplier);
            }
        }
        else{
            entity.slowMovement(state, multiplier);
        }
    }
}

