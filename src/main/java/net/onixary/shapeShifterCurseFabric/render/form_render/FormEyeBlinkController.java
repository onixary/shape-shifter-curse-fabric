package net.onixary.shapeShifterCurseFabric.render.form_render;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.config.ClientConfig;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public final class FormEyeBlinkController {
    private static final String EYE_ROOT_BONE = "eyeRoot";
    private static final float OPEN_SCALE = 1.0f;
    private static final float CLOSED_SCALE = 0.01f;
    private static final int DEFAULT_MIN_BLINK_INTERVAL_TICK = 60;
    private static final int DEFAULT_MAX_BLINK_INTERVAL_TICK = 140;
    private static final int DEFAULT_BLINK_TICK = 4;
    private static final Map<PlayerEntity, BlinkState> STATE_MAP = new WeakHashMap<>();

    private FormEyeBlinkController() {}

    public static void update(FormModel model, PlayerEntity player, float tickDelta) {
        GeoBone eyeRoot = model.getCachedGeoBone(EYE_ROOT_BONE);
        if (eyeRoot == null) {
            return;
        }

        BlinkState state = STATE_MAP.computeIfAbsent(player, ignored -> new BlinkState());
        state.update(player, tickDelta, BlinkConfig.fromClientConfig());
        applyScale(eyeRoot, state.currentScaleY);
    }

    public static void applyForRenderedBone(FormAnimatable animatable, GeoBone bone) {
        if (!EYE_ROOT_BONE.equals(bone.getName()) || animatable == null || animatable.e == null) {
            return;
        }

        BlinkState state = STATE_MAP.get(animatable.e);
        float scaleY = animatable.e.isSleeping() ? CLOSED_SCALE : state == null ? OPEN_SCALE : state.currentScaleY;
        applyScale(bone, scaleY);
    }

    private static void applyScale(GeoBone bone, float scaleY) {
        bone.setScaleX(OPEN_SCALE);
        bone.setScaleY(scaleY);
        bone.setScaleZ(OPEN_SCALE);
    }

    private static final class BlinkState {
        private final Random random = new Random();
        private int waitTicksRemaining = -1;
        private int blinkTicksElapsed = 0;
        private int lastAge = Integer.MIN_VALUE;
        private boolean blinking = false;
        private boolean wasSleeping = false;
        private float currentScaleY = OPEN_SCALE;

        private void update(PlayerEntity player, float tickDelta, BlinkConfig config) {
            if (player.isSleeping()) {
                this.wasSleeping = true;
                this.blinking = false;
                this.waitTicksRemaining = -1;
                this.blinkTicksElapsed = 0;
                this.lastAge = player.age;
                this.currentScaleY = CLOSED_SCALE;
                return;
            }

            if (this.lastAge == Integer.MIN_VALUE || this.wasSleeping) {
                scheduleNextWait(config);
                this.lastAge = player.age;
                this.wasSleeping = false;
            }

            if (player.age != this.lastAge) {
                int elapsedTicks = MathHelper.clamp(player.age - this.lastAge, 1, 100);
                for (int i = 0; i < elapsedTicks; i++) {
                    advanceOneTick(config);
                }
                this.lastAge = player.age;
            }

            this.currentScaleY = calculateScaleY(tickDelta, config);
        }

        private void advanceOneTick(BlinkConfig config) {
            if (this.blinking) {
                this.blinkTicksElapsed++;
                if (this.blinkTicksElapsed >= config.blinkTicks) {
                    this.blinking = false;
                    this.blinkTicksElapsed = 0;
                    scheduleNextWait(config);
                }
                return;
            }

            if (this.waitTicksRemaining > 0) {
                this.waitTicksRemaining--;
            }

            if (this.waitTicksRemaining <= 0) {
                this.blinking = true;
                this.blinkTicksElapsed = 0;
            }
        }

        private void scheduleNextWait(BlinkConfig config) {
            int bound = config.maxIntervalTicks - config.minIntervalTicks + 1;
            this.waitTicksRemaining = config.minIntervalTicks + this.random.nextInt(bound);
            this.blinking = false;
            this.blinkTicksElapsed = 0;
            this.currentScaleY = OPEN_SCALE;
        }

        private float calculateScaleY(float tickDelta, BlinkConfig config) {
            if (!this.blinking) {
                return OPEN_SCALE;
            }

            float progress = MathHelper.clamp((this.blinkTicksElapsed + tickDelta) / config.blinkTicks, 0.0f, 1.0f);
            float closeAmount = progress <= 0.5f ? progress * 2.0f : (1.0f - progress) * 2.0f;
            return MathHelper.lerp(closeAmount, OPEN_SCALE, CLOSED_SCALE);
        }
    }

    private static final class BlinkConfig {
        private final int minIntervalTicks;
        private final int maxIntervalTicks;
        private final int blinkTicks;

        private BlinkConfig(int minIntervalTicks, int maxIntervalTicks, int blinkTicks) {
            this.minIntervalTicks = minIntervalTicks;
            this.maxIntervalTicks = maxIntervalTicks;
            this.blinkTicks = blinkTicks;
        }

        private static BlinkConfig fromClientConfig() {
            ClientConfig config = ShapeShifterCurseFabric.clientConfig;
            int rawMinInterval = config == null ? DEFAULT_MIN_BLINK_INTERVAL_TICK : config.minBlinkIntervalTick;
            int rawMaxInterval = config == null ? DEFAULT_MAX_BLINK_INTERVAL_TICK : config.maxBlinkIntervalTick;
            int minInterval = Math.max(0, Math.min(rawMinInterval, rawMaxInterval));
            int maxInterval = Math.max(minInterval, Math.max(rawMinInterval, rawMaxInterval));
            int blinkTicks = Math.max(1, config == null ? DEFAULT_BLINK_TICK : config.blinkTick);
            return new BlinkConfig(minInterval, maxInterval, blinkTicks);
        }
    }
}
