package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IFormGroup {
    @NotNull Map<Integer, List<Pair<Integer, IForm>>> getGroupData();

    default @NotNull IFormGroup registerForm(int formTier, int formWeight, IForm form) {
        getGroupData().computeIfAbsent(formTier, k -> new ArrayList<>()).add(new Pair<>(formWeight, form));
        form.setFormGroup(this, formTier);
        return this;
    }

    default @Nullable List<Pair<Integer, IForm>> getFormWeightList(int formTier) {
        return getGroupData().get(formTier);
    }

    default @Nullable List<IForm> getFormList(int formTier) {
        List<Pair<Integer, IForm>> formWeightList = getFormWeightList(formTier);
        if (formWeightList == null) return null;
        List<IForm> forms = new ArrayList<>();
        for (Pair<Integer, IForm> formWeight : formWeightList) {
            forms.add(formWeight.getRight());
        }
        return forms;
    }

    default @Nullable IForm getRandomForm(int formTier, Random random, @Nullable Predicate<IForm> predicate) {
        List<Pair<Integer, IForm>> formWeightList = getFormWeightList(formTier);
        if (formWeightList == null) return null;
        List<Pair<Integer, IForm>> eligible = new ArrayList<>();
        for (Pair<Integer, IForm> pair : formWeightList) {
            IForm form = pair.getRight();
            if (predicate == null || predicate.test(form)) {
                eligible.add(pair);
            }
        }
        if (eligible.isEmpty()) return null;
        int totalWeight = 0;
        for (Pair<Integer, IForm> pair : eligible) {
            Integer weight = pair.getLeft();
            if (weight != null && weight > 0) {
                totalWeight += weight;
            }
        }
        if (totalWeight <= 0) return null;
        int randomValue = random.nextInt(totalWeight);
        int cumulative = 0;
        for (Pair<Integer, IForm> pair : eligible) {
            Integer weight = pair.getLeft();
            if (weight != null && weight > 0) {
                cumulative += weight;
                if (randomValue < cumulative) {
                    return pair.getRight();
                }
            }
        }
        return null;
    }
}
