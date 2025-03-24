package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.custom_ui.BookOfShapeShifterScreen;
import net.onixary.shapeShifterCurseFabric.custom_ui.StartBookScreen;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BookOfShapeShifter extends Item {
    public BookOfShapeShifter(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        // 根据玩家当前形态判断要开启哪个screen
        PlayerForms currentForm = user.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        if(currentForm == PlayerForms.ORIGINAL_BEFORE_ENABLE){
            if(!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen)) {
                StartBookScreen startScreen = new StartBookScreen();
                startScreen.currentPlayer = user;
                MinecraftClient.getInstance().setScreen(startScreen);
            }
        }
        else if(!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreen)) {
            BookOfShapeShifterScreen bookScreen = new BookOfShapeShifterScreen();
            bookScreen.currentPlayer = user;
            MinecraftClient.getInstance().setScreen(bookScreen);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.book_of_shape_shifter.tooltip",Formatting.LIGHT_PURPLE));
    }

}
