package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Inhibitor  extends Item {
    public Inhibitor(Settings settings) {
        super(settings.maxCount(16)
                .food(
                new FoodComponent.Builder()
                        .hunger(2)
                        .saturationModifier(0.3f)
                        .alwaysEdible()
                        .build()
        ));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.canConsume(true)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 实际效果在ItemStackMixin的注入中进行处理
        super.finishUsing(stack, world, user);

        if (user instanceof PlayerEntity playerEntity) {
            if (playerEntity.getAbilities().creativeMode) {
                return stack;
            }
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.BOWL);
        } else {
            if (user instanceof PlayerEntity playerEntity) {
                if (!playerEntity.getInventory().insertStack(new ItemStack(Items.BOWL))) {
                    playerEntity.dropItem(new ItemStack(Items.BOWL), false);
                }
            }
            return stack;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.inhibitor.tooltip").formatted(Formatting.YELLOW));
    }
}