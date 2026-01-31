package net.onixary.shapeShifterCurseFabric.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.jetbrains.annotations.NotNull;

public class CustomTrinket extends TrinketItem implements TrinketUtils.CustomPowerTrinketInterface {
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

    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return false;
        }
        if (nbt.contains("custom_accessory_slots")) {
            NbtList slots = nbt.getList("custom_accessory_slots", 8);
            SlotType slotType = slot.inventory().getSlotType();
            String slotGroup = slotType.getGroup();
            String slotName = slotType.getName();
            String slotFinalName = slotGroup + "/" + slotName;
            for (int i = 0; i < slots.size(); i++) {
                if (slots.getString(i).equals(slotFinalName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            TrinketUtils.ApplyAccessoryPowerOnEquip(player, getAccessoryID(stack));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            TrinketUtils.ApplyAccessoryPowerOnUnEquip(player, getAccessoryID(stack));
        }
    }

    @Override
    public void onFormChange(ItemStack stack, SlotReference slot, PlayerEntity player) {
        TrinketUtils.ApplyAccessoryPowerOnPlayerFormChange(player, getAccessoryID(stack));
    }
}
