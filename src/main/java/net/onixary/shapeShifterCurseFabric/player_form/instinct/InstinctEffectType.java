package net.onixary.shapeShifterCurseFabric.player_form.instinct;

public enum InstinctEffectType {
    // 立即效果（值类型）
    COMBAT_HIT(15f),
    POTION_CALM(-20f),

    // 持续效果（速率类型）
    FORM_BAT_IN_DARK(0.005f),
    FORM_BAT_EAT_FRUIT(0.05f);

    private final float value;

    InstinctEffectType(float value) {
        this.value = value;
    }

    public boolean isSustained() {
        return this == FORM_BAT_IN_DARK
                || this == FORM_BAT_EAT_FRUIT;
    }

    public float getValue() {
        return isSustained() ? 0f : value;
    }

    public float getRateModifier() {
        return isSustained() ? value : 0f;
    }
}
