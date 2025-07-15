package net.onixary.shapeShifterCurseFabric.player_form;

import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FormRandomSelector {
    private static final Random RANDOM = new Random();
    // 避免选择到自定义的空形态，直接定义好一组可选形态
    private static final List<PlayerForms> forms = Arrays.asList(
            PlayerForms.FAMILIAR_FOX_0,
            PlayerForms.BAT_0,
            PlayerForms.AXOLOTL_0,
            PlayerForms.OCELOT_0
    );

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

    public static PlayerForms getRandomFormFromPredefined() {
        if (forms.isEmpty()) {
            throw new IllegalStateException("No predefined forms available");
        }
        PlayerForms randomForm = forms.get(RANDOM.nextInt(forms.size()));
        ShapeShifterCurseFabric.LOGGER.info("Random predefined form selected: " + randomForm);
        return randomForm;
    }
}
