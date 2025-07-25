package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class StaticParams {
    private StaticParams() {
    }
    // cursed moon data
    public static final int CURSED_MOON_INTERVAL_DAY = 5;
    // 概率期望时间约为1720秒 约等于3天
    public static final float CURSED_MOON_BASE_PROBABILITY = 0.0001f;
    public static final float CURSED_MOON_PROBABILITY_INCREASE = 0.00000042f;
    //public static final float CURSED_MOON_PROBABILITY_INCREASE = 0.0001f;
    public static final float CURSED_MOON_PROBABILITY_MAX = 0.05f;
    // ----------------------------------------
    // transformative effect default duration
    public static final int T_EFFECT_DEFAULT_DURATION = 400 * 20; // 400 seconds
    // ----------------------------------------
    // items related
    // ----------------------------------------
    public static final float MOONDUST_DROP_PROBABILITY = 0.45F;
    public static final float FAMILIAR_CURSE_POTION_DROP_PROBABILITY = 0.35F;
    // instinct setting
    public static final float INSTINCT_MAX = 100.0f;
    // per tick instinct increase
    //public static final float INSTINCT_INCREASE_RATE_0 = (INSTINCT_MAX / 2000.0f) / 20.0f;
    //public static final float INSTINCT_INCREASE_RATE_1 = (INSTINCT_MAX / 2000.0f) / 20.0f;
    public static final float INSTINCT_INCREASE_RATE_0 = (INSTINCT_MAX / 9000.0f) / 20.0f;
    public static final float INSTINCT_INCREASE_RATE_1 = (INSTINCT_MAX / 9000.0f) / 20.0f;
    // ----------------------------------------
    // FX settings
    public static final int TRANSFORM_FX_DURATION_IN = 3 * 20; // 2 seconds
    public static final int TRANSFORM_FX_DURATION_OUT = 5 * 20; // 4 seconds
    public static final ParticleEffect PLAYER_TRANSFORM_PARTICLE = ParticleTypes.ENCHANT;
    // ----------------------------------------
    // transformative mob settings
    // transformative mob default attack damage
    public static final float CUSTOM_MOB_DEFAULT_DAMAGE = 0.5F;

    // attack range for non aggressive transformative mobs
    public static final double CUSTOM_MOB_DEFAULT_ATTACK_RANGE = 3.0;

    // transformative mob default emission particle
    public static final ParticleEffect CUSTOM_MOB_DEFAULT_PARTICLE = ParticleTypes.ENCHANT;
    // ----------------------------------------
    // mod data
    // bat
    //public static final float T_BAT_REPLACE_PROBABILITY = 0.15F;
    //public static final float T_AXOLOTL_REPLACE_PROBABILITY = 0.4F;
}
