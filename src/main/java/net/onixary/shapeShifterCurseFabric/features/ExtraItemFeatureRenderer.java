package net.onixary.shapeShifterCurseFabric.features;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

@Environment(EnvType.CLIENT)
public class ExtraItemFeatureRenderer <T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    //private final HeldItemRenderer heldItemRenderer;
    private final CustomFeralItemRenderer customFeralItemRenderer;
    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");

    public ExtraItemFeatureRenderer(FeatureRendererContext<T, M> context, EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(context);
        this.customFeralItemRenderer = new CustomFeralItemRenderer(MinecraftClient.getInstance(), entityRenderDispatcher, itemRenderer);
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            T livingEntity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {

        if (livingEntity instanceof ClientPlayerEntity player) {
            PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            boolean isFeral = curForm.getBodyType() == PlayerFormBodyType.FERAL;

            if (isFeral && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {

                if(IS_FIRST_PERSON_MOD_LOADED) {
                    // Feral形态的forstperson配置必须固定为-25 offset，否则会导致物品位置不正确以及模型看不到
                    FirstPersonModelCore fpm = FirstPersonModelCore.instance;
                    fpm.getConfig().xOffset = -25;
                    fpm.getConfig().sitXOffset = -25;
                    fpm.getConfig().sneakXOffset = -25;

                    // 已知Bug 在开启 FirstPersonModel 并启用时 且在第一人称时 物品位置不对
                    if (fpm.isEnabled()) {
                        // 仅限开启FirstPersonModel时渲染额外物品
                        matrices.push();
                        //var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
                        //var head = eR.getModel().head;
                        // FirstPerson Mod会直接将head.pivot移动到某个非常远的位置来“隐藏”头部，所以需要直接定义好一个固定位置
                        // Vec3d posOffset = new Vec3d(0.0F, 0.0F, 0.0F);
                        // Vec3d rotCenter = ShapeShifterCurseFabric.feralItemCenter;
                        Vec3d rotCenter = new Vec3d(0.0F, -4.0F, -6.0F);
                        matrices.translate(rotCenter.x / 16.0F, rotCenter.y / 16.0F, rotCenter.z / 16.0F);
                        //Vec3d posOffset = ShapeShifterCurseFabric.feralItemPosOffset;
                        Vec3d posOffset = new Vec3d(-12.0F, 15.0F, 4.0F);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(240.0F));
                        matrices.translate(posOffset.x / 16.0F, posOffset.y / 16.0F, posOffset.z / 16.0F);
                        float pitch = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
                        float equipProgress = 1.0F - MathHelper.lerp(tickDelta, customFeralItemRenderer.prevEquipProgressMainHand, customFeralItemRenderer.equipProgressMainHand);
                        // 调用第一人称物品渲染逻辑
                        customFeralItemRenderer.renderFirstPersonItem(
                                player,
                                tickDelta,
                                pitch,
                                Hand.MAIN_HAND,
                                player.getHandSwingProgress(tickDelta),
                                player.getMainHandStack(),
                                equipProgress,
                                matrices,
                                vertexConsumers,
                                light
                        );
                        matrices.pop();
                    }
                }
            }
        }
    }
}
