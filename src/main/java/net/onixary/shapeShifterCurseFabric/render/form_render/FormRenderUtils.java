package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.PlayerOriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FormRenderUtils {
    public static final HashMap<Identifier, Supplier<IModelAnimationSystem>> modelAnimationSystemRegistry = new HashMap<>();

    // { "layer(slot)": {"form": formRenderer} }
    public static final HashMap<Identifier, HashMap<Identifier, FormRenderer>> formRendererRegistry = new HashMap<>();

    public static final Identifier DEFAULT_MAS = register_MAS(ShapeShifterCurseFabric.identifier("default"), DefaultModelAnimationSystem::new);

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

    public static @Nullable FormRenderer getFormRenderer(Identifier slotID, Identifier formID) {
        return formRendererRegistry.getOrDefault(slotID, new HashMap<>()).get(formID);
    }

    public static void loadFormRenderer(Identifier slotID, Identifier formID, FormRenderer renderer) {
        formRendererRegistry.computeIfAbsent(slotID, k -> new HashMap<>()).put(formID, renderer);
    }

    // Origins 版本核心 如果需要重构形态系统需要重新写一份这个函数
    public static List<FormRenderer> getPlayerAllFormRenderer(PlayerEntity player) {
        try {
            PlayerFormBase playerFormBase = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
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
