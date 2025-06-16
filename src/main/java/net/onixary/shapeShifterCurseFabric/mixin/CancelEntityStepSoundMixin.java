package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class CancelEntityStepSoundMixin {
    /**
     * 拦截脚步声播放逻辑
     */
    @Inject(
            method = "playStepSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disablePlayerStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        // 类型检查确保是玩家实体
        if ((Object)this instanceof PlayerEntity) {
            PlayerForms currentForm = FormAbilityManager.getForm((PlayerEntity) (Object)this);
            if(currentForm == PlayerForms.ALLAY_SP || currentForm == PlayerForms.OCELOT_2 || currentForm == PlayerForms.OCELOT_3) {
                ci.cancel(); // 取消原方法执行
            }
        }
    }
}
