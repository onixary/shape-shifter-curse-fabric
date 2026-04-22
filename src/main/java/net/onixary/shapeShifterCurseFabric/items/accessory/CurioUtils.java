package net.onixary.shapeShifterCurseFabric.items.accessory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;

import java.util.List;
import java.util.Map;

// 和 Accessory_ICurio 一样 得用外部编译mixin解决 希望能运行吧 如果不行我得新建mod了 那工作量有点多了
// 应该可以使用类似Trinket的系统 直接mixin AccessoryItem
public class CurioUtils {
    public static boolean isEquipped(LivingEntity entity, Item item) {
        return false;
    }

    public static Map<String, List<ItemStack>> getEntitySlots(LivingEntity entity) {
        return Map.of();
    }

    public static List<ItemStack> getEntitySlot(LivingEntity entity, String SlotName) {
        return List.of();
    }

    public static void setEntitySlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        return;
    }

    // 仅在此mod里实现功能 写forge版本的mixin时为空的 仅调用 这样那个Mod仅需要3个java文件应该就能复制环境
    public static void onPlayerEquip(PlayerEntity player, Identifier itemID) {
        TrinketUtils.ApplyAccessoryPowerOnEquip(player, itemID);
    }

    public static void onPlayerUnEquip(PlayerEntity player, Identifier itemID) {
        TrinketUtils.ApplyAccessoryPowerOnUnEquip(player, itemID);
    }

    public static boolean CanAutoExecute(Identifier itemID) {
        return TrinketUtils.getAccessoryMixinAuto(itemID);
    }
}
