package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    @Inject(method = "applySplashPotion", at = @At("HEAD"))
    public void applySplashPotion(List<StatusEffectInstance> statusEffects, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            PotionEntity realThis = ((PotionEntity) (Object) this);
            ItemStack stack = realThis.getStack();
            Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(stack.getNbt());
            if (CTPFormID != null) {
                CTPUtils.setTransformativePotionForm(player, CTPFormID);
            }
        }
        return;
    }

    @Inject(method = "applyLingeringPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setPotion(Lnet/minecraft/potion/Potion;)V"))
    public void applyLingeringPotion(ItemStack stack, Potion potion, CallbackInfo ci, @Local AreaEffectCloudEntity areaEffectCloudEntity) {
        Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(stack.getNbt());
        if (CTPFormID != null && areaEffectCloudEntity instanceof CTPUtils.CTPFormIDHolder) {
            ((CTPUtils.CTPFormIDHolder) areaEffectCloudEntity).setCTPFormID(CTPFormID);
        }
        return;
    }
}
