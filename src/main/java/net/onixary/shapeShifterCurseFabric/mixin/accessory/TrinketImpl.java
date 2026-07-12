package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import dev.emi.trinkets.api.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

// Trinket的Api实现代码 需要Trinket加载 未加载时崩溃 所以需要在MixinConfigPlugin里声明一下

// XuHaoNan: 功能未完工 也没测试 就先不加入到mixin列表里了
// 还差Curio的代码 和对应的Power实现 对应的Utils和槽位渲染 不过优先级不大 等我有空再写吧 完工后再加入Mixin列表里
// 不负责DataPack的注册 所以如果需要双端需要把对应的DataPack里的Tag补上(减少兼容风险)
// 鉴于有Kilt的存在 所以Curio的兼容代码会在主分支上实现 我之后开发尝试一下仅编译导入依赖 如果不行就写一个ForgeMod

@Mixin(AccessoryItem.class)
public class TrinketImpl implements Trinket {
    @Unique
    private static final HashMap<Integer, AccessoryItem.SlotData> slotDataCache = new HashMap<>();

    @Unique
    private AccessoryItem.SlotData getSlotData(SlotReference slot) {
        if (slotDataCache.containsKey(slot.hashCode())) {
            return slotDataCache.get(slot.hashCode());
        }
        SlotType slotType = slot.inventory().getSlotType();
        AccessoryItem.SlotData data = new AccessoryItem.SlotData(new Identifier("trinket", "%s/%s".formatted(slotType.getGroup(), slotType.getName())), slot.index());
        slotDataCache.put(slot.hashCode(), data);
        return data;
    }

    @Inject(method = "accessoryInit", at = @At("HEAD"), cancellable = true)
    private void initAccessory(Item.Settings settings, CallbackInfo ci) {
        AccessoryItem realThis = ((AccessoryItem) (Object) this);
        if (realThis instanceof Trinket trinket) {
            TrinketsApi.registerTrinket(realThis, trinket);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).accessoryTick(stack, entity, getSlotData(slot));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onEquip(stack, entity, getSlotData(slot));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onUnequip(stack, entity, getSlotData(slot));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canEquip(stack, entity, getSlotData(slot));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canUnequip(stack, entity, getSlotData(slot));
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onBreak(stack, entity, getSlotData(slot));
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        AccessoryItem.DropRule dropRule = ((AccessoryItem) (Object) this).getDropRule(stack, entity, getSlotData(slot));
        switch (dropRule) {
            case KEEP -> {
                return TrinketEnums.DropRule.KEEP;
            }
            case DROP -> {
                return TrinketEnums.DropRule.DROP;
            }
            case DESTROY -> {
                return TrinketEnums.DropRule.DESTROY;
            }
            case DEFAULT -> {
                return TrinketEnums.DropRule.DEFAULT;
            }
            default -> {
                return TrinketEnums.DropRule.DEFAULT;
            }
        }
    }
}
