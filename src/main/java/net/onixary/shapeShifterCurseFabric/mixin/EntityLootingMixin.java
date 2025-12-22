package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyEntityLootPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class EntityLootingMixin {
    @Unique
    private ItemEntity DropLootStack(ItemStack stack) {
        LivingEntity RealThis = (LivingEntity)(Object)this;
        LivingEntity Attacker = RealThis.getAttacker();
        if (Attacker instanceof PlayerEntity player) {
            AtomicReference<ItemStack> FinalStack = new AtomicReference<>(stack);
            PowerHolderComponent.getPowers(player, ModifyEntityLootPower.class).forEach(
                    power -> FinalStack.set(power.ApplyModifyDrop(FinalStack.get(), RealThis.getRandom()))
            );
            return RealThis.dropStack(FinalStack.get());
        }
        return RealThis.dropStack(stack);
    }

    @ModifyArg(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"), index = 2)
    private Consumer<ItemStack> modifyLootTableArgs(Consumer<ItemStack> lootConsumer) {
        return this::DropLootStack;
    }
}
