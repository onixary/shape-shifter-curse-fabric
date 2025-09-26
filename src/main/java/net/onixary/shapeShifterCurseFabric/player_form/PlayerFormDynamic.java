package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import java.util.HashMap;

public class PlayerFormDynamic extends PlayerFormBase{
    static class AnimationHolderData {
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

    private final HashMap<PlayerAnimState, AnimationHolderData> animMap_Building = new HashMap<>();
    public HashMap<PlayerAnimState, AnimationHolder> animMap = new HashMap<>();
    private final AnimationHolderData defaultAnim_Building = null;
    public AnimationHolder defaultAnim = null;

    public PlayerFormDynamic(Identifier id) {
        super(id);
    }

    @Override
    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        return animMap.getOrDefault(currentState, defaultAnim);
    }

    @Override
    public void Anim_registerAnims() {
        for (PlayerAnimState state : animMap_Building.keySet()) {
            animMap.put(state, animMap_Building.get(state).build());
        }
    }


    private AnimationHolderData loadAnim(JsonObject animData) {
        try {
            Identifier AnimID = Identifier.tryParse(animData.get("animID").getAsString());
            float Speed = 1.0f;
            int Fade = 2;
            if (animData.has("speed")) {
                Speed = animData.get("speed").getAsFloat();
            }
            if (animData.has("fade")) {
                Fade = animData.get("fade").getAsInt();
            }
            return new AnimationHolderData(AnimID, Speed, Fade);
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Error while loading player animation: " + e.getMessage());
            return null;
        }
    }

    public void registerAnim(JsonObject animData) {
        try {
            PlayerAnimState State = PlayerAnimState.valueOf(animData.get("state").getAsString());
            animMap_Building.put(State, loadAnim(animData));
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Error while register player animation: " + e.getMessage());
        }
    }

    public void load(JsonObject formData) {
        // TODO 完成读取JsonObject的逻辑
    }

    public static PlayerFormDynamic of(Identifier id, JsonObject formData) {
        PlayerFormDynamic form = new PlayerFormDynamic(id);
        form.load(formData);
        return form;
    }
}
