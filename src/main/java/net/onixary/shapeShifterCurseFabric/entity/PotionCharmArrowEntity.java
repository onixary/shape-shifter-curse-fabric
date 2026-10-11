package net.onixary.shapeShifterCurseFabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;

/** Renders the thrown charm; healing charms apply their potion without arrow impact damage. */
public class PotionCharmArrowEntity extends ArrowEntity implements FlyingItemEntity {
    private static final TrackedData<ItemStack> CHARM_STACK = DataTracker.registerData(PotionCharmArrowEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public PotionCharmArrowEntity(EntityType<? extends PotionCharmArrowEntity> type, World world) {
        super(type, world);
        pickupType = PickupPermission.DISALLOWED;
    }
    @Override public void setOwner(net.minecraft.entity.Entity owner) {
        super.setOwner(owner);
        // PersistentProjectileEntity enables pickup when a player owner is assigned.
        pickupType = PickupPermission.DISALLOWED;
    }
    @Override protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(CHARM_STACK, new ItemStack(Items.PAPER));
    }

    public void setCharmStack(ItemStack stack) {
        dataTracker.set(CHARM_STACK, stack.copyWithCount(1));
    }

    @Override public ItemStack getStack() { return dataTracker.get(CHARM_STACK); }

    @Override protected void onEntityHit(EntityHitResult hit) {
        if (!getStack().isOf(RegCustomItem.HEALING_CHARM)) {
            super.onEntityHit(hit);
            return;
        }
        if (!getWorld().isClient) {
            if (hit.getEntity() instanceof LivingEntity target) {
                onHit(target);
            }
            discard();
        }
    }

    @Override public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("CharmStack", getStack().writeNbt(new NbtCompound()));
    }

    @Override public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CharmStack")) {
            setCharmStack(ItemStack.fromNbt(nbt.getCompound("CharmStack")));
        }
    }
}
