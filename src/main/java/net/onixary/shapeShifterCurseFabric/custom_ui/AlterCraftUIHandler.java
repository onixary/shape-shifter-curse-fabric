package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.AlterOutputSlot;

public class AlterCraftUIHandler extends AbstractRecipeScreenHandler<SidedInventory> {
    public final PlayerInventory playerInventory;
    public final Inventory alterBlockEntity;
    public final ScreenHandlerContext context;
    public final PlayerEntity player;
    public final World world;

    public static AlterCraftUIHandler createMenu(int i, PlayerInventory inventory) {
        return new AlterCraftUIHandler(RegMenuType.AlterCraftUI, i, inventory, new SimpleInventory(11), ScreenHandlerContext.EMPTY);
    }

    public AlterCraftUIHandler(ScreenHandlerType<?> screenHandlerType, int syncId, PlayerInventory playerInventory, Inventory alterBlockEntity, ScreenHandlerContext context) {
        super(screenHandlerType, syncId);
        this.playerInventory = playerInventory;
        this.alterBlockEntity = alterBlockEntity;
        this.context = context;
        this.player = playerInventory.player;
        this.world = playerInventory.player.getWorld();

        this.addSlot(new AlterOutputSlot(this.alterBlockEntity, 10, 124, 35));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.alterBlockEntity, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        this.addSlot(new Slot(this.alterBlockEntity, 9, 152, 57));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        if (this.alterBlockEntity instanceof AlterBlockEntity realAlter) {
            realAlter.provideRecipeInputs(finder);
        }
    }

    @Override
    public void clearCraftingSlots() {
        for (int i = 0; i < this.alterBlockEntity.size(); ++i) {
            if (i == 9) {
                continue;
            }
            this.getSlot(i).setStackNoCallbacks(ItemStack.EMPTY);
        }
    }

    @Override
    public boolean matches(Recipe<? super SidedInventory> recipe) {
        if (this.alterBlockEntity instanceof AlterBlockEntity realAlter) {
            return recipe.matches(realAlter, world);
        }
        return false;
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 10;
    }

    @Override
    public int getCraftingWidth() {
        return 3;
    }

    @Override
    public int getCraftingHeight() {
        return 3;
    }

    @Override
    public int getCraftingSlotCount() {
        return 11;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != this.getCraftingResultSlotIndex();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // 有点麻烦 之后再写 先把逻辑跑通
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.CRAFTING_TABLE);
    }
}
