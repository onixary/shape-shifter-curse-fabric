package net.onixary.shapeShifterCurseFabric.mixin;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

// 本来想挂个Event 结果发现需要的Event 1.20.1 版本没有 所以只能用Mixin了 可能部分物品会失效 取决于是否使用super.onEquip等方法
@Mixin(TrinketItem.class)
public class TrinketItemMixin implements Trinket {
    @Unique
    public boolean CanAutoExecute(Identifier ItemID) {
        return TrinketUtils.getAccessoryMixinAuto(ItemID);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            Identifier ItemID = Registries.ITEM.getId(stack.getItem());
            if (CanAutoExecute(ItemID)) {
                TrinketUtils.ApplyAccessoryPowerOnEquip(player, ItemID);
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            Identifier ItemID = Registries.ITEM.getId(stack.getItem());
            if (CanAutoExecute(ItemID)) {
                TrinketUtils.ApplyAccessoryPowerOnUnEquip(player, ItemID);
            }
        }
    }
}
