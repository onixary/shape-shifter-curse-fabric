package net.onixary.shapeShifterCurseFabric.render.form_render.sub_controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.config.ClientConfig;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormModel;
import net.onixary.shapeShifterCurseFabric.util.util.CachedDataMap;
import net.onixary.shapeShifterCurseFabric.util.util.ICachedDataMap;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class FormEyeBlinkController {
    public final List<String> EYE_ROOT_BONES = new ArrayList<>();
    public float OPEN_SCALE = 1.0f;
    public float CLOSED_SCALE = 0.01f;
    private static final int DEFAULT_MIN_BLINK_INTERVAL_TICK = 60;
    private static final int DEFAULT_MAX_BLINK_INTERVAL_TICK = 140;
    private static final int DEFAULT_BLINK_TICK = 4;
    private static final ICachedDataMap<UUID, PlayerEntity, BlinkState> STATE_MAP = new CachedDataMap<>(player -> new BlinkState(), Entity::getUuid);

    // "eye_blink": {
    //     "eye": ["eyeRoot", "extraEyeRoot"], // 单个骨骼仍可写成 "eye": "eyeRoot"
    //     "open_scale": 1.0,
    //     "close_scale": 0.01
    // }

    public FormEyeBlinkController(JsonObject jsonObject) {
        if (jsonObject.has("eye")) {
            JsonElement eye = jsonObject.get("eye");
            if (eye.isJsonArray()) {
                for (JsonElement bone : eye.getAsJsonArray()) {
                    if (!bone.isJsonPrimitive() || !bone.getAsJsonPrimitive().isString()) {
                        throw new IllegalArgumentException("eye_blink.eye array must contain bone name strings");
                    }
                    EYE_ROOT_BONES.add(bone.getAsString());
                }
            } else {
                EYE_ROOT_BONES.add(eye.getAsString());
            }
        }
        if (jsonObject.has("open_scale")) {
            OPEN_SCALE = jsonObject.get("open_scale").getAsFloat();
        }
        if (jsonObject.has("close_scale")) {
            CLOSED_SCALE = jsonObject.get("close_scale").getAsFloat();
        }
    }

    public void update(FormModel model, PlayerEntity player, float tickDelta) {
        BlinkState state = null;
        for (String boneName : EYE_ROOT_BONES) {
            GeoBone eyeRoot = model.getCachedGeoBone(boneName);
            if (eyeRoot == null) {
                continue;
            }
            // 每帧只更新一次眨眼状态，各眼组在自己的枢轴上同步缩放。
            if (state == null) {
                state = STATE_MAP.get(player);
                state.update(player, tickDelta, BlinkConfig.fromClientConfig(), this);
            }
            applyScale(eyeRoot, state.currentScaleY);
        }
    }

    private void applyScale(GeoBone bone, float scaleY) {
        bone.setScaleY(scaleY);
    }

    private static final class BlinkState {
        private final Random random = new Random();
        private int waitTicksRemaining = -1;
        private int blinkTicksElapsed = 0;
        private int lastAge = Integer.MIN_VALUE;
        private boolean blinking = false;
        private boolean wasSleeping = false;
        private float currentScaleY = 1.0f;

        private void update(PlayerEntity player, float tickDelta, BlinkConfig config, FormEyeBlinkController controller) {
            if (player.isSleeping()) {
                this.wasSleeping = true;
                this.blinking = false;
                this.waitTicksRemaining = -1;
                this.blinkTicksElapsed = 0;
                this.lastAge = player.age;
                this.currentScaleY = controller.CLOSED_SCALE;
                return;
            }

            if (this.lastAge == Integer.MIN_VALUE || this.wasSleeping) {
                scheduleNextWait(config, controller);
                this.lastAge = player.age;
                this.wasSleeping = false;
            }

            if (player.age != this.lastAge) {
                int elapsedTicks = MathHelper.clamp(player.age - this.lastAge, 1, 100);
                for (int i = 0; i < elapsedTicks; i++) {
                    advanceOneTick(config, controller);
                }
                this.lastAge = player.age;
            }

            this.currentScaleY = calculateScaleY(tickDelta, config, controller);
        }

        private void advanceOneTick(BlinkConfig config, FormEyeBlinkController controller) {
            if (this.blinking) {
                this.blinkTicksElapsed++;
                if (this.blinkTicksElapsed >= config.blinkTicks) {
                    this.blinking = false;
                    this.blinkTicksElapsed = 0;
                    scheduleNextWait(config, controller);
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

        private void scheduleNextWait(BlinkConfig config, FormEyeBlinkController controller) {
            int bound = config.maxIntervalTicks - config.minIntervalTicks + 1;
            this.waitTicksRemaining = config.minIntervalTicks + this.random.nextInt(bound);
            this.blinking = false;
            this.blinkTicksElapsed = 0;
            this.currentScaleY = controller.OPEN_SCALE;
        }

        private float calculateScaleY(float tickDelta, BlinkConfig config, FormEyeBlinkController controller) {
            if (!this.blinking) {
                return controller.OPEN_SCALE;
            }

            float progress = MathHelper.clamp((this.blinkTicksElapsed + tickDelta) / config.blinkTicks, 0.0f, 1.0f);
            float closeAmount = progress <= 0.5f ? progress * 2.0f : (1.0f - progress) * 2.0f;
            return MathHelper.lerp(closeAmount, controller.OPEN_SCALE, controller.CLOSED_SCALE);
        }
    }

    private record BlinkConfig(int minIntervalTicks, int maxIntervalTicks, int blinkTicks) {
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
