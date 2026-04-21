package net.onixary.shapeShifterCurseFabric.items.accessory;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.UUID;

// 大概原理 使用Mixin为Trinket提供接口 使用外部CurioItem作为Curio的接口
// 理论上可以用接口的 但是为了最小化注入(使用接口得注入到Item里面 兼容风险较大) 还是用Class吧

public abstract class AccessoryItem extends Item {
    public static record SlotData(Identifier slot, int index) {}

    public static enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT
    }

    public AccessoryItem(Settings settings) {
        super(settings);
        this.accessoryInit(settings);
    }

    public void accessoryInit(Settings settings) {
        return;
    }

    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return true;
    }

    public boolean canUnequip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return !EnchantmentHelper.hasBindingCurse(stack);
    }

    public void onBreak(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return;
    }

    public DropRule getDropRule(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return DropRule.DEFAULT;
    }
}
