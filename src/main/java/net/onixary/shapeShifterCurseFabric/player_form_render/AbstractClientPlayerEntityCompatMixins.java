package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public interface AbstractClientPlayerEntityCompatMixins {
    default boolean betterCombat$isAttacking() {return false;}
    default ArrayList<Origin> originfurs$getOrigins(){return new ArrayList<>();}
    default void originfurs$setOrigins(ArrayList<Identifier> origins){}
}
