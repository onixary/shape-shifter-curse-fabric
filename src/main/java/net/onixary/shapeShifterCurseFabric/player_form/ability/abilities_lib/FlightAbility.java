package net.onixary.shapeShifterCurseFabric.player_form.ability.abilities_lib;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FlightAbility extends StatusEffect {
    public static final AbilitySource FLIGHT_POTION = Pal.getAbilitySource(Identifier.of(MOD_ID, "flight_ability"));

    public FlightAbility(StatusEffectCategory statusEffectType, int color) {
        super(statusEffectType, color);
    }

    @Override
    public void onApplied(LivingEntity effected, AttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onApplied(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof ServerPlayerEntity sp) {
            Pal.grantAbility(sp, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }

    @Override
    public void onRemoved(LivingEntity effected, AttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onRemoved(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof ServerPlayerEntity sp) {
            Pal.revokeAbility(sp, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }
}
