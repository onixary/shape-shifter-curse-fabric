package net.onixary.shapeShifterCurseFabric.player_form.ability;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Objects;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.DEBUG_UUID;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormAbilityManager {

    public static PlayerForms getForm(PlayerEntity player) {
        PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
        return component.getCurrentForm();
    }

    public static void applyForm(PlayerEntity player, PlayerForms newForm) {
        PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
        PlayerForms oldForm = component.getCurrentForm();

        // 清理旧状态能力
        clearFormEffects(player, oldForm);

        // 应用新状态能力
        FormConfig config = RegFormConfig.getConfig(newForm);
        //applyHealth(player, config.getMaxHealth());
        //applyArmor(player, config.getArmor());
        applyScale(player, config.getScale());
        applyFormOrigin(player, config.getFormOriginLayerID(), config.getFormOriginID());
        //applyPower(player, config.getPowerId());

        component.setCurrentForm(newForm);
        // 存储
        PlayerNbtStorage.savePlayerFormComponent(DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID, component);
    }

    public static void loadForm(PlayerEntity player) {
        PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
        PlayerForms savedForm = loadSavedForm(player);
        if (savedForm != null) {
            applyForm(player, savedForm);
        }
    }

    public static void saveForm(PlayerEntity player) {
        PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
        // 存储
        PlayerNbtStorage.savePlayerFormComponent(DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID, component);
    }

    private static PlayerForms loadSavedForm(PlayerEntity player) {
        // 从存储中加载保存的 form
        PlayerFormComponent formComponent = PlayerNbtStorage.loadPlayerFormComponent(DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID);
        return formComponent != null ? formComponent.getCurrentForm() : null;
    }

    private static void clearFormEffects(PlayerEntity player, PlayerForms oldForm) {
        FormConfig oldConfig = RegFormConfig.getConfig(oldForm);
        // 移除 Pehkui 缩放
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
        scaleData.setScale(1.0f);
    }

    private static void applyScale(PlayerEntity player, float scale) {
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
        scaleData.setScale(scale);
    }

    private static void applyFormOrigin(PlayerEntity playerEntity, String originLayerId, String originId) {
        // Origins引用的Identifier需要使用Origins.MODID
        OriginComponent component = ModComponents.ORIGIN.get(playerEntity);
        OriginLayer layer = OriginLayers.getLayer(Identifier.of(Origins.MODID, originLayerId));
        Identifier id = Identifier.of(Origins.MODID, originId);
        if (layer != null && id != null) {
            Origin origin = OriginRegistry.get(id);
            if(layer.contains(origin, playerEntity)){
                component.setOrigin(layer, origin);
                component.sync();
                ShapeShifterCurseFabric.LOGGER.info("Set form origin " + originId + " for player " + playerEntity.getName());
            }
        }
    }

    private static void applyPower(PlayerEntity player, Identifier powerId) {
        PowerType<?> powerType = PowerTypeRegistry.get(powerId);
        if (powerType != null) {
            PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
            powerHolder.addPower(powerType, powerId);
        }
    }
}
