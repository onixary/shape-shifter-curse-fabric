package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimUtils {
    public static class AnimationHolderData {
        public Identifier AnimID;
        public float Speed;
        public int Fade;
        public AnimationHolderData(Identifier AnimID, float Speed, int Fade) {
            this.AnimID = AnimID;
            this.Speed = Speed;
            this.Fade = Fade;
        }
        public AnimationHolder build() {
            return new AnimationHolder(AnimID, true, Speed, Fade);
        }
    }

    private static class EmptyAnimationHolderData extends AnimationHolderData {
        public EmptyAnimationHolderData() {
            super(null, 0.0f, 0);
        }
        @Override
        public AnimationHolder build() {
            return null;
        }
    }

    public static AnimationHolderData EMPTY = new EmptyAnimationHolderData();

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
            return EMPTY;
        }
    }
}
