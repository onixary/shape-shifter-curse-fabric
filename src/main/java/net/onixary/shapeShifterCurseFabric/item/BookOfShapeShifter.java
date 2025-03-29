package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
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
        PlayerForms currentForm = user.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        if (world.isClient) {
            // 客户端逻辑：仅处理打开界面
            if (currentForm == PlayerForms.ORIGINAL_BEFORE_ENABLE) {
                if (!(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen)) {
                    StartBookScreen startScreen = new StartBookScreen();
                    startScreen.currentPlayer = user;
                    MinecraftClient.getInstance().setScreen(startScreen);
                }
            } else if (!(MinecraftClient.getInstance().currentScreen instanceof BookOfShapeShifterScreen)) {
                BookOfShapeShifterScreen bookScreen = new BookOfShapeShifterScreen();
                bookScreen.currentPlayer = user;
                MinecraftClient.getInstance().setScreen(bookScreen);
            }
        } else {
            // 服务端逻辑：触发成就
            if (currentForm != PlayerForms.ORIGINAL_BEFORE_ENABLE){
                if (user instanceof ServerPlayerEntity serverPlayer) {
                    ShapeShifterCurseFabric.ON_OPEN_BOOK_OF_SHAPE_SHIFTER.trigger(serverPlayer);
                }
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.book_of_shape_shifter.tooltip",Formatting.LIGHT_PURPLE));
    }

}
