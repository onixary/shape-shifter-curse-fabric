package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreen;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreen;

public class BookOfShapeShifter extends Item {
    public BookOfShapeShifter(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        if(!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreen)) {
            BookOfShapeShifterScreen bookScreen = new BookOfShapeShifterScreen();
            bookScreen.currentPlayer = user;
            MinecraftClient.getInstance().setScreen(bookScreen);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
