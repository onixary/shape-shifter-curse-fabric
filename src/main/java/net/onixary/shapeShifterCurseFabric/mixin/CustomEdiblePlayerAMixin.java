package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(LivingEntity.class)
public class CustomEdiblePlayerAMixin {
    @Shadow
    protected ItemStack activeItemStack;

    @ModifyExpressionValue(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isFood()Z"))
    private boolean eatFood$isFood(boolean original, World world, ItemStack stack) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_01");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            return getPowerFoodComponent(playerEntity, stack) != null || original;
        }
        return original;
    }

    @ModifyExpressionValue(method = "applyFoodEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
    private boolean applyFoodEffects$isFood(boolean original, ItemStack stack, World world, LivingEntity targetEntity) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_02");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            return getPowerFoodComponent(playerEntity, stack) != null || original;
        }
        return original;
    }

    @ModifyExpressionValue(method = "applyFoodEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
    private FoodComponent applyFoodEffects$getFoodComponent(FoodComponent original, ItemStack stack, World world, LivingEntity targetEntity) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_03");
        if (targetEntity instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, stack);
            if (fc == null) {
                return original;
            }
            return fc;
        }
        return original;
    }

    @ModifyExpressionValue(method = "shouldSpawnConsumptionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"))
    private int shouldSpawnConsumptionEffects$getMaxUseTime(int original) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_04");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return original;
            }
            return fc.isSnack() ? 16 : 32;
        }
        return original;
    }

    @ModifyExpressionValue(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"))
    private int onTrackedDataSet$getMaxUseTime(int original) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_05");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return original;
            }
            return fc.isSnack() ? 16 : 32;
        }
        return original;
    }

    // 和Lodestone冲突
    // 可惜协议是LGPL3的 我没法把有问题的代码复制出来解释
    // https://github.com/LodestarMC/Lodestone/blob/1.20.1-fabric/src/main/java/team/lodestar/lodestone/mixin/common/LivingEntityMixin.java
    // 中的lodestone$injectUseEvent会直接ci.cancel 我ModifyExpressionValue优先度没Inject高

    @ModifyExpressionValue(method = "setCurrentHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"))
    private int setCurrentHand$getMaxUseTime(int original) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_06");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return original;
            }
            return fc.isSnack() ? 16 : 32;
        }
        return original;
    }

    @ModifyExpressionValue(method = "shouldSpawnConsumptionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
    private FoodComponent shouldSpawnConsumptionEffects$getFoodComponent(FoodComponent original) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_07");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return original;
            }
            return fc;
        }
        return original;
    }

    @ModifyExpressionValue(method = "spawnConsumptionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"))
    private UseAction spawnConsumptionEffects$getUseAction(UseAction original, ItemStack stack, int particleCount) {
        // ShapeShifterCurseFabric.LOGGER.error("SSC_CE_SYSTEM_CEPA_08");
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return original;
            }
            return UseAction.EAT;
        }
        return original;
    }
}
