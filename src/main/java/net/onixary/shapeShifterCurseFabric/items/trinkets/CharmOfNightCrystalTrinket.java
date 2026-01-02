package net.onixary.shapeShifterCurseFabric.items.trinkets;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CharmOfNightCrystalTrinket extends TrinketItem {
    public CharmOfNightCrystalTrinket(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.charm_of_night_crystal.tooltip").formatted(Formatting.YELLOW));
    }
}
