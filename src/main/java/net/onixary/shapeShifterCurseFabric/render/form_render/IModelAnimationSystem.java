package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import mod.azure.azurelib.cache.object.GeoBone;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IModelAnimationSystem {
    public void loadConfig(@Nullable JsonObject json);

    public default void beforeRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {}

    public void processAnimation(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);

    public default void afterRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {}

    public default void finishRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {}

    public default @Nullable GeoBone beforeRenderFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) { return geoBone; }

    public @Nullable GeoBone processAnimationFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve);

    public default @Nullable GeoBone afterRenderFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) { return geoBone; }

}
