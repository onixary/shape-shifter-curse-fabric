package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;


public class AuxiliaryAxe extends AxeItem {

    public AuxiliaryAxe(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

//    @Override
//    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
//        tooltip.add(Text.translatable("item.shape-shifter-curse.diamond_mining_claw.tooltip").formatted(Formatting.YELLOW));
//    }
}
