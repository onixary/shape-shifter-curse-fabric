package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.FormUpgradeScreen;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCostType implements IFUSDrawableCostType<ItemCostType> {
    private static final Identifier id = ShapeShifterCurseFabric.identifier("item");
    private static final ISprite itemIconSprite = new BaseSprite(FormUpgradeScreen.TEXTURE, FormUpgradeScreen.TEXTURE_WIDTH, FormUpgradeScreen.TEXTURE_HEIGHT, 434, 54, 18, 18);

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public void drawIcon(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z) {
        itemIconSprite.draw(context, x, y, z, 0, 0, 18, 18);
        if (!(costObject instanceof ItemCost cost)) {
            return;
        }
        ItemStack stack = cost.getExampleStack();
        if (stack.isEmpty()) {
            return;
        }
        context.drawItem(stack, x + 1, y + 1);
    }

    @Override
    public void drawOnHover(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY) {
        if (mouseX <= 0 || mouseX >= 18 || mouseY <= 0 || mouseY >= 18) {
            return;
        }
        if (!(costObject instanceof ItemCost cost)) {
            return;
        }
        ItemStack stack = cost.getExampleStack();
        if (stack.isEmpty()) {
            return;
        }
        context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, stack, x + mouseX, y + mouseY);
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        if (!(costObject instanceof ItemCost cost)) {
            throw new RuntimeException("ItemCostType.canPay costObject must be ItemCost");
        }
        ItemStack exampleStack = cost.getExampleStack();
        int amount = cost.getAmount();
        if (exampleStack.isEmpty() || amount <= 0) {
            return true;
        }
        if (player == null) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                player = ClientUtils.getPlayer();
            } else {
                throw new RuntimeException("CostType.canPay Player Argument In ServerSide Must NotNull");
            }
        }
        PlayerInventory playerInventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < playerInventory.size(); i++) {
            ItemStack stack = playerInventory.getStack(i);
            if (!stack.isEmpty() && ItemStack.canCombine(exampleStack, stack)) {
                total += stack.getCount();
                if (total >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull PlayerEntity player) {
        if (!(costObject instanceof ItemCost cost)) {
            throw new RuntimeException("ItemCostType.pay costObject must be ItemCost");
        }
        ItemStack exampleStack = cost.getExampleStack();
        int amount = cost.getAmount();
        if (exampleStack.isEmpty() || amount <= 0) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        int remaining = amount;
        for (int i = 0; i < playerInventory.size(); i++) {
            if (remaining <= 0) {
                break;
            }
            ItemStack stack = playerInventory.getStack(i);
            if (!stack.isEmpty() && ItemStack.canCombine(exampleStack, stack)) {
                int take = Math.min(stack.getCount(), remaining);
                stack.decrement(take);
                remaining -= take;
            }
        }
        if (remaining > 0) {
            ShapeShifterCurseFabric.LOGGER.warn("ItemCostType.pay: insufficient items, remaining {}", remaining);
        }
    }
}