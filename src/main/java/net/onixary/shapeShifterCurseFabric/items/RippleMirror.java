package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RippleMirror extends Item {
    public RippleMirror(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 36;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && !user.isUsingItem()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.1f, 2.0f);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) return;
        boolean finishing = remainingUseTicks == 1;
        if (!finishing && remainingUseTicks % 5 != 0) return;

        float progress = (getMaxUseTime(stack) - remainingUseTicks) / (float) getMaxUseTime(stack);
        int count = finishing ? 32 : 4 + (int) (progress * 20);
        var random = user.getRandom();
        double centerY = user.getY() + user.getHeight() * 0.5;
        for (int i = 0; i < count; i++) {
            // Enchantment particles converge on the player as the charge builds.
            world.addParticle(ParticleTypes.ENCHANT, user.getX(), centerY, user.getZ(),
                    (random.nextDouble() - 0.5) * 3,
                    random.nextDouble() * 2,
                    (random.nextDouble() - 0.5) * 3);
            if (finishing || (progress >= 0.25f && i % 3 == 0)) {
                world.addParticle(ParticleTypes.END_ROD,
                        user.getX() + (random.nextDouble() - 0.5) * 1.5,
                        user.getY() + random.nextDouble() * user.getHeight(),
                        user.getZ() + (random.nextDouble() - 0.5) * 1.5,
                        (random.nextDouble() - 0.5) * 0.08,
                        0.03 + random.nextDouble() * 0.07,
                        (random.nextDouble() - 0.5) * 0.08);
            }
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 只有选中后发送变形包时才减少物品数量
        if (!world.isClient) {
            ModPacketsS2CServer.sendOpenSelectSubFormMenu((ServerPlayerEntity) user);
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.ripple_mirror.tooltip").formatted(Formatting.YELLOW));
    }
}
