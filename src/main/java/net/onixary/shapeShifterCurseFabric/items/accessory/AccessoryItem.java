package net.onixary.shapeShifterCurseFabric.items.accessory;

import com.google.common.collect.Multimap;
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
    }

    public abstract void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData);

    public abstract void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData);

    public abstract void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData);

    public abstract boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slotData);

    public abstract boolean canUnequip(ItemStack stack, LivingEntity entity, SlotData slotData);

    public boolean enableCustomAttributeModifiers() {
        return false;
    }

    public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, LivingEntity entity, SlotData slotData, UUID uuid);

    public abstract boolean onBreak(ItemStack stack, LivingEntity entity, SlotData slotData);

    public abstract DropRule getDropRule(ItemStack stack, LivingEntity entity, SlotData slotData);
}
