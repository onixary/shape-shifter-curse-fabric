package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.EmptyController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class AnimUtils {
    public static class AnimationHolderData {
        public final Identifier AnimID;
        public final float Speed;
        public final int Fade;
        private @Nullable AnimationHolder animationHolder;
        public AnimationHolderData(Identifier AnimID, float Speed, int Fade) {
            this.AnimID = AnimID;
            this.Speed = Speed;
            this.Fade = Fade;
        }

        public AnimationHolderData(Identifier AnimID, float Speed) {
            this(AnimID, Speed, 2);
        }

        public AnimationHolderData(Identifier AnimID) {
            this(AnimID, 1.0f, 2);
        }

        public AnimationHolder build() {
            if (animationHolder == null) {
                animationHolder = new AnimationHolder(AnimID, true, Speed, Fade);
            }
            return animationHolder;
        }
    }

    public static final String ANIM_CONTROLLER_TYPE_KEY = "controllerType";

    private static class EmptyAnimationHolderData extends AnimationHolderData {
        public EmptyAnimationHolderData() {
            super(null, 0.0f, 0);
        }
        @Override
        public AnimationHolder build() {
            return null;
        }
    }

    public static AnimationHolderData EMPTY_ANIM = new EmptyAnimationHolderData();
    public static AbstractAnimStateController EMPTY_CONTROLLER = new EmptyController();

    public static @NotNull AnimationHolderData readAnim(JsonObject jsonData) {
        try {
            Identifier AnimID = Identifier.tryParse(jsonData.get("animID").getAsString());
            float Speed = 1.0f;
            int Fade = 2;
            if (jsonData.has("speed")) {
                Speed = jsonData.get("speed").getAsFloat();
            }
            if (jsonData.has("fade")) {
                Fade = jsonData.get("fade").getAsInt();
            }
            return new AnimUtils.AnimationHolderData(AnimID, Speed, Fade);
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Error while loading player animation: " + e.getMessage());
            return EMPTY_ANIM;
        }
    }

    // 还是防一下AnimationHolderData=null的情况吧
    public static @NotNull AnimationHolderData ensureAnimHolderDataNotNull(AnimationHolderData animationHolderData) {
        if (animationHolderData == null) {
            return EMPTY_ANIM;
        }
        else {
            return animationHolderData;
        }
    }

    public static @NotNull AnimationHolderData readAnimInJson(JsonObject jsonObject, String Key, @Nullable AnimationHolderData defaultValue) {
        if (ANIM_CONTROLLER_TYPE_KEY.equals(Key)) {
            throw new IllegalArgumentException("Cannot read animation from controllerType");
        }
        if (jsonObject.has(Key) && jsonObject.get(Key).isJsonObject()) {
            return readAnim(jsonObject.get(Key).getAsJsonObject());
        } else {
            return ensureAnimHolderDataNotNull(defaultValue);
        }
    }

    public static @NotNull AbstractAnimStateController readController(JsonObject jsonData) {
        try {
            Identifier ControllerType = Identifier.tryParse(jsonData.get(ANIM_CONTROLLER_TYPE_KEY).getAsString());
            Function<JsonObject, AbstractAnimStateController> controllerFactory = AnimRegistry.getAnimStateControllerSupplier(ControllerType);
            if (controllerFactory != null) {
                return controllerFactory.apply(jsonData);
            } else {
                ShapeShifterCurseFabric.LOGGER.warn("Unknown animation controller type: " + ControllerType);
                return EMPTY_CONTROLLER;
            }
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Error while loading player animation: " + e.getMessage());
            return EMPTY_CONTROLLER;
        }
    }
}
