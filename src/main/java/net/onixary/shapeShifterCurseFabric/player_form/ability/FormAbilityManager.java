package net.onixary.shapeShifterCurseFabric.player_form.ability;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.cancelEffect;

public class FormAbilityManager {
    private static ServerWorld world;

    public static PlayerForms getForm(PlayerEntity player) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        return component.getCurrentForm();
    }

    public static void getServerWorld(ServerWorld world) {
        FormAbilityManager.world = world;
    }

    public static void applyForm(PlayerEntity player, PlayerForms newForm) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
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
        // 清空Status
        cancelEffect(player);

        component.setCurrentForm(newForm);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        // 存储
        PlayerNbtStorage.savePlayerFormComponent(world, player.getUuid().toString(), component);
    }

    public static void loadForm(PlayerEntity player) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerForms savedForm = loadSavedForm(player);
        if (savedForm != null) {
            applyForm(player, savedForm);
        }
        else{
            applyForm(player, PlayerForms.ORIGINAL_BEFORE_ENABLE);
        }
    }

    public static void saveForm(PlayerEntity player) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        // 存储
        PlayerNbtStorage.savePlayerFormComponent(world, player.getUuid().toString(), component);
    }

    private static PlayerForms loadSavedForm(PlayerEntity player) {
        // 从存储中加载保存的 form
        PlayerFormComponent formComponent = PlayerNbtStorage.loadPlayerFormComponent(world, player.getUuid().toString());
        return formComponent != null ? formComponent.getCurrentForm() : null;
    }

    private static void clearFormEffects(PlayerEntity player, PlayerForms oldForm) {
        FormConfig oldConfig = RegFormConfig.getConfig(oldForm);
        // 移除 Pehkui 缩放
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
        scaleData.setScale(1.0f);
    }

    private static void applyScale(PlayerEntity player, float scale) {
        ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(player);
        ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(player);
        scaleDataWidth.setScale(scale);
        scaleDataWidth.setPersistence(true);
        scaleDataHeight.setScale(scale);
        scaleDataHeight.setPersistence(true);
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
