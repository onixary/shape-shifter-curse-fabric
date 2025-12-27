package net.onixary.shapeShifterCurseFabric.player_form;

import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class FormRandomSelector {
    private static final Random RANDOM = new Random();

    public static @Nullable PlayerFormBase getRandomForm(@NotNull Predicate<PlayerFormBase> filter) {
        List<PlayerFormBase> flitteredForms = RegPlayerForms.playerForms.values().stream().filter(filter).toList();
        if (flitteredForms.isEmpty()) {
            ShapeShifterCurseFabric.LOGGER.warn("No forms found for the given filter");
            return null;
        }
        return flitteredForms.get(RANDOM.nextInt(flitteredForms.size()));
    }

    // 省的每回添加形态后还得在这里的List中添加形态 上回就忘加了
    public static @NotNull PlayerFormBase getRandomForm_CurseMoon() {
        PlayerFormBase randomForm = getRandomForm(form -> (form.getPhase() == PlayerFormPhase.PHASE_0 && !form.getIsCustomForm()));
        if (randomForm == null) {
            throw new IllegalStateException("No forms available");  // 这只有Bug才会触发 代表形态系统重构没改这里 还是Throw出来比较好
        }
        return randomForm;
    }
}
