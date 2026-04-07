package net.onixary.shapeShifterCurseFabric.render.tech;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class ThirdPersonExtraHandItemRender<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {

    public static abstract class TPEHR_Render {
        public abstract void render(HeldItemRenderer heldItemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);
    }

    public record TPEHRData(Predicate<AbstractClientPlayerEntity> shouldRender, TPEHR_Render render) { }

    public static HashMap<PlayerFormBase, ArrayList<TPEHRData>> data = new HashMap<>();

    public final HeldItemRenderer heldItemRenderer;

    static {
        register(RegPlayerForms.SPIDER_3, new TPEHRData(p -> true, new SpiderTPEHR()));
    }

    public static void register(PlayerFormBase form, TPEHRData Rdata) {
        if (!data.containsKey(form)) {
            data.put(form, new ArrayList<>());
        }
        data.get(form).add(Rdata);
    }

    public ThirdPersonExtraHandItemRender(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (livingEntity instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            if (data.containsKey(curForm)) {
                for (TPEHRData Rdata : data.get(curForm)) {
                    if (Rdata.shouldRender().test(player)) {
                        matrixStack.push();
                        Rdata.render().render(heldItemRenderer, matrixStack, vertexConsumerProvider, i, player, f, g, h, j, k, l);
                        matrixStack.pop();
                    }
                }
            }
        }
    }
}
