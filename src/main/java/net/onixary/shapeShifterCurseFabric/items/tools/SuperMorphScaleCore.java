package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuperMorphScaleCore extends Item implements Vanishable {
    public static final int damagePerItem = 64;

    public SuperMorphScaleCore(Settings settings) {
        super(settings);
    }

    public static int getMaxUseCount(ItemStack stack, int multiplier) {
        int damage = (stack.getMaxDamage() - stack.getDamage());
        int damagePerCount = damagePerItem * multiplier;
        return damage / damagePerCount;
    }

    public static int getUpgradeDamageMultiplier(ItemStack stack) {
        int upgradeItemStackCount = stack.getMaxCount();
        if (upgradeItemStackCount == 0) {
            return 1;
        }
        return 64 / upgradeItemStackCount;
    }

    public static void damageItemAfterUpgrade(ItemStack stack, int multiplier) {
        int damagePerCount = damagePerItem * multiplier;
        int damage = stack.getDamage();
        int targetDamage = damage + damagePerCount;
        if (targetDamage >= stack.getMaxDamage()) {
            stack.setDamage(stack.getMaxDamage());
        }
        else {
            stack.setDamage(targetDamage);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.super_morphscale_core.tooltip", getMaxUseCount(stack, 1)).formatted(Formatting.DARK_PURPLE));
    }
}
