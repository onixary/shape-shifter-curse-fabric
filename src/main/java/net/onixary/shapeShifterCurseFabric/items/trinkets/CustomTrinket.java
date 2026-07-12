package net.onixary.shapeShifterCurseFabric.items.trinkets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.jetbrains.annotations.NotNull;

public class CustomTrinket extends AccessoryItem implements TrinketUtils.CustomPowerTrinketInterface {
    static {
        TrinketUtils.registerAccessoryMixinAuto(ShapeShifterCurseFabric.identifier("custom_trinket"), false);
    }
    private static final Identifier DEFAULT_IDENTIFIER = ShapeShifterCurseFabric.identifier("custom_trinket");

    public CustomTrinket(Settings settings) {
        super(settings);
    }

    private @NotNull Identifier getAccessoryID(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return DEFAULT_IDENTIFIER;
        }
        if (nbt.contains("custom_accessory_id")) {
            Identifier identifier = Identifier.tryParse(nbt.getString("custom_accessory_id"));
            if (identifier != null) {
                return identifier;
            }
        }
        return DEFAULT_IDENTIFIER;
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity entity, AccessoryItem.SlotData slot) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return false;
        }
        if (nbt.contains("custom_accessory_slots")) {
            NbtList slots = nbt.getList("custom_accessory_slots", 8);
            Identifier slotFinalName = slot.slot();
            for (int i = 0; i < slots.size(); i++) {
                if (slots.getString(i).equals(slotFinalName.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, LivingEntity entity, AccessoryItem.SlotData slot) {
        if (entity instanceof PlayerEntity player) {
            TrinketUtils.ApplyAccessoryPowerOnEquip(player, getAccessoryID(stack));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, LivingEntity entity, AccessoryItem.SlotData slot) {
        if (entity instanceof PlayerEntity player) {
            TrinketUtils.ApplyAccessoryPowerOnUnEquip(player, getAccessoryID(stack));
        }
    }

    @Override
    public void onFormChange(ItemStack stack, AccessoryItem.SlotData slot, PlayerEntity player) {
        TrinketUtils.ApplyAccessoryPowerOnPlayerFormChange(player, getAccessoryID(stack));
    }
}
