package net.onixary.shapeShifterCurseFabric.player_form.instinct;

public enum InstinctEffectType {
    // 立即效果（值类型）
    COMBAT_HIT(15f),
    POTION_CALM(-20f),

    // 持续效果（速率类型）
    ENVIRONMENT_DANGER(0.2f),
    BUFF_RELAX(-0.1f);

    private final float value;

    InstinctEffectType(float value) {
        this.value = value;
    }

    public boolean isSustained() {
        return this == ENVIRONMENT_DANGER || this == BUFF_RELAX;
    }

    public float getValue() {
        return isSustained() ? 0f : value;
    }

    public float getRateModifier() {
        return isSustained() ? value : 0f;
    }
}
