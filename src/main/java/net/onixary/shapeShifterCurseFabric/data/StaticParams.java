package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class StaticParams {
    private StaticParams() {
    }
    // transformative effect default duration
    public static final int T_EFFECT_DEFAULT_DURATION = 400 * 20; // 400 seconds
    // ----------------------------------------
    // instinct setting
    public static final float INSTINCT_MAX = 100.0f;
    // per tick instinct increase
    public static final float INSTINCT_INCREASE_RATE_0 = (INSTINCT_MAX / 200.0f) / 20.0f;
    public static final float INSTINCT_INCREASE_RATE_1 = (INSTINCT_MAX / 100.0f) / 20.0f;
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
    public static final float T_BAT_REPLACE_PROBABILITY = 0.5F;
}
