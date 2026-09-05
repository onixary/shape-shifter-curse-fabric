package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class AlterOutputSlot extends Slot {
    public AlterOutputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
