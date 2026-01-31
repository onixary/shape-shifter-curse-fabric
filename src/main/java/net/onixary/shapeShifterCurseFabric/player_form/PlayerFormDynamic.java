package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.PowerTypeRegistryAccessor;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginalFurClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import java.util.*;

public class PlayerFormDynamic extends PlayerFormBase{

    /*
    private final HashMap<PlayerAnimState, AnimationHolderData> animMap_Builder = new HashMap<>();
    public static final HashMap<Identifier, HashMap<PlayerAnimState, AnimationHolder>> animMap = new HashMap<>();
    private AnimationHolderData defaultAnim_Builder = null;
    public static final HashMap<Identifier, AnimationHolder> defaultAnim = new HashMap<>();
    public static final HashMap<Identifier, Boolean> isAnimRegistered = new HashMap<>();
     */

    public static final UUID PublicUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public Identifier FurModelID = null;
    public List<Identifier> ExtraPower = new LinkedList<Identifier>();
    public HashMap<Identifier, JsonObject> ExtraPowerData = new LinkedHashMap<>();
    private int TempPowerIndex = 0;
    public boolean IsPatronForm = false;  // 可以使用特殊物品直接变形
    public int RequirePatronLevel = 0;  // 需要的赞助等级
    public List<UUID> PlayerUUIDs = new ArrayList<UUID>();


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

    private static String _Gson_GetString(JsonObject data, String key, String defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        }
        return defaultValue;
    }

    private static int _Gson_GetInt(JsonObject data, String key, int defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        }
        return defaultValue;
    }

    private static boolean _Gson_GetBoolean(JsonObject data, String key, boolean defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    public void load(JsonObject formData) {
        try {
            if (formData.has("FormID")) {
                this.FormID = Identifier.tryParse(formData.get("FormID").getAsString());
            }
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
            String IDStr = _Gson_GetString(formData, "FurModelID", null);
            this.FurModelID = IDStr == null ? null : Identifier.tryParse(IDStr);
            this.loadExtraPower(formData);
            this.IsPatronForm = _Gson_GetBoolean(formData, "IsPatronForm", false);
            this.PlayerUUIDs.clear();
            if (formData.has("PlayerUUID")) {
                for (JsonElement uuidJson : formData.get("PlayerUUID").getAsJsonArray()) {
                    UUID uuid = UUID.fromString(uuidJson.getAsString());
                    if (uuid != null) {
                        this.PlayerUUIDs.add(uuid);
                    }
                }
            }
            this.RequirePatronLevel = _Gson_GetInt(formData, "RequirePatronLevel", 0);
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
        if (this.FurModelID != null) {
            data.addProperty("FurModelID", this.FurModelID.toString());
        }
        this.saveExtraPower(data);
        data.addProperty("IsPatronForm", this.IsPatronForm);
        if (!PlayerUUIDs.isEmpty()) {
            JsonArray uuids = new JsonArray();
            for (UUID uuid : PlayerUUIDs) {
                uuids.add(uuid.toString());
            }
            data.add("PlayerUUID", uuids);
        }
        data.addProperty("RequirePatronLevel", this.RequirePatronLevel);
        return data;
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

    public static PlayerFormDynamic of(JsonObject formData) throws IllegalArgumentException {
        PlayerFormDynamic form = new PlayerFormDynamic(null);
        form.load(formData);
        if (form.FormID == null) {
            throw new IllegalArgumentException("FormID is required");
        }
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

    // 添加在玩家自选Form的UI判断
    public boolean IsPlayerCanUse(PlayerEntity player) {
        // PlayerUUIDs 为白名单 为空则无限制
        if (this.PlayerUUIDs.contains(player.getUuid())) {
            return true;
        }
        return (this.PlayerUUIDs.isEmpty() || this.PlayerUUIDs.contains(PublicUUID)) && (PatronUtils.PatronLevels.getOrDefault(player.getUuid(), 0) >= this.RequirePatronLevel);
    }

    public List<Identifier> getExtraPower() {
        List<Identifier> powerList = new LinkedList<>(this.ExtraPower);
        // this.ExtraPowerData
        for (Map.Entry<Identifier, JsonObject> powerData : this.ExtraPowerData.entrySet()) {
            powerList.add(powerData.getKey());
        }
        return powerList;
    }

    private Identifier registerPower(JsonObject powerData) {
        Identifier powerID = new Identifier(this.FormID.getNamespace(), this.FormID.getPath() + "_tpower_" + this.TempPowerIndex);
        if (powerData == null) {
            return null;
        }
        try {
            Identifier PowerID = Identifier.tryParse(powerData.get("type").getAsString());
            PowerFactory<Power> pf = null;
            if (NamespaceAlias.hasAlias(PowerID)) {
                pf = ApoliRegistries.POWER_FACTORY.get(NamespaceAlias.resolveAlias(PowerID));
            }
            else {
                pf = ApoliRegistries.POWER_FACTORY.get(PowerID);
            }
            if (pf == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Power Factory is null! From {}", this.FormID.toString());
                return null;
            }
            PowerFactory<Power>.Instance pi = pf.read(powerData);
            PowerType<?> powerType = new PowerType<>(powerID, pi);
            PowerTypeRegistryAccessor.Invoke_Update(powerID, powerType);
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to register power: {}", powerData.toString());
            return null;
        }
        this.TempPowerIndex++;
        return powerID;
    }

    private void loadExtraPower(JsonObject formData) {
        /* "ExtraPower" : [
         *      "power:power_id",
         *      {
         *          some power data
         *      }
         * ]
         */
        this.ExtraPower.clear();
        this.ExtraPowerData.clear();
        if (!formData.has("ExtraPower")) {
            return;
        }
        JsonArray powerArray = formData.getAsJsonArray("ExtraPower");
        for (JsonElement powerElement : powerArray) {
            if (powerElement.isJsonPrimitive()) {
                this.ExtraPower.add(Identifier.tryParse(powerElement.getAsString()));
            }
            else if (powerElement.isJsonObject()) {
                this.ExtraPowerData.put(registerPower(powerElement.getAsJsonObject()), powerElement.getAsJsonObject());
            }
            else {
                ShapeShifterCurseFabric.LOGGER.warn("Invalid ExtraPower data: {}", powerElement.toString());
            }
        }
    }

    /*
    private void saveExtraPower(JsonObject data) {
        JsonArray powerArray = new JsonArray();
        if (!this.ExtraPower.isEmpty()) {
            for (Identifier powerID : this.ExtraPower) {
                powerArray.add(powerID.toString());
            }
        }
        if (!this.ExtraPowerData.isEmpty()) {
            for (Map.Entry<Identifier, JsonObject> powerData : this.ExtraPowerData.entrySet()) {
                powerArray.add(powerData.getValue());
            }
        }
        data.add("ExtraPower", powerArray);
    }
     */
}
