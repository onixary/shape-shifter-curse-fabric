package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultModelAnimationSystem implements IModelAnimationSystem {
    public final List<Pair<String, String>> extraPartsMap = new ArrayList<>();

    public String leftArmGeoBoneID = "bipedLeftArm";
    public String rightArmGeoBoneID = "bipedRightArm";

    private float tailDragAmount = 0.0F;
    private float tailDragAmountO;
    private float currentTailDragAmount = 0.0F;
    private float tailDragAmountVertical = 0.0F;
    private float tailDragAmountVerticalO;
    private float currentTailDragAmountVertical = 0.0F;

    /*
    "extra_parts_map": {
      "__anim_part__": "__model_part__"
    },
    "first_person_render": {
      "left_arm": "bipedLeftArm",
      "right_arm": "bipedRightArm"
    }
     */
    @Override
    public void loadConfig(@Nullable JsonObject json) {
        this.extraPartsMap.clear();
        if (json == null) {
            return;
        }
        if (json.has("extra_parts_map")) {
            JsonObject extraPartsMap = json.getAsJsonObject("extra_parts_map");
            for (String key : extraPartsMap.keySet()) {
                this.extraPartsMap.add(new Pair<>(key, extraPartsMap.get(key).getAsString()));
            }
        }
        this.leftArmGeoBoneID = "bipedLeftArm";
        this.rightArmGeoBoneID = "bipedRightArm";
        if (json.has("first_person_render")) {
            JsonObject firstPersonRender = json.getAsJsonObject("first_person_render");
            if (firstPersonRender.has("left_arm")) {
                this.leftArmGeoBoneID = firstPersonRender.get("left_arm").getAsString();
            }
            if (firstPersonRender.has("right_arm")) {
                this.rightArmGeoBoneID = firstPersonRender.get("right_arm").getAsString();
            }
        }
    }

    public void ProcessExtraBone(FormModel m, PlayerEntity player, String OriginFursBoneID, String AnimBoneID) {
        GeoBone bone =  m.resetBone(OriginFursBoneID);
        Vec3f AnimPosition = AnimSystem.getPlayerBone3DTransform(player, AnimBoneID, TransformType.POSITION, new Vec3f(0, 0, 0));
        m.setPositionForBone(OriginFursBoneID, new Vec3d(AnimPosition.getX(), -AnimPosition.getY(), -AnimPosition.getZ()));
        m.setRotationForBone(OriginFursBoneID, AnimSystem.getPlayerBone3DTransform(player, AnimBoneID, TransformType.ROTATION, new Vec3f(0, 0, 0)));
        m.invertRotForPart(OriginFursBoneID, false, true, true);
    }

    @Override
    public void beforeRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float targetDrag = MathHelper.lerp(tickDelta, tailDragAmountO, tailDragAmount);
        currentTailDragAmount = MathHelper.lerp(0.04f, currentTailDragAmount, targetDrag);
    }


    @Override
    public void processAnimation(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        model.resetBone("bipedHead");
        model.resetBone("bipedBody");
        model.resetBone("bipedLeftArm");
        model.resetBone("bipedRightArm");
        model.resetBone("bipedLeftLeg");
        model.resetBone("bipedRightLeg");
        for (Pair<String, String> pair : extraPartsMap) {
            ProcessExtraBone(model, player, pair.getLeft(), pair.getRight());
        }
        PlayerEntityModel<?> playerModel = renderer.getModel();
        model.setRotationForBone("bipedHead", FormRenderUtils.getPartRotation(playerModel.head));
        model.translatePositionForBone("bipedHead", FormRenderUtils.getPartPosition(playerModel.head));
        model.translatePositionForBone("bipedBody", FormRenderUtils.getPartPosition(playerModel.body));
        model.translatePositionForBone("bipedLeftArm", FormRenderUtils.getPartPosition(playerModel.leftArm));
        model.translatePositionForBone("bipedRightArm", FormRenderUtils.getPartPosition(playerModel.rightArm));
        model.translatePositionForBone("bipedLeftLeg", FormRenderUtils.getPartPosition(playerModel.leftLeg));
        model.translatePositionForBone("bipedRightLeg", FormRenderUtils.getPartPosition(playerModel.rightLeg));
        model.translatePositionForBone("bipedLeftArm", new Vec3d(5, 2, 0));
        model.translatePositionForBone("bipedRightArm", new Vec3d(-5, 2, 0));
        model.translatePositionForBone("bipedLeftLeg", new Vec3d(2, 12, 0));
        model.translatePositionForBone("bipedRightLeg", new Vec3d(-2, 12, 0));
        model.setRotationForBone("bipedBody", FormRenderUtils.getPartRotation(playerModel.body));
        model.setRotationForTailBones(limbAngle, limbDistance, player.age, currentTailDragAmount, tailDragAmountVertical);
        model.setRotationForHeadTailBones(headYaw, player.age, currentTailDragAmount, tailDragAmountVertical);
        model.setRotationForWingBones(limbAngle, limbDistance, player.age, tailDragAmountVertical);
        model.invertRotForPart("bipedBody", false, true, false);
        model.setRotationForBone("bipedLeftArm", FormRenderUtils.getPartRotation(playerModel.leftArm));
        model.setRotationForBone("bipedRightArm", FormRenderUtils.getPartRotation(playerModel.rightArm));
        model.setRotationForBone("bipedLeftLeg", FormRenderUtils.getPartRotation(playerModel.leftLeg));
        model.setRotationForBone("bipedRightLeg", FormRenderUtils.getPartRotation(playerModel.rightLeg));
        model.invertRotForPart("bipedHead", false, true, true);
        model.invertRotForPart("bipedRightArm", false, true, true);
        model.invertRotForPart("bipedLeftArm", false, true, true);
        model.invertRotForPart("bipedRightLeg", false, true, true);
        model.invertRotForPart("bipedLeftLeg", false, true, true);
    }

    @Override
    public void afterRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        tailDragAmountO = tailDragAmount;
        tailDragAmount *= 0.75F;
        tailDragAmount -= (float) (Math.toRadians((player.bodyYaw - player.prevBodyYaw)) * 0.55F);
        tailDragAmount = MathHelper.clamp(tailDragAmount, -1.6F, 1.6F);
        float verticalSpeed = (float) player.getVelocity().y;
        float targetVerticalDrag = MathHelper.clamp(verticalSpeed * 1.5f, -1.6f, 1.6f);
        float targetDragVertical = MathHelper.lerp(tickDelta, tailDragAmountVerticalO, tailDragAmountVertical);
        currentTailDragAmountVertical = MathHelper.lerp(0.04f, currentTailDragAmountVertical, targetDragVertical);
        tailDragAmountVertical *= 0.8F;
        tailDragAmountVertical += targetVerticalDrag * 0.15F;
        tailDragAmountVertical = MathHelper.clamp(tailDragAmountVertical, -1.6f, 1.6f);
        tailDragAmountVerticalO = tailDragAmountVertical;
    }

    @Override
    public @Nullable GeoBone beforeRenderFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) {
        boolean IsRenderRight = arm.equals(renderer.getModel().rightArm);
        String GeoBoneName = IsRenderRight ? this.rightArmGeoBoneID : this.leftArmGeoBoneID;
        Optional<GeoBone> OptionalGeoBone = model.getBone(GeoBoneName);
        if (OptionalGeoBone.isEmpty()) {
            // 有时AzureLib 未能及时注册 GeoBone 因此需要手动注册
            if (model.getAnimationProcessor().getRegisteredBones().isEmpty()) {
                ShapeShifterCurseFabric.LOGGER.info("GeoBone 未注册, 尝试重新注册模型");
                BakedGeoModel bakedGeoModel = model.getBakedModel(model.getModelResource(formRenderer.getAnimatable()));
                model.getAnimationProcessor().setActiveModel(bakedGeoModel);
            }
            return null;
        }
        geoBone = OptionalGeoBone.get();
        return geoBone;
    }

    @Override
    public @Nullable GeoBone processAnimationFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) {
        boolean IsRenderRight = arm.equals(renderer.getModel().rightArm);
        String GeoBoneName = IsRenderRight ? this.rightArmGeoBoneID : this.leftArmGeoBoneID;
        model.resetBone(GeoBoneName);
        model.translatePositionForBone(GeoBoneName, FormRenderUtils.getPartPosition(arm));
        model.translatePositionForBone(GeoBoneName, new Vec3d(5 * (IsRenderRight ? -1.0 : 1.0), 2, 0));
        model.setRotationForBone(GeoBoneName, FormRenderUtils.getPartRotation(arm));
        model.invertRotForPart(GeoBoneName, false, true, true);
        return geoBone;
    }
}
