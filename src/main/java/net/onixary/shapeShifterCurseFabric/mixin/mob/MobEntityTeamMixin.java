package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.PillagerFriendlyPower;
import net.onixary.shapeShifterCurseFabric.additional_power.WitchFriendlyPower;
import net.onixary.shapeShifterCurseFabric.util.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MobEntityTeamMixin {
    @Inject(method = "isTeammate", at = @At("HEAD"), cancellable = true)
    private void onIsTeammateCheck(Entity other, CallbackInfoReturnable<Boolean> cir) {
        // `this` 在 Mixin 中指向 Mixin 类的实例，需要强制转换为 Entity
        Entity self = (Entity) (Object) this;

        // 检查目标实体是否为玩家
        if (other instanceof PlayerEntity player) {
            if(self instanceof LivingEntity livingEntity) {
                // 双重判断 提升其他Mod的兼容性 (没有就加Illager_Tag)
                if ((livingEntity.getGroup() == EntityGroup.ILLAGER || livingEntity.getType().isIn(ModTags.Illager_Tag)) && PowerHolderComponent.hasPower(player, PillagerFriendlyPower.class)) {
                    cir.setReturnValue(true);
                }
                if ((livingEntity instanceof WitchEntity || livingEntity.getType().isIn(ModTags.Witch_Tag)) && PowerHolderComponent.hasPower(player, WitchFriendlyPower.class)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

}
