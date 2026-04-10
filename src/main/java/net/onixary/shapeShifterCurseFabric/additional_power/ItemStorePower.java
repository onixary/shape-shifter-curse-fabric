package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.render.tech.ItemStorePowerRender;

import java.util.function.Consumer;

public class ItemStorePower extends Power implements ItemStorePowerRender.itemStorePowerRenderInterface {
    public ItemStack storedItem = ItemStack.EMPTY;
    public final @Nullable Identifier powerID;
    public int bobbingAnimationTime = 0;
    public final int Slot;
    public final int VanillaSlotStart = 2800;

    public ItemStorePower(PowerType<?> type, LivingEntity entity, @Nullable Identifier powerID, int Slot) {
        super(type, entity);
        this.powerID = powerID;
        this.Slot = Slot;
        this.setTicking();
    }

    public void clientTick() {
        if (this.bobbingAnimationTime > 0) {
            this.bobbingAnimationTime -= 1;
        }
    }

    @Override
    public void tick() {
        if (this.bobbingAnimationTime > 0) {
            this.bobbingAnimationTime -= 1;
        }
        this.storedItem.inventoryTick(this.entity.getWorld(), this.entity, VanillaSlotStart + this.Slot, false);
    }

    public void SetItem(ItemStack stack) {
        if (this.entity.getWorld().isClient) {
            return;
        }
        this.storedItem = stack.copy();
        this.bobbingAnimationTime = 5;
        PowerHolderComponent.syncPower(this.entity, this.getType());
    }

    public void GainItem(ItemStack stack) {
        if (this.entity.getWorld().isClient) {
            return;
        }
        if (!this.storedItem.isEmpty()) {
            this.DropItem();
        }
        this.SetItem(stack);
    }

    public void DropItem() {
        if (this.entity.getWorld().isClient) {
            return;
        }
        if (!storedItem.isEmpty()) {
            this.entity.getWorld().spawnEntity(
                    new ItemEntity(
                            this.entity.getWorld(),
                            this.entity.getX(),
                            this.entity.getY(),
                            this.entity.getZ(),
                            this.storedItem
                    )
            );
            this.SetItem(ItemStack.EMPTY);
        }
    }

    public void SwapItem(EquipmentSlot slot) {
        if (this.entity.getWorld().isClient) {
            return;
        }
        ItemStack item = this.entity.getEquippedStack(slot);
        ItemStack stored = this.storedItem;
        this.SetItem(item);
        this.entity.equipStack(slot, stored);
    }

    public void InvokeItemAction(ActionFactory<Pair<World, ItemStack>>.Instance action) {
        if (this.entity.getWorld().isClient) {
            return;
        }
        if (action != null) {
            action.accept(new Pair<>(this.entity.getWorld(), this.storedItem));
        }
        PowerHolderComponent.syncPower(this.entity, this.getType());
    }

    @Override
    public void onLost() {
        super.onLost();
        this.DropItem();
    }


    @Override
    public NbtElement toTag() {
        NbtCompound tag = new NbtCompound();
        NbtCompound itemTag = new NbtCompound();
        this.storedItem.writeNbt(itemTag);
        tag.put("stored_item", itemTag);
        tag.putInt("bobbing_animation_time", this.bobbingAnimationTime);
        return tag;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if (tag instanceof NbtCompound compound) {
            NbtCompound itemStackNBT = compound.getCompound("stored_item");
            if (!itemStackNBT.isEmpty()) {
                this.storedItem = ItemStack.fromNbt(itemStackNBT);
            }
            this.bobbingAnimationTime = compound.getInt("bobbing_animation_time");
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("item_store"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("slot", SerializableDataTypes.INT, 0),
                data -> (type, entity) -> new ItemStorePower(type, entity, data.get("id"), data.getInt("slot"))
        ).allowCondition();
    }

    public static @Nullable ItemStorePower findPower(Entity entity, @Nullable Identifier powerID) {
        if (powerID == null) return null;
        if (entity instanceof LivingEntity livingEntity) {
            return PowerHolderComponent.getPowers(livingEntity, ItemStorePower.class).stream()
                    .filter(power -> power.powerID != null && power.powerID.equals(powerID))
                    .findFirst().orElse(null);
        }
        return null;
    }

    public static void registerCondition(Consumer<ConditionFactory<Entity>> registerFunc) {
        registerFunc.accept(new ConditionFactory<>(
                ShapeShifterCurseFabric.identifier("check_stored_item"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("default", SerializableDataTypes.BOOLEAN, false),
                (data, entity) -> {
                    ItemStorePower itemStorePower = findPower(entity, data.get("id"));
                    if (itemStorePower == null) return data.getBoolean("default");
                    ConditionFactory<ItemStack>.Instance condition = data.get("item_condition");
                    if (condition == null) return data.getBoolean("default");
                    return condition.test(itemStorePower.storedItem);
                }
        ));
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("gain_store_power_item"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("item", SerializableDataTypes.ITEM_STACK, null)
                        .add("if_no_power_drop", SerializableDataTypes.BOOLEAN, true),
                (data, entity) -> {
                    ItemStorePower itemStorePower = findPower(entity, data.get("id"));
                    if (itemStorePower != null) {
                        itemStorePower.GainItem(data.get("item"));
                    }
                    else if (data.getBoolean("if_no_power_drop")) {
                        entity.dropStack(data.get("item"));
                    }
                }
        ));

        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("drop_store_power_item"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("remove_item", SerializableDataTypes.BOOLEAN, false),
                (data, entity) -> {
                    ItemStorePower itemStorePower = findPower(entity, data.get("id"));
                    boolean removeItem = data.getBoolean("remove_item");
                    if (itemStorePower != null) {
                        if (removeItem) {
                            itemStorePower.storedItem = ItemStack.EMPTY;
                        } else {
                            itemStorePower.DropItem();
                        }
                    }
                }
        ));

        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("swap_store_power_item"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("slot", SerializableDataTypes.EQUIPMENT_SLOT, EquipmentSlot.MAINHAND),
                (data, entity) -> {
                    ItemStorePower itemStorePower = findPower(entity, data.get("id"));
                    if (itemStorePower != null) {
                        itemStorePower.SwapItem(data.get("slot"));
                    }
                }
        ));

        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("invoke_store_power_item"),
                new SerializableData()
                        .add("id", SerializableDataTypes.IDENTIFIER, null)
                        .add("action", ApoliDataTypes.ITEM_ACTION, null),
                (data, entity) -> {
                    ItemStorePower itemStorePower = findPower(entity, data.get("id"));
                    if (itemStorePower != null) {
                        itemStorePower.InvokeItemAction(data.get("action"));
                    }
                }
        ));
    }

    @Override
    public int getSlot() {
        return this.Slot;
    }

    @Override
    public ItemStack getStack() {
        return this.storedItem;
    }

    @Override
    public float getBobbingAnimationTime() {
        return this.bobbingAnimationTime;
    }
}
