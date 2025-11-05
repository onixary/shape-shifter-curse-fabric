package net.onixary.shapeShifterCurseFabric.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(HeldItemRenderer.class)
public class CustomEdibleHeldItemRendererMixin {
    @Shadow
    private MinecraftClient client;

    @ModifyExpressionValue(method = "renderFirstPersonItem", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"))
    private UseAction renderFirstPersonItem$getUseAction(UseAction original, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        return getPowerFoodComponent(player, item) != null ? UseAction.EAT : original;
    }

    @ModifyExpressionValue(method = "applyEatOrDrinkTransformation", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"))
    private int applyEatOrDrinkTransformation$getMaxUseTime(int original, MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
        FoodComponent fc = getPowerFoodComponent(client.player, stack);
        if (fc == null) {
            return original;
        }
        return fc.isSnack() ? 16 : 32;
    }
}
