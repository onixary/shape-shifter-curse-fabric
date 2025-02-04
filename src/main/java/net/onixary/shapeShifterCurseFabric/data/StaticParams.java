package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;

public class StaticParams {
    private StaticParams() {
    }
    // transformative effect default duration
    public static final int T_EFFECT_DEFAULT_DURATION = 400 * 20; // 400 seconds
    // transformative mob default attack damage
    public static final float CUSTOM_MOB_DEFAULT_DAMAGE = 0.5F;

    // attack range for non aggressive transformative mobs
    public static final double CUSTOM_MOB_DEFAULT_ATTACK_RANGE = 3.0;


    // transformative mob default emission particle
    public static final ParticleEffect CUSTOM_MOB_DEFAULT_PARTICLE = ParticleTypes.ENCHANT;

    // transformative mob default emission particle count
    public static final int CUSTOM_MOB_DEFAULT_PARTICLE_COUNT = 2;
}
