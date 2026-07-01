package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.PowerTypeRegistryAccessor;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.NeedCheckUsableForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormRenderUtils;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DynamicForm implements IForm, NeedCheckUsableForm {
    public static final UUID PublicUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public @NotNull Identifier formID;
    public Set<String> formFlag;

    private IFormGroup formGroup = null;
    private int formTier = -2;
    private PlayerFormBodyType bodyType = PlayerFormBodyType.NORMAL;

    private @Nullable Pair<Identifier, Identifier> layerOverwrite = null;
    public @Nullable Pair<Identifier, Identifier> layerRenderOverwrite = null;
    private boolean powerAnimRegistered = false;

    private JsonObject formData;

    private Map<Identifier, AbstractAnimStateController> animStateControllerMap = new HashMap<>();
    private AbstractAnimStateController defaultAnimStateController = AnimUtils.EMPTY_CONTROLLER;
    private Map<Identifier, AnimUtils.AnimationHolderData> powerAnimBuilderMap = new HashMap<>();
    private Map<Identifier, AnimationHolder> powerAnimMap = new HashMap<>();

    public boolean IsPatronForm = false;  // 可以使用特殊物品直接变形
    public int RequirePatronLevel = 0;  // 需要的赞助等级
    public List<UUID> PlayerUUIDs = new ArrayList<UUID>();

    public List<Identifier> ExtraPower = new LinkedList<Identifier>();
    public HashMap<Identifier, JsonObject> ExtraPowerData = new LinkedHashMap<>();
    public List<Identifier> RemovedPower = new LinkedList<Identifier>();
    private int TempPowerIndex = 0;

    public Identifier fallbackFormID = null;

    public DynamicForm(@Nullable Identifier formID, JsonObject formData) {
        this.formID = formID;
        this.formData = formData;
        this.loadFromJson();
    }

    @Override
    public @NotNull Identifier getFormID() {
        return formID;
    }

    @Override
    public @NotNull Set<String> getFormFlag() {
        return this.formFlag;
    }

    @Override
    public int getFormTier() {
        return this.formTier;
    }

    @Override
    public @Nullable IFormGroup getFormGroup() {
        return this.formGroup;
    }

    @Override
    public void setFormGroup(IFormGroup group, int formTier) {
        this.formGroup = group;
        this.formTier = formTier;
    }

    @Override
    public @NotNull Pair<Identifier, Identifier> getFormLayer() {
        if (this.layerOverwrite != null) {
            return layerOverwrite;
        }
        return new Pair<>(Identifier.of("origins", "origin"), Identifier.of(this.formID.getNamespace(), "form_" + this.formID.getPath()));
    }

    @Override
    public @NotNull PlayerFormBodyType getBodyType() {
        return this.bodyType;
    }

    @Override
    public @Nullable IForm getNextForm(PlayerEntity player, ITransformReason reason) {
        return IForm.super.getNextForm(player, reason);
    }

    @Override
    public @Nullable IForm getPrevForm(PlayerEntity player, ITransformReason reason) {
        return IForm.super.getPrevForm(player, reason);
    }

    @Override
    public @NotNull IForm getDefaultNextForm(PlayerEntity player, ITransformReason reason) {
        return IForm.super.getDefaultNextForm(player, reason);
    }

    @Override
    public @NotNull IForm getDefaultPrevForm(PlayerEntity player, ITransformReason reason) {
        return IForm.super.getDefaultPrevForm(player, reason);
    }

    public Pair<Identifier, Identifier> getCurrentRenderLayer() {
        return Objects.requireNonNullElseGet(this.layerRenderOverwrite, this::getFormLayer);
    }

    public boolean isModelExist() {
        Pair<Identifier, Identifier> currentLayer = this.getCurrentRenderLayer();
        return FormRenderUtils.formRendererRegistry.getOrDefault(currentLayer.getLeft(), new HashMap<>()).containsKey(currentLayer.getRight());
    }

    @Override
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
        this.powerAnimRegistered = true;
    }

    @Override
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        if (!this.isModelExist()) {
            return new Pair<>(false, null); // 如果未加载模型则不修改动画
        }
        boolean isAnimRegistered = powerAnimMap.containsKey(powerAnimID);
        AnimationHolder powerAnimData = powerAnimMap.get(powerAnimID);
        if (isAnimRegistered) {
            return new Pair<>(true, powerAnimData);
        }
        return new Pair<>(false, null);
    }

    @Override
    public boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return powerAnimRegistered;
    }

    @Override
    public void applyScale(PlayerEntity player) {
        // NormalForm.RESET_SCALE_FUNC.accept(player);
    }

    public void loadFromJson() {
        if (this.formData.has("FormID")) { this.formID = Identifier.tryParse(this.formData.get("FormID").getAsString()); }
        if (this.formID == null) {
            throw new RuntimeException("FormID Is Null");
        }
        Identifier groupID = Identifier.tryParse(_Gson_GetString(formData, "group", this.formID.toString()));
        int weight = _Gson_GetInt(formData, "weight", 1);
        int tier = _Gson_GetInt(formData, "tier", 1);
        IFormGroup group = RegPlayerForms.getPlayerFormGroup(groupID);
        if (group != null) {
            group.registerForm(tier, weight, this);
        }
        this.bodyType = PlayerFormBodyType.valueOf(_Gson_GetString(formData, "bodyType", "NORMAL"));
        Identifier layerID = Identifier.tryParse(_Gson_GetString(formData, "originLayerID", null));
        Identifier powerFormID = Identifier.tryParse(_Gson_GetString(formData, "originID", null));
        if (layerID != null && powerFormID != null) {
            this.layerOverwrite = new Pair<>(layerID, powerFormID);
        }
        HashSet<String> flags = new HashSet<>();
        if (this.formData.has("flag")) {
            for (JsonElement flagJson : this.formData.get("flag").getAsJsonArray()) {
                flags.add(flagJson.getAsString());
            }
        }
        this.formFlag = Set.copyOf(flags);
        if (formData.has("anim")) {
            if (formData.get("anim").isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : formData.get("anim").getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        Identifier animStateID = Identifier.tryParse(entry.getKey());
                        if (animStateID != null) {
                            this.RegisterAnim(animStateID, entry.getValue().getAsJsonObject());
                        } else {
                            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animStateID: {}", this.formID.toString(), entry.getKey());
                        }
                    } else {
                        ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animState data: {}", this.formID.toString(), entry.getValue().toString());
                    }
                }
            } else {
                ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Need Update DataPack", this.formID.toString());
            }
        }
        if (formData.has("powerAnim") && formData.get("powerAnim").isJsonObject())  {
            for (Map.Entry<String, JsonElement> entry : formData.get("powerAnim").getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    Identifier powerAnimID = Identifier.tryParse(entry.getKey());
                    if (powerAnimID != null) {
                        this.RegisterPowerAnim(powerAnimID, entry.getValue().getAsJsonObject());
                    } else {
                        ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnimID: {}", this.formID.toString(), entry.getKey());
                    }
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnim data: {}", this.formID.toString(), entry.getValue().toString());
                }
            }
        }
        if (formData.has("animDefault") && formData.get("animDefault").isJsonObject()) {
            this.defaultAnimStateController = AnimUtils.readController(formData.get("animDefault").getAsJsonObject());
        }
        String IDStr = _Gson_GetString(formData, "render_layer", null);
        this.layerRenderOverwrite = IDStr == null ? null : new Pair<>(Identifier.of("origins", "origin"), Identifier.tryParse(IDStr));
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
        if (formData.has("fallback")) {
            this.fallbackFormID = Identifier.tryParse(formData.get("fallback").getAsString());
        }
    }

    public static DynamicForm fromJson(@Nullable Identifier identifier, JsonObject data) {
        return new DynamicForm(identifier, data);
    }

    public JsonObject toJson() {
        return formData;
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

    private void RegisterAnim(@NotNull Identifier animStateID, @NotNull JsonObject controllerJsonData) {
        AbstractAnimStateController controller = AnimUtils.readController(controllerJsonData);
        animStateControllerMap.put(animStateID, controller);
    }

    private void RegisterPowerAnim(@NotNull Identifier powerAnimID, @NotNull JsonObject powerAnimJsonData) {
        AnimUtils.AnimationHolderData powerAnimData = AnimUtils.readAnim(powerAnimJsonData);
        powerAnimBuilderMap.put(powerAnimID, powerAnimData);
    }

    public List<Identifier> getExtraPower() {
        List<Identifier> powerList = new LinkedList<>(this.ExtraPower);
        // this.ExtraPowerData
        for (Map.Entry<Identifier, JsonObject> powerData : this.ExtraPowerData.entrySet()) {
            powerList.add(powerData.getKey());
        }
        return powerList;
    }

    public List<Identifier> getRemovedPower() {
        return this.RemovedPower;
    }

    private Identifier registerPower(JsonObject powerData) {
        Identifier powerID = new Identifier(this.formID.getNamespace(), this.formID.getPath() + "_tpower_" + this.TempPowerIndex);
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
                ShapeShifterCurseFabric.LOGGER.warn("Power Factory is null! From {}", this.formID.toString());
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
        this.ExtraPower.clear();
        this.ExtraPowerData.clear();
        this.RemovedPower.clear();
        if (formData.has("ExtraPower")) {
            JsonArray powerArray = formData.getAsJsonArray("ExtraPower");
            for (JsonElement powerElement : powerArray) {
                if (powerElement.isJsonPrimitive()) {
                    this.ExtraPower.add(Identifier.tryParse(powerElement.getAsString()));
                } else if (powerElement.isJsonObject()) {
                    this.ExtraPowerData.put(registerPower(powerElement.getAsJsonObject()), powerElement.getAsJsonObject());
                } else {
                    ShapeShifterCurseFabric.LOGGER.warn("Invalid ExtraPower data: {}", powerElement.toString());
                }
            }
        }
        if (formData.has("RemovedPower")) {
            JsonArray powerArray = formData.getAsJsonArray("RemovedPower");
            for (JsonElement powerElement : powerArray) {
                this.RemovedPower.add(Identifier.tryParse(powerElement.getAsString()));
            }
        }
    }

    @Override
    public boolean IsPlayerCanUse(PlayerEntity player) {
        if (this.PlayerUUIDs.contains(player.getUuid())) {
            return true;
        }
        return (this.PlayerUUIDs.isEmpty() || this.PlayerUUIDs.contains(PublicUUID)) && (PatronUtils.PatronLevels.getOrDefault(player.getUuid(), 0) >= this.RequirePatronLevel);
    }

    @Override
    public boolean isDynamicForm() {
        return true;
    }

    @Override
    public void afterApplyLayer(PlayerEntity player) {
        Identifier layer = this.getFormLayer().getRight();
        for (Identifier powerID: this.getExtraPower()) {
            FormUtils.applyPower(player, powerID, layer);
        }
        for (Identifier powerID: this.getRemovedPower()) {
            FormUtils.removePower(player, powerID, layer);
        }
    }

    @Override
    public void onTransform_Finish(PlayerEntity player) {
        if (this.fallbackFormID != null) {
            PlayerFormComponent pfc = PlayerFormComponent.COMPONENT.get(player);
            pfc.setFallbackForm(this.fallbackFormID);
        }
    }
}
