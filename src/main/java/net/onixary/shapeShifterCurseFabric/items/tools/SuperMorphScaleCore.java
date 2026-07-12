package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuperMorphScaleCore extends Item implements Vanishable {
    public static final int damagePerItem = 64;
    public static final float mendingMultiplier = 2.0f;
    public static float quickChargeCostMultiplier = 0.75f;
    public static float quickChargeCostMultiplierNoMending = 0.20f;

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
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 24;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    // 我最早的Mod中的代码(没发布) 最后一次更新还是2年前了
    public static int getTotalExperience(int level, int xp) {
        int totalExp;
        if (level <= 16) {
            totalExp = level * level + 6 * level;
        }
        else if (level <= 31) {
            totalExp = (int) (2.5 * level * level - 40.5 * level + 360);
        }
        else {
            totalExp = (int) (4.5 * level * level - 162.5 * level + 2220);
        }
        int sum = totalExp + xp;
        return sum < 0 ? totalExp : sum;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && !world.isClient) {
            int damage = stack.getDamage();
            int need_repair = 0;
            if (user.isSneaking()) {
                need_repair = getMaxDamage();
            } else {
                need_repair = damagePerItem;
            }
            int max_repair = damage;
            need_repair = Math.min(need_repair, max_repair);
            float exp_multiplier = mendingMultiplier;
            if (EnchantmentHelper.getLevel(Enchantments.MENDING, stack) > 0) {
                exp_multiplier *= quickChargeCostMultiplier;
            } else {
                exp_multiplier *= quickChargeCostMultiplierNoMending;
            }
            int player_exp = getTotalExperience(player.experienceLevel, MathHelper.floor(player.experienceProgress * (float) player.getNextLevelExperience()));
            max_repair = MathHelper.floor(player_exp * exp_multiplier);
            need_repair = Math.min(need_repair, max_repair);
            int exp_cost = MathHelper.ceil(need_repair / exp_multiplier);
            if (need_repair > 0) {
                int finalDamage = damage - need_repair;
                if (finalDamage < 0) {
                    finalDamage = 0;
                }
                stack.setDamage(finalDamage);
                player.addExperience(-exp_cost);
                player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.super_morphscale_core.tooltip", getMaxUseCount(stack, 1)).formatted(Formatting.DARK_PURPLE));
    }
}
