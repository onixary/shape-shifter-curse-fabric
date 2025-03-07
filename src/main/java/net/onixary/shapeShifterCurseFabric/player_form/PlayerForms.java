package net.onixary.shapeShifterCurseFabric.player_form;

import java.util.Arrays;

public enum PlayerForms {
    // group: form kind, index: form phase, 2 is max
    ORIGINAL_BEFORE_ENABLE("original_form", -2),
    ORIGINAL_SHIFTER("base_form", -1),
    BAT_0("bat_form", 0),
    BAT_1("bat_form", 1),
    BAT_2("bat_form", 2);

    private final String group;
    private final int index;

    PlayerForms(String group, int index) {
        this.group = group;
        this.index = index;
    }

    public String getGroup() {
        return group;
    }

    public int getIndex() {
        return index;
    }

    public static PlayerForms[] getFormsByGroup(String group) {
        return Arrays.stream(values())
                .filter(form -> form.getGroup().equals(group))
                .toArray(PlayerForms[]::new);
    }
}
