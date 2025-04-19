package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.*;

public class RegTStatusPotionEffect {
    private RegTStatusPotionEffect(){}

    public static final StatusEffect TO_BAT_0_POTION = register("to_bat_0_potion",new ToBatStatusPotion());
    public static final StatusEffect TO_AXOLOTL_0_POTION = register("to_axolotl_0_potion", new ToAxolotlStatusPotion());
    public static final StatusEffect TO_OCELOT_0_POTION = register("to_ocelot_0_potion", new ToOcelotStatusPotion());
    public static final StatusEffect TO_ALLAY_SP_POTION = register("to_allay_sp_potion", new ToAlleyStatusPotion());

    public static <T extends StatusEffect> T register(String path, T effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), effect);
    }

    public static void initialize() {}
}
