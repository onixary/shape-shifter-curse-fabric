package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginalFurClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerFormDynamic extends PlayerFormBase{

    /*
    private final HashMap<PlayerAnimState, AnimationHolderData> animMap_Builder = new HashMap<>();
    public static final HashMap<Identifier, HashMap<PlayerAnimState, AnimationHolder>> animMap = new HashMap<>();
    private AnimationHolderData defaultAnim_Builder = null;
    public static final HashMap<Identifier, AnimationHolder> defaultAnim = new HashMap<>();
    public static final HashMap<Identifier, Boolean> isAnimRegistered = new HashMap<>();
     */

    // 覆写数据
    private Identifier originID = null;
    private Identifier originLayerID = null;

    private JsonObject formData = null;  // 我觉得可以不用save出JsonObject 在load的时候直接保存原始JsonObject 省的给一堆的Field写序列化

    private PlayerFormDynamic(Identifier id) {
        super(id);
    }

    public boolean isModelExist() {
        return OriginalFurClient.FUR_RESOURCES.containsKey(this.getFormOriginID());
    }

    @Override
    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        /*
        // 如果未加载模型则不修改动画
        if (!this.isModelExist()) {
            return null;
        }
        if (!isAnimRegistered.getOrDefault(this.FormID, false)) {
            Anim_registerAnims();
        }
        return this.getAnimMap().getOrDefault(currentState, defaultAnim.get(this.FormID));
         */
        return null;
    }

    /*
    public HashMap<PlayerAnimState, AnimationHolder> getAnimMap() {
        return animMap.computeIfAbsent(this.FormID, k -> new HashMap<>());
    }
     */

    @Override
    public void Anim_registerAnims() {
        /*
        this.getAnimMap().clear();
        for (PlayerAnimState state : this.animMap_Builder.keySet()) {
            this.getAnimMap().put(state, this.animMap_Builder.get(state).build());
        }
        if (this.defaultAnim_Builder != null) {
            defaultAnim.put(this.FormID, defaultAnim_Builder.build());
        }
        isAnimRegistered.put(this.FormID, true);
         */
    }

    private Map<Identifier, AbstractAnimStateController> animStateControllerMap = new HashMap<>();
    private AbstractAnimStateController defaultAnimStateController = AnimUtils.EMPTY_CONTROLLER;
    private Map<Identifier, AnimUtils.AnimationHolderData> powerAnimBuilderMap = new HashMap<>();
    private Map<Identifier, AnimationHolder> powerAnimMap = new HashMap<>();

    private void RegisterAnim(@NotNull Identifier animStateID, @NotNull JsonObject controllerJsonData) {
        AbstractAnimStateController controller = AnimUtils.readController(controllerJsonData);
        animStateControllerMap.put(animStateID, controller);
    }

    private void RegisterPowerAnim(@NotNull Identifier powerAnimID, @NotNull JsonObject powerAnimJsonData) {
        AnimUtils.AnimationHolderData powerAnimData = AnimUtils.readAnim(powerAnimJsonData);
        powerAnimBuilderMap.put(powerAnimID, powerAnimData);
    }

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        if (!this.isModelExist()) {
            return AnimUtils.EMPTY_CONTROLLER; // 如果未加载模型则不修改动画
        }
        return animStateControllerMap.getOrDefault(animStateID, defaultAnimStateController);
    }

    @Override
    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        for (Identifier powerAnimID : powerAnimBuilderMap.keySet()) {
            AnimUtils.AnimationHolderData powerAnimData = powerAnimBuilderMap.get(powerAnimID);
            powerAnimMap.put(powerAnimID, powerAnimData.build());
        }
        super.registerPowerAnim(player, animSystemData);
    }

    @Override
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        if (!this.isModelExist()) {
            return new Pair<>(false, null); // 如果未加载模型则不修改动画
        }
        Boolean isAnimRegistered = powerAnimMap.containsKey(powerAnimID);
        AnimationHolder powerAnimData = powerAnimMap.get(powerAnimID);
        if (isAnimRegistered) {
            return new Pair<>(true, powerAnimData);
        }
        return super.getPowerAnim(player, animSystemData, powerAnimID);
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
                if (formData.get("anim").isJsonObject()) { // 给老版数据包提出需要更新的Log
                    for (Map.Entry<String, JsonElement> entry : formData.get("anim").getAsJsonObject().entrySet()) {
                        if (entry.getValue().isJsonObject()) {
                            Identifier animStateID = Identifier.tryParse(entry.getKey());
                            if (animStateID != null) {
                                this.RegisterAnim(animStateID, entry.getValue().getAsJsonObject());
                            } else {
                                ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animStateID: {}", this.FormID.toString(), entry.getKey());
                            }
                        } else {
                            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animState data: {}", this.FormID.toString(), entry.getValue().toString());
                        }
                    }
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Need Update DataPack", this.FormID.toString());
                }
            }
            if (formData.has("powerAnim") && formData.get("powerAnim").isJsonObject())  {
                for (Map.Entry<String, JsonElement> entry : formData.get("powerAnim").getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        Identifier powerAnimID = Identifier.tryParse(entry.getKey());
                        if (powerAnimID != null) {
                            this.RegisterPowerAnim(powerAnimID, entry.getValue().getAsJsonObject());
                        } else {
                            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnimID: {}", this.FormID.toString(), entry.getKey());
                        }
                    } else {
                        ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnim data: {}", this.FormID.toString(), entry.getValue().toString());
                    }
                }
            }
            if (formData.has("animDefault") && formData.get("animDefault").isJsonObject()) {
                this.defaultAnimStateController = AnimUtils.readController(formData.get("animDefault").getAsJsonObject());
            }
            Identifier GroupID = Identifier.tryParse(_Gson_GetString(formData, "groupID", this.FormID.toString()));
            int GroupIndex = _Gson_GetInt(formData, "groupIndex", 0);
            PlayerFormGroup group = RegPlayerForms.getPlayerFormGroup(GroupID);
            if (group == null) {
                group = RegPlayerForms.registerDynamicPlayerFormGroup(new PlayerFormGroup(GroupID));
            }
            this.setGroup(group, GroupIndex);
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form: {}", e.getMessage());
        }
        this.formData = formData;
    }

    public JsonObject save() {
        /*
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
        for (Map.Entry<PlayerAnimState, AnimationHolderData> entry : animMap_Builder.entrySet()) {
            anims.add(saveAnim(entry.getKey(), entry.getValue()));
        }
        data.add("anim", anims);
        if (this.defaultAnim_Builder != null) {
            data.add("animDefault", saveAnim(null, this.defaultAnim_Builder));
        }
        if (this.getGroup() != null) {
            data.addProperty("groupID", this.getGroup().GroupID.toString());
            data.addProperty("groupIndex", this.FormIndex);
        }
         */
        if (this.formData == null) {
            throw new RuntimeException("PlayerFormDynamic.save() called before load()");
        }
        return this.formData;
    }

    public static PlayerFormDynamic of(Identifier id, JsonObject formData) {
        PlayerFormDynamic form = new PlayerFormDynamic(id);
        form.load(formData);
        return form;
    }

    @Override
    public Identifier getFormOriginID() {
        return this.originID != null ? this.originID : super.getFormOriginID();
    }

    @Override
    public Identifier getFormOriginLayerID() {
        return this.originLayerID != null ? this.originLayerID : super.getFormOriginLayerID();
    }
}
