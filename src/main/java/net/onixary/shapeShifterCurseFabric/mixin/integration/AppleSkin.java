package net.onixary.shapeShifterCurseFabric.mixin.integration;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyFoodPower;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.FoodHelper;

import java.util.List;

@Mixin(FoodHelper.class)
// 排除逻辑在net.onixary.shapeShifterCurseFabric.mixin.plugin.MixinConfigPlugin里临时注册
public class AppleSkin {
    static {
        // 写一个理应不会抛异常的静态代码块，防止之后修改MixinConfigPlugin出错导致出现其他问题
        if (!FabricLoader.getInstance().isModLoaded("appleskin")) {
            throw new IllegalStateException("AppleSkin mixin was loaded but appleskin is not installed!");
        }
    }

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private static void shapeShifterCurseFabric$canConsume(ItemStack itemStack, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            FoodComponent itemFood = CustomEdibleUtils.getPowerFoodComponent(player, itemStack);
            boolean CanConsume = itemFood != null && player.canConsume(itemFood.isAlwaysEdible());
            if (CanConsume) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getModifiedFoodValues", at = @At("HEAD"), cancellable = true)
    private static void shapeShifterCurseFabric$getModifiedFoodValues(ItemStack itemStack, PlayerEntity player, CallbackInfoReturnable<FoodValues> cir) {
        if (player != null) {
            FoodComponent itemFood = CustomEdibleUtils.getPowerFoodComponent(player, itemStack);
            itemFood = itemFood != null ? itemFood : itemStack.getItem().getFoodComponent();
            int hunger = itemFood != null ? itemFood.getHunger() : 0;
            float saturationModifier = itemFood != null ? itemFood.getSaturationModifier() : 0.0F;
            List<ModifyFoodPower> mfps = PowerHolderComponent.getPowers(player, ModifyFoodPower.class);
            List<Modifier> modifiersFood = mfps.stream().filter((p) -> p.doesApply(itemStack)).flatMap((p) -> p.getFoodModifiers().stream()).toList();
            int newFood = (int) ModifierUtil.applyModifiers((Entity) player, modifiersFood, (double)hunger);
            List<Modifier> modifiersSaturation = mfps.stream().filter((p) -> p.doesApply(itemStack)).flatMap((p) -> p.getSaturationModifiers().stream()).toList();
            float newSaturation = (float) ModifierUtil.applyModifiers((Entity) player, modifiersSaturation, (double)saturationModifier);
            cir.setReturnValue(new FoodValues(newFood, newSaturation));
        }
    }
}
