package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(PlayerEntity.class)
public abstract class CustomEdiblePlayerBMixin extends LivingEntity {

    protected CustomEdiblePlayerBMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"), cancellable = true)
    private void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent foodComponent = getPowerFoodComponent(playerEntity, stack);
            if (foodComponent == null) {
                return;
            }
            playerEntity.getHungerManager().add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            if (playerEntity instanceof ServerPlayerEntity spe) {
                Criteria.CONSUME_ITEM.trigger(spe, stack);
            }
            cir.setReturnValue(super.eatFood(world, stack));
        }
    }
}
