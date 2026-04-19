package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

// Trinket的Api实现代码 需要Trinket加载 未加载时崩溃 所以需要在MixinConfigPlugin里声明一下

// XuHaoNan: 功能未完工 也没测试 就先不加入到mixin列表里了
// 还差Curio的代码 和对应的Power实现 对应的Utils和槽位渲染 不过优先级不大 等我有空再写吧 完工后再加入Mixin列表里
// 不负责DataPack的注册 所以如果需要双端需要把对应的DataPack里的Tag补上(减少兼容风险)
// 鉴于有Kilt的存在 所以Curio的兼容代码会在主分支上实现 我之后开发尝试一下仅编译导入依赖 如果不行就写一个ForgeMod

@Mixin(AccessoryItem.class)
public class TrinketImpl implements Trinket {
    @Unique
    private static final HashMap<Integer, AccessoryItem.SlotData> slotDataCache = new HashMap<>();

    @Unique
    private AccessoryItem.SlotData getSlotData(SlotReference slot) {
        if (slotDataCache.containsKey(slot.hashCode())) {
            return slotDataCache.get(slot.hashCode());
        }
        SlotType slotType = slot.inventory().getSlotType();
        AccessoryItem.SlotData data = new AccessoryItem.SlotData(new Identifier("trinket", "%s/%s".formatted(slotType.getGroup(), slotType.getName())), slot.index());
        slotDataCache.put(slot.hashCode(), data);
        return data;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).accessoryTick(stack, entity, getSlotData(slot));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onEquip(stack, entity, getSlotData(slot));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onUnequip(stack, entity, getSlotData(slot));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canEquip(stack, entity, getSlotData(slot));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canUnequip(stack, entity, getSlotData(slot));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        AccessoryItem realThis = ((AccessoryItem) (Object) this);
        if (realThis.enableCustomAttributeModifiers()) {
            return realThis.getAttributeModifiers(stack, entity, getSlotData(slot), uuid);
        }

        // Trinket 的代码
        Multimap<EntityAttribute, EntityAttributeModifier> map = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
        if (stack.hasNbt() && stack.getNbt().contains("TrinketAttributeModifiers", 9)) {
            NbtList list = stack.getNbt().getList("TrinketAttributeModifiers", 10);

            for (int i = 0; i < list.size(); i++) {
                NbtCompound tag = list.getCompound(i);

                if (!tag.contains("Slot", NbtType.STRING) || tag.getString("Slot")
                        .equals(slot.inventory().getSlotType().getGroup() + "/" + slot.inventory().getSlotType().getName())) {
                    Optional<EntityAttribute> optional = Registries.ATTRIBUTE
                            .getOrEmpty(Identifier.tryParse(tag.getString("AttributeName")));

                    if (optional.isPresent()) {
                        EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromNbt(tag);

                        if (entityAttributeModifier != null
                                && entityAttributeModifier.getId().getLeastSignificantBits() != 0L
                                && entityAttributeModifier.getId().getMostSignificantBits() != 0L) {
                            map.put(optional.get(), entityAttributeModifier);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onBreak(stack, entity, getSlotData(slot));
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        AccessoryItem.DropRule dropRule = ((AccessoryItem) (Object) this).getDropRule(stack, entity, getSlotData(slot));
        switch (dropRule) {
            case KEEP -> {
                return TrinketEnums.DropRule.KEEP;
            }
            case DROP -> {
                return TrinketEnums.DropRule.DROP;
            }
            case DESTROY -> {
                return TrinketEnums.DropRule.DESTROY;
            }
            case DEFAULT -> {
                return TrinketEnums.DropRule.DEFAULT;
            }
            default -> {
                return TrinketEnums.DropRule.DEFAULT;
            }
        }
    }
}
