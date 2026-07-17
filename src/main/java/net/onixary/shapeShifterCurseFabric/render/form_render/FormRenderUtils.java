package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;
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
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FormRenderUtils {
    public static final HashMap<Identifier, Supplier<IModelAnimationSystem>> modelAnimationSystemRegistry = new HashMap<>();
    public static final HashMap<String, Predicate<PlayerEntity>> conditionRegistry = new HashMap<>();
    static {
        conditionRegistry.put("always_true", player -> true);
        conditionRegistry.put("always_false", player -> false);
        conditionRegistry.put("is_sneaking", Entity::isSneaking);
        conditionRegistry.put("is_sprinting", Entity::isSprinting);
    }

    public static boolean isRenderingInWorld = false;

    // { "layer(slot)": {"form": formRenderer} }
    public static final HashMap<Identifier, HashMap<Identifier, FormRenderer>> formRendererRegistry = new HashMap<>();

    public static final Identifier DEFAULT_MAS = register_MAS(ShapeShifterCurseFabric.identifier("default"), DefaultModelAnimationSystem::new);

    public static class BoneBipedState {
        public final float x;
        public final float y;
        public final float z;
        public final float rot_x;
        public final float rot_y;
        public final float rot_z;
        public final float pivot_x;
        public final float pivot_y;
        public final float pivot_z;
        public final float scale_x;
        public final float scale_y;
        public final float scale_z;

        private @Nullable ModelPart cachedPart = null;
        @SuppressWarnings("removal")
        private @Nullable GeoBone cachedBone = null;

        public BoneBipedState(float x, float y, float z, float rot_x, float rot_y, float rot_z, float pivot_x, float pivot_y, float pivot_z, float scale_x, float scale_y, float scale_z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rot_x = rot_x;
            this.rot_y = rot_y;
            this.rot_z = rot_z;
            this.pivot_x = pivot_x;
            this.pivot_y = pivot_y;
            this.pivot_z = pivot_z;
            this.scale_x = scale_x;
            this.scale_y = scale_y;
            this.scale_z = scale_z;
        }

        public BoneBipedState(ModelPart part) {
            this(0f, 0f, 0f, part.pitch, part.yaw, part.roll, part.pivotX, part.pivotY, part.pivotZ, part.xScale, part.yScale, part.zScale);
            this.cachedPart = part;
        }

        @SuppressWarnings("removal")
        public BoneBipedState(GeoBone bone) {
            this(bone.getPosX(), bone.getPosY(), bone.getPosZ(), bone.getRotX(), bone.getRotY(), bone.getRotZ(), bone.getPivotX(), bone.getPivotY(), bone.getPivotZ(), bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
            this.cachedBone = bone;
        }

        public void apply(ModelPart part) {
            part.pitch = rot_x;
            part.yaw = rot_y;
            part.roll = rot_z;
            part.pivotX = pivot_x;
            part.pivotY = pivot_y;
            part.pivotZ = pivot_z;
            part.xScale = scale_x;
            part.yScale = scale_y;
            part.zScale = scale_z;
        }

        @SuppressWarnings("removal")
        public void apply(GeoBone bone) {
            bone.setPosX(x);
            bone.setPosY(y);
            bone.setPosZ(z);
            bone.setRotX(rot_x);
            bone.setRotY(rot_y);
            bone.setRotZ(rot_z);
            bone.setPivotX(pivot_x);
            bone.setPivotY(pivot_y);
            bone.setPivotZ(pivot_z);
            bone.setScaleX(scale_x);
            bone.setScaleY(scale_y);
            bone.setScaleZ(scale_z);
        }

        public void restore() {
            if (cachedPart != null) {
                apply(cachedPart);
            }
            if (cachedBone != null) {
                apply(cachedBone);
            }
        }
    }

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

    @SuppressWarnings("removal")
    public static MatrixStack computeModelMatrixStack(GeoBone bone) {
        MatrixStack matrices = new MatrixStack();
        if (bone == null) return matrices;
        List<GeoBone> chain = new ArrayList<>();
        for (GeoBone b = bone; b != null; b = b.getParent()) {
            chain.add(b);
        }
        Collections.reverse(chain);
        // matrices.translate(0.5F, 0.51F, 0.5F);
        for (int i = 0; i < chain.size(); i++) {
            GeoBone b = chain.get(i);
            matrices.translate(-b.getPosX(), b.getPosY(), b.getPosZ());
            matrices.translate(b.getPivotX(), b.getPivotY(), b.getPivotZ());
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(b.getRotZ()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(b.getRotY()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(b.getRotX()));
            matrices.scale(b.getScaleX(), b.getScaleY(), b.getScaleZ());
            if (i < chain.size() - 1) {
                matrices.translate(-b.getPivotX(), -b.getPivotY(), -b.getPivotZ());
            }
        }
        return matrices;
    }

    public static Vec3d getPartScale(ModelPart part) {
        return new Vec3d(part.xScale, part.yScale, part.zScale);
    }

    public static @Nullable FormRenderer searchFirstRenderer(PlayerEntity player, Predicate<FormRenderer> predicate) {
        return getPlayerAllFormRenderer(player).stream().filter(predicate).findFirst().orElse(null);
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
            // IForm playerFormBase = FormUtils.getPlayerForm(player);
            // if (playerFormBase instanceof DynamicForm pfd) {
            //     List<FormRenderer> formRenderers = new ArrayList<>();
            //     Pair<Identifier, Identifier> currentLayer = pfd.getCurrentRenderLayer();
            //     if (currentLayer != null) {
            //         FormRenderer formRenderer = FormRenderUtils.getFormRenderer(currentLayer.getLeft(), currentLayer.getRight());
            //         if (formRenderer == null) {
            //             ShapeShifterCurseFabric.LOGGER.warn("ShapeShifterCurseFabric: PlayerFormDynamic.layerRenderOverwrite is not null, but the model is not registered: {} - {}", currentLayer.getLeft(), currentLayer.getRight());
            //             return new ArrayList<>();
            //         }
            //         formRenderers.add(formRenderer);
            //         return formRenderers;
            //     }
            // }
            IForm playerFormBase = FormUtils.getPlayerForm(player);
            Pair<Identifier, Identifier> currentLayer = playerFormBase.getRenderLayerOverride();
            if (currentLayer != null) {
                List<FormRenderer> formRenderers = new ArrayList<>();
                FormRenderer formRenderer = FormRenderUtils.getFormRenderer(currentLayer.getLeft(), currentLayer.getRight());
                if (formRenderer == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("ShapeShifterCurseFabric: IForm.layerRenderOverwrite is not null, but the model is not registered: {} - {}", currentLayer.getLeft(), currentLayer.getRight());
                    return new ArrayList<>();
                }
                formRenderers.add(formRenderer);
                return formRenderers;
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
