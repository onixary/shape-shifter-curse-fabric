package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = AccessoryItem.class, remap = false)
public class AccessoriesImpl implements Accessory {
    @Unique
    private static final HashMap<Integer, AccessoryItem.SlotData> slotDataCache = new HashMap<>();

    @Unique
    private AccessoryItem.SlotData getSlotData(SlotReference reference) {
        int key = reference.hashCode();
        if (slotDataCache.containsKey(key)) {
            return slotDataCache.get(key);
        }
        String slotName = reference.slotName();
        Identifier slotId = new Identifier("accessories", slotName);
        AccessoryItem.SlotData data = new AccessoryItem.SlotData(slotId, reference.slot());
        slotDataCache.put(key, data);
        return data;
    }

    @Inject(method = "accessoryInit", at = @At("HEAD"))
    private void initAccessory(Item.Settings settings, CallbackInfo ci) {
        AccessoryItem realThis = ((AccessoryItem) (Object) this);
        if (realThis instanceof Accessory accessory) {
            AccessoriesAPI.registerAccessory(realThis, accessory);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        ((AccessoryItem) (Object) this).accessoryTick(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        ((AccessoryItem) (Object) this).onEquip(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        ((AccessoryItem) (Object) this).onUnequip(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return ((AccessoryItem) (Object) this).canEquip(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        return ((AccessoryItem) (Object) this).canUnequip(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference reference) {
        ((AccessoryItem) (Object) this).onBreak(stack, reference.entity(), getSlotData(reference));
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        AccessoryItem.DropRule dropRule = ((AccessoryItem) (Object) this).getDropRule(stack, reference.entity(), getSlotData(reference));
        switch (dropRule) {
            case KEEP -> { return DropRule.KEEP; }
            case DROP -> { return DropRule.DROP; }
            case DESTROY -> { return DropRule.DESTROY; }
            default -> { return DropRule.DEFAULT; }
        }
    }
}