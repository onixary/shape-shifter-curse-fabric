package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.IsMorphScaleItemCondition;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "finishUsing",
            at = @At("HEAD")
    )
    private void shape_shifter_curse$onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ItemStack stack = (ItemStack) (Object) this;
            if(stack.getItem() == Items.GOLDEN_APPLE){
                IForm currentForm = FormUtils.getPlayerForm(player);
                if(!FormUtils.NoInstinct.hasFlag(currentForm) && !FormUtils.LockInstinct.hasFlag(currentForm)){
                    ShapeShifterCurseFabric.ON_USE_GOLDEN_APPLE.trigger(player);
                }
            }
        }
    }

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void shape_shifter_curse$getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack realThis = (ItemStack) (Object) this;
        List<Text> tooltip = cir.getReturnValue();
        if (realThis.hasNbt()) {
            if (realThis.getNbt().contains(IsMorphScaleItemCondition.IsMorphScaleArmorTagName) && realThis.getNbt().getBoolean(IsMorphScaleItemCondition.IsMorphScaleArmorTagName)) {
                tooltip.add(Text.translatable("tooltip.shape_shifter_curse.morphscale_item").formatted(Formatting.GRAY));
            }
            if (realThis.getNbt().contains(IsMorphScaleItemCondition.IsMorphScaleFoodTagName) && realThis.getNbt().getBoolean(IsMorphScaleItemCondition.IsMorphScaleFoodTagName)) {
                tooltip.add(Text.translatable("tooltip.shape_shifter_curse.morphscale_food").formatted(Formatting.GRAY));
            }
        }
    }
}
