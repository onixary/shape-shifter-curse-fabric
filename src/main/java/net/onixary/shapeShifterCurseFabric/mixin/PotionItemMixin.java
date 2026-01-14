package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PotionItem.class)
public class PotionItemMixin {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof PlayerEntity player) {
            Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(stack.getNbt());
            if (CTPFormID != null) {
                CTPUtils.setTransformativePotionForm(player, CTPFormID);
            }
        }
    }

    @Inject(method = "appendTooltip", at = @At("RETURN"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (PotionUtil.getPotion(stack) != RegCustomPotions.CUSTOM_STATUE_FORM_POTION) {
            return;
        }
        Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(stack.getNbt());
        if (CTPFormID != null) {
            Text formName = Text.literal(RegPlayerForms.getPlayerFormOrDefault(CTPFormID, RegPlayerForms.ORIGINAL_BEFORE_ENABLE).name());
            // TODO 合并赞助者分支后使用下面的方法
            // Text formName = RegPlayerForms.getPlayerFormOrDefault(CTPFormID, RegPlayerForms.ORIGINAL_BEFORE_ENABLE).getFormName();
            tooltip.add(Text.translatable("tooltip.shape_shifter_curse.potion_target_form").append(formName));
        }
    }
}

