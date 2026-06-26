package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanRule> USE_CONFIGURED_INITIAL_FORM = GameRuleRegistry.register(
            "sscUseConfiguredInitialForm",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(false)
    );

    public static void register() {
        // Loading this class registers the game rules.
    }
}
