package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.PlayerOriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class FormRenderUtils {
    public static final HashMap<Identifier, Supplier<IModelAnimationSystem>> modelAnimationSystemRegistry = new HashMap<>();

    public static boolean isRenderingInWorld = false;

    // { "layer(slot)": {"form": formRenderer} }
    public static final HashMap<Identifier, HashMap<Identifier, FormRenderer>> formRendererRegistry = new HashMap<>();

    public static final Identifier DEFAULT_MAS = register_MAS(ShapeShifterCurseFabric.identifier("default"), DefaultModelAnimationSystem::new);

    public static void onClientInit() {
        WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
        WorldRenderEvents.START.register(context -> isRenderingInWorld = true);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FormModelResourceReloadListener());
    }

    public static Identifier register_MAS(Identifier id, Supplier<IModelAnimationSystem> supplier) {
        modelAnimationSystemRegistry.put(id, supplier);
        return id;
    }

    public static @Nullable IModelAnimationSystem get_MAS(Identifier id, @Nullable JsonObject json) {
        @Nullable Supplier<IModelAnimationSystem> supplier = modelAnimationSystemRegistry.get(id);
        if (supplier != null) {
            IModelAnimationSystem system = supplier.get();
            system.loadConfig(json);
            return system;
        }
        return null;
    }

    public static void registerFormRenderer(Identifier slotID, Identifier formID, FormRenderer renderer) {
        formRendererRegistry.computeIfAbsent(slotID, k -> new HashMap<>()).put(formID, renderer);
    }

    public static @Nullable FormRenderer getFormRenderer(Identifier slotID, Identifier formID) {
        return formRendererRegistry.getOrDefault(slotID, new HashMap<>()).get(formID);
    }

    public static void loadFormRenderer(Identifier slotID, Identifier formID, FormRenderer renderer) {
        formRendererRegistry.computeIfAbsent(slotID, k -> new HashMap<>()).put(formID, renderer);
    }

    public static Vec3d getPartPosition(ModelPart part) {
        var t = part.getTransform();
        return new Vec3d(t.pivotX, t.pivotY, t.pivotZ).negate();
    }

    public static Vec3d getPartRotation(ModelPart part) {
        var t = part.getTransform();
        return new Vec3d(t.pitch, t.yaw, t.roll);
    }

    public static Vec3d getPartScale(ModelPart part) {
        return new Vec3d(part.xScale, part.yScale, part.zScale);
    }

    // Origins 版本核心 如果需要重构形态系统需要重新写一份这个函数
    public static List<FormRenderer> getPlayerAllFormRenderer(PlayerEntity player) {
        if (FormTextureUtils.useTempFormModel && Objects.equals(player, MinecraftClient.getInstance().player)) {
            List<FormRenderer> formRenderers = new ArrayList<>();
            Identifier formID = FormTextureUtils.tempFormModelProcessor.getLayerID();
            FormRenderer formRenderer = FormRenderUtils.getFormRenderer(Identifier.of("origins", "origin"), formID);
            if (formRenderer == null) {
                ShapeShifterCurseFabric.LOGGER.warn("ShapeShifterCurseFabric: PlayerFormDynamic.ModelID is not null, but the model is not registered: {}", formID);
                return new ArrayList<>();
            }
            formRenderers.add(formRenderer);
            return formRenderers;
        }
        try {
            IForm playerFormBase = FormUtils.getPlayerForm(player);
            if (playerFormBase instanceof PlayerFormDynamic pfd) {
                List<FormRenderer> formRenderers = new ArrayList<>();
                if (pfd.FurModelID != null) {
                    FormRenderer formRenderer = FormRenderUtils.getFormRenderer(Identifier.of("origins", "origin"), pfd.FurModelID);
                    if (formRenderer == null) {
                        ShapeShifterCurseFabric.LOGGER.warn("ShapeShifterCurseFabric: PlayerFormDynamic.ModelID is not null, but the model is not registered: {}", pfd.FurModelID);
                        return new ArrayList<>();
                    }
                    formRenderers.add(formRenderer);
                    return formRenderers;
                }
            }
        } catch (Exception ignored) {}
        PlayerOriginComponent poc = (PlayerOriginComponent) ModComponents.ORIGIN.get(player);
        HashMap<OriginLayer, Origin> OriginData = poc.getOrigins();
        List<FormRenderer> formRenderers = new ArrayList<>();
        for (Map.Entry<OriginLayer, Origin> entry : OriginData.entrySet()) {
            Identifier layer = entry.getKey().getIdentifier();
            Identifier form = entry.getValue().getIdentifier();
            FormRenderer formRenderer = FormRenderUtils.getFormRenderer(layer, form);
            if (formRenderer != null) {
                formRenderers.add(formRenderer);
            }
        }
        return formRenderers;
    }
}
