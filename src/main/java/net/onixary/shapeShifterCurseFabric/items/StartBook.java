package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreen;

// 已弃用：都在幻形者之书中进行判断
public class StartBook  extends Item {
    public StartBook(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        if(!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen)) {
            StartBookScreen startScreen = new StartBookScreen();
            startScreen.currentPlayer = (ServerPlayerEntity) user;
            MinecraftClient.getInstance().setScreen(startScreen);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
