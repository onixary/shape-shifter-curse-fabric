package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/** Vanilla tipped-arrow behavior with a replaceable item-shaped placeholder renderer. */
public class PotionCharmArrowEntity extends ArrowEntity implements FlyingItemEntity {
    public PotionCharmArrowEntity(EntityType<? extends PotionCharmArrowEntity> type, World world) {
        super(type, world);
        pickupType = PickupPermission.DISALLOWED;
    }
    @Override public void setOwner(net.minecraft.entity.Entity owner) {
        super.setOwner(owner);
        // PersistentProjectileEntity enables pickup when a player owner is assigned.
        pickupType = PickupPermission.DISALLOWED;
    }
    @Override public ItemStack getStack() { return new ItemStack(Items.PAPER); }
}
