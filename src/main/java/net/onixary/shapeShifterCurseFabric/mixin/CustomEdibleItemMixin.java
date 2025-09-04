package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.CanEatAmethystShardPower;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class CustomEdibleItemMixin {

    boolean canConsumeCustomFood = false;

    @Unique
    private static final FoodComponent ALLAY_AMETHYST_SHARD_FOOD = new FoodComponent.Builder()
            .hunger(10)
            .alwaysEdible()
            .saturationModifier(0.0f)
            .build();

    @Inject(method = "getFoodComponent", at = @At("HEAD"), cancellable = true)
    private void onGetFoodComponent(CallbackInfoReturnable<FoodComponent> cir) {

        Item self = (Item)(Object)this;
        if (self == Items.AMETHYST_SHARD) {
            cir.setReturnValue(ALLAY_AMETHYST_SHARD_FOOD);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        PlayerForms currentForm = FormAbilityManager.getForm(user);
        canConsumeCustomFood = false;
        if(currentForm != null){

//            if (stack.getItem() == Items.AMETHYST_SHARD && currentForm == PlayerForms.ALLAY_SP) {
            if (stack.getItem() == Items.AMETHYST_SHARD && PowerHolderComponent.hasPower(user, CanEatAmethystShardPower.class)) {
                if (user.canConsume(ALLAY_AMETHYST_SHARD_FOOD.isAlwaysEdible())) {
                    user.setCurrentHand(hand);
                    canConsumeCustomFood = true;
                    cir.setReturnValue(TypedActionResult.consume(stack));
                } else {
                    canConsumeCustomFood = false;
                    cir.setReturnValue(TypedActionResult.fail(stack));
                }
            }
        }
        else{
            cir.setReturnValue(TypedActionResult.fail(stack));
        }

    }

    @ModifyReturnValue(method = "getUseAction", at = @At("RETURN"))
    private UseAction replaceUseAction(UseAction original) {
        return canConsumeCustomFood? UseAction.EAT : original;
    }

    @ModifyReturnValue(method = "getEatSound", at = @At("RETURN"))
    private SoundEvent replaceEatingSound(SoundEvent original) {
        return canConsumeCustomFood? SoundEvents.ENTITY_GENERIC_EAT: original;
    }

    /**
     * 设置食用耗时
     */
    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void onGetMaxUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if ((Object)this == Items.AMETHYST_SHARD) {
            cir.setReturnValue(32); // 32tick=1.6秒
        }
    }

    /**
     * 处理食用完成效果
     */
    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    private void onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(user instanceof PlayerEntity){
            PlayerForms currentForm = FormAbilityManager.getForm((PlayerEntity) user);
//            if ((stack.getItem() == Items.AMETHYST_SHARD) && (currentForm == PlayerForms.ALLAY_SP)) {
            if ((stack.getItem() == Items.AMETHYST_SHARD) && (PowerHolderComponent.hasPower(user, CanEatAmethystShardPower.class))) {
                if (user instanceof PlayerEntity player) {
                    player.getHungerManager().add(ALLAY_AMETHYST_SHARD_FOOD.getHunger(), ALLAY_AMETHYST_SHARD_FOOD.getSaturationModifier());
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.NEUTRAL,
                            1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);

                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                }
                cir.setReturnValue(stack);
            }
        }
    }
}

