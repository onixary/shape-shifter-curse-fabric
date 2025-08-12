package net.onixary.shapeShifterCurseFabric.player_form.instinct;

public enum InstinctEffectType {
    // 立即效果（值类型）
    FORM_OCELOT_ATTACK_LIVESTOCK(0.8f),
    FORM_FAMILIAR_FOX_ABSORB_ENERGY(2.5f),
    FORM_AXOLOTL_ATTACK_FISH(0.8f),
    FORM_SNOW_FOX_CRITICAL_ATTACK(1.0f),
    // 供自定义形态使用的效果
    FORM_INSTANT_INSTINCT_MEDIUM(3.0f),
    FORM_INSTANT_INSTINCT_LARGE(5.0f),

    // 持续效果（速率类型）
    // 催化剂与金苹果的生效时间都是2秒
    FORM_USE_GOLDEN_APPLE(-4.25f / 2),
    FORM_USE_CATALYST(1.95f / 2),

    FORM_BAT_IN_DARK(0.004f),
    FORM_BAT_IN_AIR(0.008f),
    FORM_BAT_EAT_FRUIT(0.1f),
    FORM_BAT_NEAR_DRIPSTONE(0.006f),
    FORM_BAT_BIOME(0.002f),

    FORM_FAMILIAR_FOX_EAT_BERRY(0.15f),
    FORM_FAMILIAR_FOX_IN_BERRY_BUSH(0.008f),
    FORM_FAMILIAR_FOX_MOVING_ON_SNOW(0.008f),
    FORM_FAMILIAR_FOX_LOOKING_WITCH(0.013f),
    FORM_FAMILIAR_FOX_BIOME(0.002f),

    FORM_AXOLOTL_IN_WATER(0.004f),
    FORM_AXOLOTL_EAT_FISH(0.1f),
    FORM_AXOLOTL_NEAR_DRIPLEAF(0.008f),
    FORM_AXOLOTL_BIOME(0.003f),

    FORM_OCELOT_EAT_RAW_MEAT(0.1f),
    FORM_OCELOT_ON_LEAF(0.008f),
    FORM_OCELOT_BIOME(0.003f),

    FORM_SNOW_FOX_SPRINT_JUMP(0.008f),

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
                || this == FORM_FAMILIAR_FOX_EAT_BERRY
                || this == FORM_FAMILIAR_FOX_IN_BERRY_BUSH
                || this == FORM_FAMILIAR_FOX_MOVING_ON_SNOW
                || this == FORM_FAMILIAR_FOX_LOOKING_WITCH
                || this == FORM_BAT_NEAR_DRIPSTONE
                || this == FORM_AXOLOTL_IN_WATER
                || this == FORM_AXOLOTL_NEAR_DRIPLEAF
                || this == FORM_AXOLOTL_EAT_FISH
                || this == FORM_USE_GOLDEN_APPLE
                || this == FORM_USE_CATALYST
                || this == FORM_OCELOT_EAT_RAW_MEAT
                || this == FORM_OCELOT_ON_LEAF
                || this == FORM_BAT_IN_AIR
                || this == FORM_BAT_BIOME
                || this == FORM_FAMILIAR_FOX_BIOME
                || this == FORM_AXOLOTL_BIOME
                || this == FORM_OCELOT_BIOME
                || this == FORM_SNOW_FOX_SPRINT_JUMP;
    }

    public float getValue() {
        return isSustained() ? 0f : value;
    }

    public float getRateModifier() {
        return isSustained() ? value : 0f;
    }
}
