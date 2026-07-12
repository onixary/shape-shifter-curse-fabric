package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Unique
    private static final List<TransformativeStatusInstance> tsiList = new ArrayList<>();

    @Inject(
            method = "finishUsing",
            at = @At("HEAD")
    )
    private void shape_shifter_curse$onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ItemStack stack = (ItemStack) (Object) this;
            if(stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE){
                IForm currentForm = FormUtils.getPlayerForm(player);
                if(!FormUtils.NoInstinct.hasFlag(currentForm) && !FormUtils.LockInstinct.hasFlag(currentForm)){
                    ShapeShifterCurseFabric.ON_USE_GOLDEN_APPLE.trigger(player);
                }
                if (EffectManager.hasTransformativeEffect(player)) {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.transformative_effect_cure").formatted(Formatting.YELLOW));
                    EffectManager.clearTransformativeEffect(player);
                }
            }
            else if (stack.getItem() == Items.MILK_BUCKET) {
                if (EffectManager.hasTransformativeEffect(player)) {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.milk_cannot_remove_effect").formatted(Formatting.YELLOW));
                    tsiList.clear();
                    Iterator<StatusEffectInstance> iterator = player.getStatusEffects().iterator();
                    while (iterator.hasNext()) {
                        StatusEffectInstance effectInstance = iterator.next();
                        if (effectInstance instanceof TransformativeStatusInstance tsi) {
                            tsiList.add(tsi);
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("TAIL"))
    private void shape_shifter_curse$onFinishUsingEnd(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            if (!tsiList.isEmpty()) {
                tsiList.forEach(tsi -> player.getActiveStatusEffects().put(tsi.getEffectType(), tsi));
                tsiList.clear();
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
