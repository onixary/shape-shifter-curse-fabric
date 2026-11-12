package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoriesUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = AccessoriesUtils.class, remap = false)
public class AccessoriesUtilsImpl {
    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static boolean isLoaded() { return true; }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static boolean isEquipped(LivingEntity entity, Item item) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.isEquipped(item);
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static Map<String, List<ItemStack>> getEntitySlots(LivingEntity entity) {
        HashMap<String, List<ItemStack>> ret = new HashMap<>();
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            for (Map.Entry<String, AccessoriesContainer> entry : capability.getContainers().entrySet()) {
                ArrayList<ItemStack> itemList = new ArrayList<>();
                ExpandedSimpleContainer accessories = entry.getValue().getAccessories();
                for (int i = 0; i < accessories.size(); i++) {
                    itemList.add(accessories.getStack(i));
                }
                ret.put(entry.getKey(), itemList);
            }
        }
        return ret;
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static List<ItemStack> getEntitySlot(LivingEntity entity, String SlotName) {
        ArrayList<ItemStack> itemList = new ArrayList<>();
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                ExpandedSimpleContainer accessories = container.getAccessories();
                for (int i = 0; i < accessories.size(); i++) {
                    itemList.add(accessories.getStack(i));
                }
            }
        }
        return itemList;
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static void setEntitySlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                container.getAccessories().setStack(Index, stack);
            }
        }
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static Map<String, List<ItemStack>> getEntityCosmeticSlots(LivingEntity entity) {
        HashMap<String, List<ItemStack>> ret = new HashMap<>();
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            for (Map.Entry<String, AccessoriesContainer> entry : capability.getContainers().entrySet()) {
                ArrayList<ItemStack> itemList = new ArrayList<>();
                ExpandedSimpleContainer cosmetics = entry.getValue().getCosmeticAccessories();
                for (int i = 0; i < cosmetics.size(); i++) {
                    itemList.add(cosmetics.getStack(i));
                }
                ret.put(entry.getKey(), itemList);
            }
        }
        return ret;
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static List<ItemStack> getEntityCosmeticSlot(LivingEntity entity, String SlotName) {
        ArrayList<ItemStack> itemList = new ArrayList<>();
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                ExpandedSimpleContainer cosmetics = container.getCosmeticAccessories();
                for (int i = 0; i < cosmetics.size(); i++) {
                    itemList.add(cosmetics.getStack(i));
                }
            }
        }
        return itemList;
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static void setEntityCosmeticSlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                container.getCosmeticAccessories().setStack(Index, stack);
            }
        }
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static Map<String, List<Boolean>> getEntitySlotRenders(LivingEntity entity) {
        HashMap<String, List<Boolean>> ret = new HashMap<>();
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            for (Map.Entry<String, AccessoriesContainer> entry : capability.getContainers().entrySet()) {
                // 和CurioUtils一样 只读 虽然CurioUtil是因为返回值类型不太对
                ret.put(entry.getKey(), new ArrayList<>(entry.getValue().renderOptions()));
            }
        }
        return ret;
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static List<Boolean> getEntitySlotRender(LivingEntity entity, String SlotName) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                // 和CurioUtils一样 只读
                return new ArrayList<>(container.renderOptions());
            }
        }
        return new ArrayList<>();
    }

    /**
     * @author XuHaoNan
     * @reason 覆写性能更好
     */
    @Overwrite
    public static void setEntitySlotRender(LivingEntity entity, String SlotName, int Index, boolean render) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get(SlotName);
            if (container != null) {
                List<Boolean> renders = container.renderOptions();
                if (Index >= 0 && Index < renders.size()) {
                    renders.set(Index, render);
                }
            }
        }
    }
}