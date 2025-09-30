package net.onixary.shapeShifterCurseFabric.player_form.ability;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.cancelEffect;

public class FormAbilityManager {
    private static ServerWorld world;

    public static PlayerFormBase getForm(PlayerEntity player) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        return component.getCurrentForm();
    }

    public static void getServerWorld(ServerWorld world) {
        FormAbilityManager.world = world;
    }

    public static void applyForm(PlayerEntity player, PlayerFormBase newForm) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormBase oldForm = component.getCurrentForm();

        clearFormEffects(player, oldForm);
        // 已被弃用，使用json定义的scale power
        //applyScale(player, config.getScale());
        applyFormOrigin(player, newForm);
        applyExtraPower(player, oldForm, newForm);
        //applyPower(player, config.getPowerId());
        // 清空Status
        cancelEffect(player);

        component.setCurrentForm(newForm);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        // 存储
        FormAbilityManager.saveForm(player);

        // 添加网络同步：通知客户端形态已变化
        if (!player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            try {
                ModPacketsS2CServer.sendFormChange(serverPlayer, newForm.name());
                ShapeShifterCurseFabric.LOGGER.info("Sent form change notification to client: " + newForm.name());
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to send form change notification: ", e);
            }
        }
    }

    public static void loadForm(PlayerEntity player) {
        PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormBase savedForm = loadSavedForm(player);
        if (savedForm != null) {
            applyForm(player, savedForm);
        }
        else{
            applyForm(player, RegPlayerForms.ORIGINAL_BEFORE_ENABLE);
        }
    }

    public static void saveForm(PlayerEntity player) {
        // 添加世界检查
        if (world == null && player instanceof ServerPlayerEntity serverPlayer) {
            world = serverPlayer.getServerWorld();
        }

        if (world != null) {
            PlayerFormComponent component = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
            PlayerNbtStorage.savePlayerFormComponent(world, player.getUuid().toString(), component);
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Cannot save form: world is null");
        }
    }

    private static PlayerFormBase loadSavedForm(PlayerEntity player) {
        // 从存储中加载保存的 form
        PlayerFormComponent formComponent = PlayerNbtStorage.loadPlayerFormComponent(world, player.getUuid().toString());
        return formComponent != null ? formComponent.getCurrentForm() : null;
    }

    private static void clearFormEffects(PlayerEntity player, PlayerFormBase oldForm) {
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

    private static void applyFormOrigin(PlayerEntity playerEntity, PlayerFormBase newForm) {
        // Origins引用的Identifier需要使用Origins.MODID
        OriginComponent component = ModComponents.ORIGIN.get(playerEntity);
        OriginLayer layer = OriginLayers.getLayer(newForm.getFormOriginLayerID());
        Identifier id = newForm.getFormOriginID();
        if (layer != null && id != null) {
            Origin origin = OriginRegistry.get(id);
            if(layer.contains(origin, playerEntity)){
                component.setOrigin(layer, origin);
                component.sync();
                ShapeShifterCurseFabric.LOGGER.info("Set form origin " + id.toString() + " for player " + playerEntity.getName());
            }
        }
    }

    private static void applyPower(PlayerEntity player, Identifier powerId, Identifier powerSource) {
        PowerType<?> powerType = PowerTypeRegistry.get(powerId);
        if (powerType != null) {
            PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
            powerHolder.addPower(powerType, powerSource);
        }
    }

    public static void applyExtraPower(PlayerEntity playerEntity, PlayerFormBase oldForm, PlayerFormBase newForm) {
        PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(playerEntity);
        // 移除旧形态的额外能力
        if (oldForm instanceof PlayerFormDynamic pfd) {
            powerComponent.removeAllPowersFromSource(pfd.FormID);
        }
        // 添加新形态的额外能力
        if (newForm instanceof PlayerFormDynamic pfd) {
            for (Identifier powerID: pfd.ExtraPower) {
                applyPower(playerEntity, powerID, pfd.FormID);
            }
        }
    }
}
