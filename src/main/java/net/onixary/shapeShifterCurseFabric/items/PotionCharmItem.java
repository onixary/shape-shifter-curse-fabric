package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.entity.PotionCharmArrowEntity;
import net.onixary.shapeShifterCurseFabric.entity.RegCustomEntity;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

public class PotionCharmItem extends Item {
    private final Potion potion;
    public PotionCharmItem(Potion potion) { super(new Settings()); this.potion = potion; }
    @Override public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!FormUtils.getPlayerForm(player).getFormID().equals(ShapeShifterCurseFabric.identifier("familiar_fox_3"))) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            var arrow = new PotionCharmArrowEntity(RegCustomEntity.POTION_CHARM_ARROW, world);
            arrow.setOwner(player);
            arrow.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
            arrow.initFromStack(PotionUtil.setPotion(new ItemStack(Items.TIPPED_ARROW), potion));
            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0, 3, 0);
            if (!world.spawnEntity(arrow)) return TypedActionResult.fail(stack);
            if (!player.isCreative()) stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
