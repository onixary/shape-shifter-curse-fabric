package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IModelAnimationSystem {
    public void loadConfig(@Nullable JsonObject json);

    public default void beforeRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {}

    public void processAnimation(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);

    public default void afterRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {}
}
