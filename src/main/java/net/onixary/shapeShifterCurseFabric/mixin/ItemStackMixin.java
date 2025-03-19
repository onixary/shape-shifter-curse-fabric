package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformRelatedItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**
     * 注入到物品使用完成时的逻辑
     * @param world 当前世界
     * @param user 使用物品的实体（可能是玩家）
     */
    @Inject(
            method = "finishUsing",
            at = @At("TAIL") // 在方法执行完毕后注入
    )
    private void shape_shifter_curse$onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        // 仅在服务端且使用者为玩家时处理
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ItemStack stack = (ItemStack) (Object) this;

            if(stack.getItem() == TransformRelatedItems.TRANSFORM_CURE){
                TransformRelatedItems.OnUseCure(player);
            }

            if(stack.getItem() == TransformRelatedItems.TRANSFORM_CURE_FINAL){
                TransformRelatedItems.OnUseCureFinal(player);
            }

            if(stack.getItem() == TransformRelatedItems.TRANSFORM_CATALYST){
                TransformRelatedItems.OnUseCatalyst(player);
            }
        }
    }
}
