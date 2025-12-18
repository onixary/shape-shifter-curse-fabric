package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.EmptyController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AnimUtils {
    public static class AnimationHolderData {
        public Identifier AnimID;
        public float Speed;
        public int Fade;
        private @Nullable AnimationHolder animationHolder;
        public AnimationHolderData(Identifier AnimID, float Speed, int Fade) {
            this.AnimID = AnimID;
            this.Speed = Speed;
            this.Fade = Fade;
        }

        public AnimationHolderData setAnimID(Identifier AnimID) {
            this.AnimID = AnimID;
            return this;
        }

        public AnimationHolderData setAnimID(AnimationHolderData otherAnimationHolder) {
            this.AnimID = otherAnimationHolder.AnimID;
            return this;
        }

        public AnimationHolderData setSpeed(float Speed) {
            this.Speed = Speed;
            return this;
        }

        public AnimationHolderData setFade(int Fade) {
            this.Fade = Fade;
            return this;
        }

        public AnimationHolderData makeCopy() {
            return new AnimationHolderData(AnimID, Speed, Fade);
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
                if (ShapeShifterCurseFabric.IsDevelopmentEnvironment() && animationHolder.getAnimation() == null)  {
                    ShapeShifterCurseFabric.LOGGER.warn("Animation " + AnimID + " not found!");
                }
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

    public enum AnimationSendSideType {
        ONLY_CLIENT((player -> player.getWorld().isClient)),
        ONLY_SERVER((player -> !player.getWorld().isClient)),
        NONE((player -> false)),
        BOTH_SIDE((player -> true));

        private final Function<PlayerEntity, Boolean> canPlayAnimCondition;

        public boolean canPlayAnim(PlayerEntity player) {
            return this.canPlayAnimCondition.apply(player);
        }

        AnimationSendSideType(Function<PlayerEntity, Boolean> canPlayAnimCondition) {
            this.canPlayAnimCondition = canPlayAnimCondition;
        }
    }

    public static boolean playPowerAnimWithTime(PlayerEntity playerEntity, Identifier powerAnimID, int animDuration, AnimationSendSideType sendSideType) {
        if (!sendSideType.canPlayAnim(playerEntity)) {
            return false;
        }
        if (playerEntity instanceof IPlayerAnimController playerAnimController) {
            playerAnimController.shape_shifter_curse$playAnimationWithTime(powerAnimID, animDuration);
            return true;
        } else {
            return false;
        }
    }

    public static boolean playPowerAnimWithCount(PlayerEntity playerEntity, Identifier powerAnimID, int animCount, AnimationSendSideType sendSideType) {
        if (!sendSideType.canPlayAnim(playerEntity)) {
            return false;
        }
        if (playerEntity instanceof IPlayerAnimController playerAnimController) {
            playerAnimController.shape_shifter_curse$playAnimationWithCount(powerAnimID, animCount);
            return true;
        } else {
            return false;
        }
    }

    public static boolean playPowerAnimLoop(PlayerEntity playerEntity, Identifier powerAnimID, AnimationSendSideType sendSideType) {
        if (!sendSideType.canPlayAnim(playerEntity)) {
            return false;
        }
        if (playerEntity instanceof IPlayerAnimController playerAnimController) {
            playerAnimController.shape_shifter_curse$playAnimationLoop(powerAnimID);
            return true;
        } else {
            return false;
        }
    }

    public static boolean stopPowerAnim(PlayerEntity playerEntity, AnimationSendSideType sendSideType) {
        if (!sendSideType.canPlayAnim(playerEntity)) {
            return false;
        }
        if (playerEntity instanceof IPlayerAnimController playerAnimController) {
            playerAnimController.shape_shifter_curse$stopAnimation();
            return true;
        } else {
            return false;
        }
    }

    public static boolean stopPowerAnimWithIDs(PlayerEntity playerEntity, AnimationSendSideType sendSideType, List<Identifier> powerAnimIDs) {
        return stopPowerAnimWithIDs(playerEntity, sendSideType, powerAnimIDs.toArray(new Identifier[0]));
    }

    public static boolean stopPowerAnimWithIDs(PlayerEntity playerEntity, AnimationSendSideType sendSideType, Identifier... powerAnimIDs) {
        if (!sendSideType.canPlayAnim(playerEntity)) {
            return false;
        }
        if (playerEntity instanceof IPlayerAnimController playerAnimController) {
            @Nullable Identifier nowAnimID = playerAnimController.shape_shifter_curse$getPowerAnimationID();
            for (Identifier powerAnimID : powerAnimIDs) {
                if (powerAnimID.equals(nowAnimID)) {
                    stopPowerAnim(playerEntity, sendSideType);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
