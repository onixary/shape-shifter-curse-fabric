package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.spongepowered.asm.mixin.Unique;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;

public class CurioImpl implements ICurioItem {
    private static final HashMap<Integer, AccessoryItem.SlotData> slotDataCache = new HashMap<>();

    private AccessoryItem.SlotData getSlotData(SlotContext slot) {
        int SlotIDHash = slot.identifier().hashCode();
        if (slotDataCache.containsKey(SlotIDHash)) {
            return slotDataCache.get(SlotIDHash);
        }
        AccessoryItem.SlotData data = new AccessoryItem.SlotData(new Identifier("curios", slot.identifier()), slot.index());
        slotDataCache.put(SlotIDHash, data);
        return data;
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            aitem.accessoryTick(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
    }

    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            aitem.onEquip(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
    }

    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            aitem.onUnequip(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
    }

    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            return aitem.canEquip(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
        return true;
    }

    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            return aitem.canUnequip(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
        return true;
    }

    public void curioBreak(SlotContext slotContext, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            aitem.onBreak(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
        }
    }

    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        if (stack.getItem() instanceof AccessoryItem aitem) {
            AccessoryItem.DropRule rule = aitem.getDropRule(stack, (LivingEntity) (Object)slotContext.entity(), getSlotData(slotContext));
            switch (rule) {
                case KEEP -> {
                    return ICurio.DropRule.ALWAYS_KEEP;
                }
                case DROP -> {
                    return ICurio.DropRule.ALWAYS_DROP;
                }
                case DESTROY -> {
                    return ICurio.DropRule.DESTROY;
                }
                case DEFAULT -> {
                    return ICurio.DropRule.DEFAULT;
                }
            }
        }
        return ICurio.DropRule.DEFAULT;
    }
}
