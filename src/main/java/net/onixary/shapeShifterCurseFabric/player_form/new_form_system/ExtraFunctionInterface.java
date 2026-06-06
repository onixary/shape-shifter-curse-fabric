package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import java.util.Objects;
import java.util.function.Function;

public interface ExtraFunctionInterface {
    @FunctionalInterface
    public static interface TriFunction<A1, A2, A3, R> {
        R apply(A1 a1, A2 a2, A3 a3);

        default <V> TriFunction<A1, A2, A3, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A1 a1, A2 a2, A3 a3) -> after.apply(apply(a1, a2, a3));
        }
    }
}
