package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.render.tech.ItemStorePowerRender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RenderTrinketsSlotPower extends Power implements ItemStorePowerRender.itemStorePowerRenderInterface {
    private final String accessoryMod;
    private final int slot;
    private final String TGroup;
    private final String TSlot;
    private final int TSlotIndex;

    public RenderTrinketsSlotPower(PowerType<?> type, LivingEntity entity, String accessoryMod, int slot, String TGroup, String TSlot, int TSlotIndex) {
        super(type, entity);
        this.accessoryMod = accessoryMod;
        this.slot = slot;
        this.TGroup = TGroup;
        this.TSlot = TSlot;
        this.TSlotIndex = TSlotIndex;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public ItemStack getStack() {
        @Nullable List<ItemStack> items = AccessoryUtils.getEntitySlot(this.entity, this.accessoryMod, this.TGroup, this.TSlot);
        if (items == null) {
            return ItemStack.EMPTY;
        }
        if (this.TSlotIndex < 0 || this.TSlotIndex >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(this.TSlotIndex);
    }

    @Override
    public float getBobbingAnimationTime() {
        return 0;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("render_accessory_slot"),
                new SerializableData()
                        .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                        .add("accessory_group", SerializableDataTypes.STRING, "")
                        .add("accessory_slot", SerializableDataTypes.STRING, "")
                        .add("accessory_slot_index", SerializableDataTypes.INT, 0)
                        .add("slot", SerializableDataTypes.INT, 0),
                data -> (type, entity) -> new RenderTrinketsSlotPower(type, entity, data.getString("accessory_mod"), data.getInt("slot"), data.getString("accessory_group"), data.getString("accessory_slot"), data.getInt("accessory_slot_index"))
        ).allowCondition();
    }
}
