package net.onixary.shapeShifterCurseFabric.player_form.instinct;

public enum InstinctEffectType {
    // 立即效果（值类型）
    FORM_OCELOT_ATTACK_LIVESTOCK(3.0f),
    // 供自定义形态使用的效果
    FORM_INSTANT_INSTINCT_MEDIUM(3.0f),
    FORM_INSTANT_INSTINCT_LARGE(5.0f),

    // 持续效果（速率类型）
    // 催化剂与金苹果的生效时间都是2秒
    FORM_USE_GOLDEN_APPLE(-4.25f / 2),
    FORM_USE_CATALYST(1.25f / 2),

    FORM_BAT_IN_DARK(0.004f),
    FORM_BAT_EAT_FRUIT(0.1f),
    FORM_BAT_NEAR_DRIPSTONE(0.006f),

    FORM_AXOLOTL_IN_WATER(0.004f),
    FORM_AXOLOTL_EAT_FISH(0.1f),
    FORM_AXOLOTL_NEAR_DRIPLEAF(0.008f),

    FORM_OCELOT_EAT_RAW_MEAT(0.1f),
    FORM_OCELOT_ON_LEAF(0.008f),

    // 供自定义形态使用的效果
    FORM_SUSTAINED_INSTINCT_ENVIRONMENT_MEDIUM(0.004f),
    FORM_SUSTAINED_INSTINCT_ENVIRONMENT_LARGE(0.008f),
    FORM_SUSTAINED_INSTINCT_FOOD(0.1f);

    private final float value;

    InstinctEffectType(float value) {
        this.value = value;
    }

    public boolean isSustained() {
        return this == FORM_BAT_IN_DARK
                || this == FORM_BAT_EAT_FRUIT
                || this == FORM_BAT_NEAR_DRIPSTONE
                || this == FORM_AXOLOTL_IN_WATER
                || this == FORM_AXOLOTL_NEAR_DRIPLEAF
                || this == FORM_AXOLOTL_EAT_FISH
                || this == FORM_USE_GOLDEN_APPLE
                || this == FORM_USE_CATALYST
                || this == FORM_OCELOT_EAT_RAW_MEAT
                || this == FORM_OCELOT_ON_LEAF;
    }

    public float getValue() {
        return isSustained() ? 0f : value;
    }

    public float getRateModifier() {
        return isSustained() ? value : 0f;
    }
}
