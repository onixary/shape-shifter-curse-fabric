package net.onixary.shapeShifterCurseFabric.minion.mobs;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.minion.MinionBase;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

public class ManaReservoirEntity extends MinionBase implements FlyingItemEntity {
    public ManaReservoirEntity(EntityType<? extends ManaReservoirEntity> type, World world) {
        super(type, world);
        minionTypeID = ShapeShifterCurseFabric.identifier("mana_reservoir");
        setNoGravity(true);
    }
    public static DefaultAttributeContainer.Builder attributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0);
    }
    @Override protected void initGoals() {}
    @Override public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) { return null; }
    @Override public boolean isBreedingItem(ItemStack stack) { return false; }
    @Override public ItemStack getStack() { return new ItemStack(Items.AMETHYST_SHARD); }
    @Override public void tick() {
        setNoGravity(true);
        super.tick();
        if (!(getWorld() instanceof ServerWorld world) || !isAlive() || age % 20 != 0) return;
        for (var player : world.getPlayers()) {
            var form = FormUtils.getPlayerForm(player).getFormID();
            if (player.isAlive() && !player.isSpectator() && squaredDistanceTo(player) <= 16
                    && (form.equals(ShapeShifterCurseFabric.identifier("familiar_fox_2"))
                    || form.equals(ShapeShifterCurseFabric.identifier("familiar_fox_3")))) {
                ManaUtils.gainPlayerMana(player, 5);
            }
        }
        // Lifetime cost bypasses invulnerability frames and still runs without recipients.
        if (getHealth() <= 2) kill(); else setHealth(getHealth() - 2);
    }
}
