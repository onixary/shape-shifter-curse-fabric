package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.entity.projectile.WebBullet;

public class WebProjectileItem extends Item {
    public WebProjectileItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            WebBullet bullet = new WebBullet(player, 3);
            bullet.EnableVenomSpindle = false;
            bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0, 2, 1);
            if (!world.spawnEntity(bullet)) return TypedActionResult.fail(stack);
            if (!player.isCreative()) stack.decrement(1);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
