package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import java.util.HashMap;
import java.util.Map;

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
    private AnimationHolderData defaultAnim_Building = null;
    public AnimationHolder defaultAnim = null;

    // 覆写数据
    private Identifier originID = null;
    private Identifier originLayerID = null;

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

    private JsonObject saveAnim(PlayerAnimState state, AnimationHolderData animData) {
        JsonObject jsonAnimData = new JsonObject();
        if (state != null) {
            jsonAnimData.addProperty("state", state.name());
        }
        jsonAnimData.addProperty("animID", animData.AnimID.toString());
        jsonAnimData.addProperty("speed", animData.Speed);
        jsonAnimData.addProperty("fade", animData.Fade);
        return jsonAnimData;
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

    private String _Gson_GetString(JsonObject data, String key, String defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        }
        return defaultValue;
    }

    private int _Gson_GetInt(JsonObject data, String key, int defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        }
        return defaultValue;
    }

    private boolean _Gson_GetBoolean(JsonObject data, String key, boolean defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    public void load(JsonObject formData) {
        try {
            this.setPhase(PlayerFormPhase.valueOf(_Gson_GetString(formData, "phase", "PHASE_CLEAR")));
            this.setBodyType(PlayerFormBodyType.valueOf(_Gson_GetString(formData, "bodyType", "NORMAL")));
            this.setHasSlowFall(_Gson_GetBoolean(formData, "hasSlowFall", false));
            this.setOverrideHandAnim(_Gson_GetBoolean(formData, "overrideHandAnim", false));
            this.setCanSneakRush(_Gson_GetBoolean(formData, "canSneakRush", false));
            this.setCanRushJump(_Gson_GetBoolean(formData, "canRushJump", false));
            this.setIsCustomForm(_Gson_GetBoolean(formData, "isCustomForm", false));
            String originIDStr = _Gson_GetString(formData, "originID", null);
            if (originIDStr != null) {
                this.originID = Identifier.tryParse(originIDStr);
            }
            String originLayerIDStr = _Gson_GetString(formData, "originLayerID", null);
            if (originLayerIDStr != null) {
                this.originLayerID = Identifier.tryParse(originLayerIDStr);
            }
            if (formData.has("anim")) {
                for (JsonElement animData : formData.get("anim").getAsJsonArray()) {
                    if (animData.isJsonObject()) {
                        registerAnim(animData.getAsJsonObject());
                    }
                }
            }
            if (formData.has("animDefault")) {
                this.defaultAnim_Building = loadAnim(formData.get("animDefault").getAsJsonObject());
            }
            Identifier GroupID = Identifier.tryParse(_Gson_GetString(formData, "groupID", this.FormID.toString()));
            int GroupIndex = _Gson_GetInt(formData, "groupIndex", 0);
            PlayerFormGroup group = RegPlayerForms.getPlayerFormGroup(GroupID);
            if (group == null) {
                group = RegPlayerForms.registerPlayerFormGroup(new PlayerFormGroup(GroupID));
            }
            this.setGroup(group, GroupIndex);
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form: {}", e.getMessage());
        }
    }

    public JsonObject save() {
        JsonObject data = new JsonObject();
        data.addProperty("phase", this.getPhase().toString());
        data.addProperty("bodyType", this.getBodyType().toString());
        data.addProperty("hasSlowFall", this.getHasSlowFall());
        data.addProperty("overrideHandAnim", this.getOverrideHandAnim());
        data.addProperty("canSneakRush", this.getCanSneakRush());
        data.addProperty("canRushJump", this.getCanRushJump());
        data.addProperty("isCustomForm", this.getIsCustomForm());
        if (this.originID != null) {
            data.addProperty("originID", this.originID.toString());
        }
        if (this.originLayerID != null) {
            data.addProperty("originLayerID", this.originLayerID.toString());
        }
        JsonArray anims = new JsonArray();
        for (Map.Entry<PlayerAnimState, AnimationHolderData> entry : animMap_Building.entrySet()) {
            anims.add(saveAnim(entry.getKey(), entry.getValue()));
        }
        data.add("anim", anims);
        if (this.defaultAnim_Building != null) {
            data.add("animDefault", saveAnim(null, this.defaultAnim_Building));
        }
        if (this.Group != null) {
            data.addProperty("groupID", this.Group.GroupID.toString());
            data.addProperty("groupIndex", this.FormIndex);
        }
        return data;
    }

    public static PlayerFormDynamic of(Identifier id, JsonObject formData) {
        PlayerFormDynamic form = new PlayerFormDynamic(id);
        form.load(formData);
        return form;
    }
}
