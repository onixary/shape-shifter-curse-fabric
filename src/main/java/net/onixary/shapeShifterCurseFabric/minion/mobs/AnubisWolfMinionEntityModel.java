package net.onixary.shapeShifterCurseFabric.minion.mobs;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.TintableAnimalModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AnubisWolfMinionEntityModel<T extends WolfEntity> extends TintableAnimalModel<T> {
    private static final String REAL_HEAD = "real_head";
    private static final String UPPER_BODY = "upper_body";
    private static final String REAL_TAIL = "real_tail";
    private final ModelPart head;
    //private final ModelPart realHead;
    private final ModelPart torso;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    //private final ModelPart realTail;
    private final ModelPart neck;
    private static final int field_32580 = 8;

    public AnubisWolfMinionEntityModel(ModelPart root) {
        this.head = root.getChild("head");
        //this.realHead = this.head.getChild("real_head");
        this.torso = root.getChild("body");
        this.neck = root.getChild("upperBody");
        this.rightHindLeg = root.getChild("leg0");
        this.leftHindLeg = root.getChild("leg1");
        this.rightFrontLeg = root.getChild("leg2");
        this.leftFrontLeg = root.getChild("leg3");
        this.tail = root.getChild("tail");
        //this.realTail = this.tail.getChild("real_tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new Dilation(0.0F))
                .uv(16, 14).cuboid(-3.4F, -5.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 14).cuboid(-2.3F, -6.0F, -0.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 14).cuboid(1.4F, -5.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 14).cuboid(1.3F, -6.0F, -0.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 10).cuboid(-1.5F, 0.9844F, -5.0F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 13.5F, -7.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 14).cuboid(-4.0F, -3.0F, -3.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData upperBody = modelPartData.addChild("upperBody", ModelPartBuilder.create().uv(21, 0).cuboid(-4.0F, -3.0F, -3.0F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F))
                .uv(43, 18).cuboid(-1.0F, -5.3F, 2.2F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 14.0F, -3.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData leg0 = modelPartData.addChild("leg0", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(52, 18).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 16.0F, 7.0F));

        ModelPartData leg1 = modelPartData.addChild("leg1", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(52, 18).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 16.0F, 7.0F));

        ModelPartData leg2 = modelPartData.addChild("leg2", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(52, 6).cuboid(-1.6F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 16.0F, -4.0F));

        ModelPartData leg3 = modelPartData.addChild("leg3", ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(52, 6).cuboid(0.6F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 16.0F, -4.0F));

        ModelPartData tail = modelPartData.addChild("tail", ModelPartBuilder.create().uv(9, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(52, 24).cuboid(-0.5F, 4.0F, 0.3F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 12.0F, 8.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.neck);
    }

    public void animateModel(T wolfEntity, float f, float g, float h) {
        if (wolfEntity.hasAngerTime()) {
            this.tail.yaw = 0.0F;
        } else {
            this.tail.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
        }

        if (wolfEntity.isInSittingPose()) {
            this.neck.setPivot(-1.0F, 16.0F, -3.0F);
            this.neck.pitch = 1.2566371F;
            this.neck.yaw = 0.0F;
            this.torso.setPivot(0.0F, 18.0F, 0.0F);
            this.torso.pitch = ((float)Math.PI / 4F);
            this.tail.setPivot(-1.0F, 21.0F, 6.0F);
            this.rightHindLeg.setPivot(-2.5F, 22.7F, 2.0F);
            this.rightHindLeg.pitch = ((float)Math.PI * 1.5F);
            this.leftHindLeg.setPivot(0.5F, 22.7F, 2.0F);
            this.leftHindLeg.pitch = ((float)Math.PI * 1.5F);
            this.rightFrontLeg.pitch = 5.811947F;
            this.rightFrontLeg.setPivot(-2.49F, 17.0F, -4.0F);
            this.leftFrontLeg.pitch = 5.811947F;
            this.leftFrontLeg.setPivot(0.51F, 17.0F, -4.0F);
        } else {
            this.torso.setPivot(0.0F, 14.0F, 2.0F);
            this.torso.pitch = ((float)Math.PI / 2F);
            this.neck.setPivot(-1.0F, 14.0F, -3.0F);
            this.neck.pitch = this.torso.pitch;
            this.tail.setPivot(-1.0F, 12.0F, 8.0F);
            this.rightHindLeg.setPivot(-2.5F, 16.0F, 7.0F);
            this.leftHindLeg.setPivot(0.5F, 16.0F, 7.0F);
            this.rightFrontLeg.setPivot(-2.5F, 16.0F, -4.0F);
            this.leftFrontLeg.setPivot(0.5F, 16.0F, -4.0F);
            this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
            this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * g;
            this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * g;
            this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
        }

        //this.realHead.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
        this.neck.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
        this.torso.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
        //this.realTail.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
    }

    public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180F);
        this.head.yaw = i * ((float)Math.PI / 180F);
        this.tail.pitch = h;
    }
}
