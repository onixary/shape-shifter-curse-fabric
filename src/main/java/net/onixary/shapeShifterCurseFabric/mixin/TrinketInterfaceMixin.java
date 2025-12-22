package net.onixary.shapeShifterCurseFabric.mixin;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 本来想挂个Event 结果发现需要的Event 1.20.1 版本没有 所以只能用Mixin了 可能部分物品会失效 取决于是否使用super.onEquip等方法
@Mixin(Trinket.class)
public class TrinketInterfaceMixin {
    @Unique
    public boolean CanAutoExecute(Identifier ItemID) {
        return TrinketUtils.getAccessoryMixinAuto(ItemID);
    }

    @Inject(method = "onEquip", at = @At("HEAD"))
    private void onEquipMixin(ItemStack stack, SlotReference slot, LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            Identifier ItemID = Registries.ITEM.getId(stack.getItem());
            if (CanAutoExecute(ItemID)) {
                TrinketUtils.ApplyAccessoryPowerOnEquip(player, ItemID);
            }
        }
    }

    @Inject(method = "onUnequip", at = @At("HEAD"))
    private void onUnequipMixin(ItemStack stack, SlotReference slot, LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            Identifier ItemID = Registries.ITEM.getId(stack.getItem());
            if (CanAutoExecute(ItemID)) {
                TrinketUtils.ApplyAccessoryPowerOnUnEquip(player, ItemID);
            }
        }
    }

}
