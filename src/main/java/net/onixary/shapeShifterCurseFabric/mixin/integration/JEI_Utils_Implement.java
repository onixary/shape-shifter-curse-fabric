package net.onixary.shapeShifterCurseFabric.mixin.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.integration.jei.JEI_Utils;
import net.onixary.shapeShifterCurseFabric.integration.jei.SSC_JEI_Plugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = JEI_Utils.class, remap = false)
public class JEI_Utils_Implement {
    /**
     * @author XuHaoNan
     * @reason 这样性能强一些 JEI_Utils无需JEI加载即可加载
     */
    @Overwrite
    public static void openJEIAltarRecipeScreen() {
        if (!JEI_Utils.isJEILoaded && !JEI_Utils.isEMILoaded) {
            return;
        }

        IJeiRuntime jeiRuntime = SSC_JEI_Plugin.getJeiRuntime();
        if (jeiRuntime == null) {
            return;
        }

        IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
        if (JEI_Utils.isEMILoaded) {
            IFocusFactory focusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
            IFocus<ItemStack> focus = focusFactory.createFocus(
                    RecipeIngredientRole.CATALYST,
                    VanillaTypes.ITEM_STACK,
                    new ItemStack(RegCustomBlock.ALTAR_BLOCK)
            );
            recipesGui.show(focus);
        } else {
            recipesGui.showTypes(List.of(
                    SSC_JEI_Plugin.ALTAR_SHAPED,
                    SSC_JEI_Plugin.ALTAR_SHAPELESS
            ));
        }
    }
}