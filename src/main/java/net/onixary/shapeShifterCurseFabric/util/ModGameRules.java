package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModGameRules {
    public static final CustomGameRuleCategory SSC_CATEGORY = new CustomGameRuleCategory(ShapeShifterCurseFabric.identifier("gamerule"), Text.translatable("gamerule.category.ssc_gamerule"));

    public static final GameRules.Key<GameRules.BooleanRule> USE_INITIAL_FORM = GameRuleRegistry.register(
            "sscUseInitialForm",
            SSC_CATEGORY,
            GameRuleFactory.createBooleanRule(true)
    );

    public static void register() {
    }
}
