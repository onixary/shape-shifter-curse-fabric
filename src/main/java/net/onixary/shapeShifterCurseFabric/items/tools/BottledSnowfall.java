package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// 形态专属工具只应用耐久度逻辑，其他逻辑由形态Power+手持道具condition实现
public class BottledSnowfall extends SwordItem {

    public BottledSnowfall(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.bottled_snowfall.tooltip").formatted(Formatting.YELLOW));
    }
}