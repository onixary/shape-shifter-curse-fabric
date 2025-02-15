package net.onixary.shapeShifterCurseFabric.player_animation;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.registry.Registries;

// fetch from Animation_Overhaul mod
// https://github.com/nvb-uy/Animation_Overhaul/blob/main/src/main/java/elocindev/animation_overhaul/api/AnimationHolder.java
public class AnimationHolder {
    public static AnimationHolder EMPTY = new AnimationHolder((KeyframeAnimation) null, false);

    private float speed;
    private int fade;
    private boolean isEnabled;
    @Nullable private KeyframeAnimation animation;

    public AnimationHolder(Identifier animation_id, boolean isEnabled, float speed) {
        this(PlayerAnimationRegistry.getAnimation(animation_id), isEnabled, speed, 5);
    }

    public AnimationHolder(Identifier animation_id, boolean isEnabled) {
        this(PlayerAnimationRegistry.getAnimation(animation_id), isEnabled, 1.0f, 5);
    }

    public AnimationHolder(@Nullable KeyframeAnimation animation, boolean isEnabled, float speed, int fade) {
        this.isEnabled = isEnabled;
        this.animation = animation;
        this.speed = speed;
        this.fade = fade;
    }

    public AnimationHolder(@Nullable KeyframeAnimation animation, boolean isEnabled) {
        this(animation, isEnabled, 1.0f, 5);
    }

    public AnimationHolder() { this.isEnabled = false; this.animation = null; }

    @Nullable
    public KeyframeAnimation getAnimation() {
        if (!isEnabled) return null;

        return animation;
    }

    public AnimationHolder setAnimation(KeyframeAnimation animation) {
        this.animation = animation;

        return this;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public AnimationHolder setEnabled(boolean condition) {
        this.isEnabled = condition;

        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public AnimationHolder setSpeed(float speed) {
        this.speed = speed;

        return this;
    }

    public int getFade() {
        return fade;
    }

    public AnimationHolder setFade(int fade) {
        this.fade = fade;

        return this;
    }
}