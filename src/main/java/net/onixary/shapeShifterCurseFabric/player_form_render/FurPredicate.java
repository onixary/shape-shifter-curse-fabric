package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.minecraft.util.Identifier;

import java.util.UUID;

public class FurPredicate {
    @FunctionalInterface
    public interface Predicate_T  {
        boolean test(OriginFurModel model, Identifier thisid, UUID ent_uuid);
    }
    Predicate_T predicate;
    public FurPredicate(Predicate_T predicate) {
        this.predicate = predicate;
    }
    public final boolean test(OriginFurModel model, Identifier thidid, UUID ent_uuid) {
        return predicate.test(model, thidid, ent_uuid);
    }
}
