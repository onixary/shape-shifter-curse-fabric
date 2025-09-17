package net.onixary.shapeShifterCurseFabric.player_form;

import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FormRandomSelector {
    private static final Random RANDOM = new Random();
    // 避免选择到自定义的空形态，直接定义好一组可选形态
    private static final List<PlayerFormBase> forms = Arrays.asList(
            RegPlayerForms.FAMILIAR_FOX_0,
            RegPlayerForms.BAT_0,
            RegPlayerForms.AXOLOTL_0,
            RegPlayerForms.OCELOT_0
    );

    public static PlayerFormBase getRandomFormWithIndexZero() {
        List<PlayerFormBase> formsWithIndexZero = RegPlayerForms.playerForms.getKeys().stream().filter(key -> RegPlayerForms.playerForms.getOrThrow(key).getIndex() == 0).map(RegPlayerForms.playerForms::getOrThrow).toList();

        if (formsWithIndexZero.isEmpty()) {
            throw new IllegalStateException("No forms with index 0 found");
        }
        PlayerFormBase randomToForm = formsWithIndexZero.get(RANDOM.nextInt(formsWithIndexZero.size()));
        ShapeShifterCurseFabric.LOGGER.info("Random form with index 0 selected: " + randomToForm);
        return randomToForm;
    }

    public static PlayerFormBase getRandomFormFromPredefined() {
        if (forms.isEmpty()) {
            throw new IllegalStateException("No predefined forms available");
        }
        PlayerFormBase randomForm = forms.get(RANDOM.nextInt(forms.size()));
        ShapeShifterCurseFabric.LOGGER.info("Random predefined form selected: " + randomForm);
        return randomForm;
    }
}
