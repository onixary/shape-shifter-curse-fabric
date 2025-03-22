package net.onixary.shapeShifterCurseFabric.player_form;

import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FormRandomSelector {
    private static final Random RANDOM = new Random();

    public static PlayerForms getRandomFormWithIndexZero() {
        List<PlayerForms> formsWithIndexZero = Arrays.stream(PlayerForms.values())
                .filter(form -> form.getIndex() == 0)
                .collect(Collectors.toList());

        if (formsWithIndexZero.isEmpty()) {
            throw new IllegalStateException("No forms with index 0 found");
        }
        PlayerForms randomToForm = formsWithIndexZero.get(RANDOM.nextInt(formsWithIndexZero.size()));
        ShapeShifterCurseFabric.LOGGER.info("Random form with index 0 selected: " + randomToForm);
        return randomToForm;
    }
}
