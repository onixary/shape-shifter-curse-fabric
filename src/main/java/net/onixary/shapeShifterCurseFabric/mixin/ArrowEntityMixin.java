package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowEntity.class)
public class ArrowEntityMixin {
    @Unique
    private boolean IsCTPArrow = false;

    @Unique
    private Identifier CTPFormID = null;

    @Inject(method = "initFromStack", at = @At("HEAD"))
    public void initFromStack(ItemStack stack, CallbackInfo ci) {
        Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(stack.getNbt());
        if (CTPFormID != null) {
            IsCTPArrow = true;
            this.CTPFormID = CTPFormID;
        }
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    public void onHit(LivingEntity target, CallbackInfo ci) {
        if (IsCTPArrow && target instanceof PlayerEntity player) {
            CTPUtils.setTransformativePotionForm(player, CTPFormID);
        }
    }

    @Inject(method = "asItemStack", at = @At("RETURN"))
    public void asItemStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (IsCTPArrow && stack.getItem().equals(Items.TIPPED_ARROW)) {
            CTPUtils.setCTPFormIDToNBT(stack.getNbt(), CTPFormID);
        }
    }
}
