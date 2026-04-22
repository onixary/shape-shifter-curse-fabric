package net.onixary.shapeShifterCurseFabric.items.trinkets;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CharmOfReverseThermometerTrinket extends AccessoryItem {
    public CharmOfReverseThermometerTrinket(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.charm_of_reverse_thermometer.tooltip").formatted(Formatting.YELLOW));
    }
}
