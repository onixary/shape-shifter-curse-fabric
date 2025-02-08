package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.ability.abilities_lib.FlightAbility;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegFormAbilities {
    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "flight"), new FlightAbility(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    }
}
