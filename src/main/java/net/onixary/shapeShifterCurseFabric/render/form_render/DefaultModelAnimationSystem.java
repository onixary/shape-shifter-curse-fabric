package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
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

import java.util.*;

public class DefaultModelAnimationSystem implements IModelAnimationSystem {
    public final List<Pair<String, String>> extraPartsMap = new ArrayList<>();

    public String leftArmGeoBoneID = "bipedLeftArm";
    public String rightArmGeoBoneID = "bipedRightArm";

    // 动画由动画库处理 没法重映射
    public String RM_HeadGeoBoneID = "bipedHead";
    public String RM_BodyGeoBoneID = "bipedBody";
    public String RM_LeftArmGeoBoneID = "bipedLeftArm";
    public String RM_RightArmGeoBoneID = "bipedRightArm";
    public String RM_LeftLegGeoBoneID = "bipedLeftLeg";
    public String RM_RightLegGeoBoneID = "bipedRightLeg";

    public partTransform headTransform = null;
    public partTransform bodyTransform = null;
    public partTransform leftArmTransform = null;
    public partTransform rightArmTransform = null;
    public partTransform leftLegTransform = null;
    public partTransform rightLegTransform = null;

    // 每个UUID只能用一个ModelAnimationSystem 改为全局变量比较省资源
    private static final HashMap<UUID, tailData> tailDataMap = new HashMap<>();
    private static class tailData {
        private float tailDragAmount = 0.0F;
        private float tailDragAmountO;
        private float currentTailDragAmount = 0.0F;
        private float tailDragAmountVertical = 0.0F;
        private float tailDragAmountVerticalO;
        private float currentTailDragAmountVertical = 0.0F;
    }

    private static final HashMap<UUID, neckLookData> neckLookDataMap = new HashMap<>();
    private static class neckLookData {
        private float headYaw;
        private float headPitch;
        private double lastRenderTick = Double.NaN;
        private boolean initialized = false;
    }

    private record NeckAngles(float headYaw, float headPitch) {}

    public static class partTransform {
        private final Vec3f pos;
        private final Vec3f rot;
        private final Vec3f pivot;
        public partTransform(Vec3f pos, Vec3f rot, Vec3f pivot) {
            this.pos = pos;
            this.rot = rot;
            this.pivot = pivot;
        }

        public void apply(@Nullable GeoBone bone) {
            if (bone == null) {
                return;
            }
            bone.setPosX(bone.getPosX() + pos.getX());
            bone.setPosY(bone.getPosY() + pos.getY());
            bone.setPosZ(bone.getPosZ() + pos.getZ());
            bone.setRotX(bone.getRotX() + rot.getX());
            bone.setRotY(bone.getRotY() + rot.getY());
            bone.setRotZ(bone.getRotZ() + rot.getZ());
            bone.setPivotX(bone.getPivotX() + pivot.getX());
            bone.setPivotY(bone.getPivotY() + pivot.getY());
            bone.setPivotZ(bone.getPivotZ() + pivot.getZ());
        }

        public static partTransform of(JsonObject json) {
            Vec3f pos, rot, pivot;
            if (json.has("pos_x") && json.has("pos_y") && json.has("pos_z")) {
                pos = new Vec3f(json.get("pos_x").getAsFloat(), json.get("pos_y").getAsFloat(), json.get("pos_z").getAsFloat());
            } else {
                pos = new Vec3f(0.0F, 0.0F, 0.0F);
            }
            if (json.has("rot_x") && json.has("rot_y") && json.has("rot_z")) {
                rot = new Vec3f(json.get("rot_x").getAsFloat(), json.get("rot_y").getAsFloat(), json.get("rot_z").getAsFloat());
            } else {
                rot = new Vec3f(0.0F, 0.0F, 0.0F);
            }
            if (json.has("pivot_x") && json.has("pivot_y") && json.has("pivot_z")) {
                pivot = new Vec3f(json.get("pivot_x").getAsFloat(), json.get("pivot_y").getAsFloat(), json.get("pivot_z").getAsFloat());
            } else {
                pivot = new Vec3f(0.0F, 0.0F, 0.0F);
            }
            return new partTransform(pos, rot, pivot);
        }
    }


    /*
    "extra_parts_map": {
      "__anim_part__": "__model_part__"
    },
    "first_person_render": {
      "left_arm": "bipedLeftArm",
      "right_arm": "bipedRightArm"
    },
    "model_part_map": {
      "head": "bipedHead",
      "body": "bipedBody",
      "left_arm": "bipedLeftArm",
      "right_arm": "bipedRightArm",
      "left_leg": "bipedLeftLeg",
      "right_leg": "bipedRightLeg"
    },
    "part_extra_pos" {
      "head": {
        "pos_x": 0.0,
        "pos_y": 0.0,
        "pos_z": 0.0,
        "rot_x": 0.0,
        "rot_y": 0.0,
        "rot_z": 0.0,
        "pivot_x": 0.0,
        "pivot_y": 0.0,
        "pivot_z": 0.0
      }
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
        this.RM_HeadGeoBoneID = "bipedHead";
        this.RM_BodyGeoBoneID = "bipedBody";
        this.RM_LeftArmGeoBoneID = "bipedLeftArm";
        this.RM_RightArmGeoBoneID = "bipedRightArm";
        this.RM_LeftLegGeoBoneID = "bipedLeftLeg";
        this.RM_RightLegGeoBoneID = "bipedRightLeg";
        if (json.has("model_part_map")) {
            JsonObject modelPartMap = json.getAsJsonObject("model_part_map");
            if (modelPartMap.has("head")) {
                this.RM_HeadGeoBoneID = modelPartMap.get("head").getAsString();
            }
            if (modelPartMap.has("body")) {
                this.RM_BodyGeoBoneID = modelPartMap.get("body").getAsString();
            }
            if (modelPartMap.has("left_arm")) {
                this.RM_LeftArmGeoBoneID = modelPartMap.get("left_arm").getAsString();
            }
            if (modelPartMap.has("right_arm")) {
                this.RM_RightArmGeoBoneID = modelPartMap.get("right_arm").getAsString();
            }
            if (modelPartMap.has("left_leg")) {
                this.RM_LeftLegGeoBoneID = modelPartMap.get("left_leg").getAsString();
            }
            if (modelPartMap.has("right_leg")) {
                this.RM_RightLegGeoBoneID = modelPartMap.get("right_leg").getAsString();
            }
        }
        if (json.has("part_extra_pos")) {
            JsonObject partExtraPos = json.getAsJsonObject("part_extra_pos");
            if (partExtraPos.has("head")) {
                this.headTransform = partTransform.of(partExtraPos.get("head").getAsJsonObject());
            }
            if (partExtraPos.has("body")) {
                this.bodyTransform = partTransform.of(partExtraPos.get("body").getAsJsonObject());
            }
            if (partExtraPos.has("left_arm")) {
                this.leftArmTransform = partTransform.of(partExtraPos.get("left_arm").getAsJsonObject());
            }
            if (partExtraPos.has("right_arm")) {
                this.rightArmTransform = partTransform.of(partExtraPos.get("right_arm").getAsJsonObject());
            }
            if (partExtraPos.has("left_leg")) {
                this.leftLegTransform = partTransform.of(partExtraPos.get("left_leg").getAsJsonObject());
            }
            if (partExtraPos.has("right_leg")) {
                this.rightLegTransform = partTransform.of(partExtraPos.get("right_leg").getAsJsonObject());
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
        tailData td = tailDataMap.computeIfAbsent(player.getUuid(), k -> new tailData());
        float targetDrag = MathHelper.lerp(tickDelta, td.tailDragAmountO, td.tailDragAmount);
        td.currentTailDragAmount = MathHelper.lerp(0.04f, td.currentTailDragAmount, targetDrag);
        float targetDragVertical = MathHelper.lerp(tickDelta, td.tailDragAmountVerticalO, td.tailDragAmountVertical);
        td.currentTailDragAmountVertical = MathHelper.lerp(0.04f, td.currentTailDragAmountVertical, targetDragVertical);
    }


    @Override
    public void processAnimation(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        tailData td = tailDataMap.computeIfAbsent(player.getUuid(), k -> new tailData());
        model.resetBone(RM_HeadGeoBoneID);
        model.resetBone(RM_BodyGeoBoneID);
        model.resetBone(RM_LeftArmGeoBoneID);
        model.resetBone(RM_RightArmGeoBoneID);
        model.resetBone(RM_LeftLegGeoBoneID);
        model.resetBone(RM_RightLegGeoBoneID);
        for (Pair<String, String> pair : extraPartsMap) {
            ProcessExtraBone(model, player, pair.getLeft(), pair.getRight());
        }
        PlayerEntityModel<?> playerModel = renderer.getModel();
        model.setRotationForBone(RM_HeadGeoBoneID, FormRenderUtils.getPartRotation(playerModel.head));
        model.translatePositionForBone(RM_HeadGeoBoneID, FormRenderUtils.getPartPosition(playerModel.head));
        model.translatePositionForBone(RM_BodyGeoBoneID, FormRenderUtils.getPartPosition(playerModel.body));
        model.translatePositionForBone(RM_LeftArmGeoBoneID, FormRenderUtils.getPartPosition(playerModel.leftArm));
        model.translatePositionForBone(RM_RightArmGeoBoneID, FormRenderUtils.getPartPosition(playerModel.rightArm));
        model.translatePositionForBone(RM_LeftLegGeoBoneID, FormRenderUtils.getPartPosition(playerModel.leftLeg));
        model.translatePositionForBone(RM_RightLegGeoBoneID, FormRenderUtils.getPartPosition(playerModel.rightLeg));
        model.translatePositionForBone(RM_LeftArmGeoBoneID, new Vec3d(5, 2, 0));
        model.translatePositionForBone(RM_RightArmGeoBoneID, new Vec3d(-5, 2, 0));
        model.translatePositionForBone(RM_LeftLegGeoBoneID, new Vec3d(2, 12, 0));
        model.translatePositionForBone(RM_RightLegGeoBoneID, new Vec3d(-2, 12, 0));
        model.setRotationForBone(RM_BodyGeoBoneID, FormRenderUtils.getPartRotation(playerModel.body));
        model.setRotationForTailBones(limbAngle, limbDistance, player.age, td.currentTailDragAmount, td.currentTailDragAmountVertical);
        model.setRotationForWingBones(limbAngle, limbDistance, player.age, td.currentTailDragAmountVertical);
        if (this.bodyTransform != null) this.bodyTransform.apply(model.getCachedGeoBone(RM_BodyGeoBoneID));
        model.invertRotForPart(RM_BodyGeoBoneID, false, true, false);
        model.setRotationForBone(RM_LeftArmGeoBoneID, FormRenderUtils.getPartRotation(playerModel.leftArm));
        model.setRotationForBone(RM_RightArmGeoBoneID, FormRenderUtils.getPartRotation(playerModel.rightArm));
        model.setRotationForBone(RM_LeftLegGeoBoneID, FormRenderUtils.getPartRotation(playerModel.leftLeg));
        model.setRotationForBone(RM_RightLegGeoBoneID, FormRenderUtils.getPartRotation(playerModel.rightLeg));
        if (this.headTransform != null) this.headTransform.apply(model.getCachedGeoBone(RM_HeadGeoBoneID));
        if (this.leftArmTransform != null) this.leftArmTransform.apply(model.getCachedGeoBone(RM_LeftArmGeoBoneID));
        if (this.rightArmTransform != null) this.rightArmTransform.apply(model.getCachedGeoBone(RM_RightArmGeoBoneID));
        if (this.leftLegTransform != null) this.leftLegTransform.apply(model.getCachedGeoBone(RM_LeftLegGeoBoneID));
        if (this.rightLegTransform != null) this.rightLegTransform.apply(model.getCachedGeoBone(RM_RightLegGeoBoneID));
        model.invertRotForPart(RM_HeadGeoBoneID, false, true, true);
        model.invertRotForPart(RM_RightArmGeoBoneID, false, true, true);
        model.invertRotForPart(RM_LeftArmGeoBoneID, false, true, true);
        model.invertRotForPart(RM_LeftLegGeoBoneID, false, true, true);
        model.invertRotForPart(RM_RightLegGeoBoneID, false, true, true);
        NeckAngles neckAngles = model.hasNeckIk()
                ? getLongNeckAngles(player, tickDelta, headYaw, headPitch)
                : new NeckAngles(headYaw, headPitch);
        model.applyNeckIk(neckAngles.headYaw(), neckAngles.headPitch());
        model.setRotationForHeadTailBones(neckAngles.headYaw(), player.age, td.currentTailDragAmount, td.currentTailDragAmountVertical);
    }

    private NeckAngles getLongNeckAngles(PlayerEntity player, float tickDelta, float fallbackHeadYaw, float fallbackHeadPitch) {
        neckLookData data = neckLookDataMap.computeIfAbsent(player.getUuid(), k -> new neckLookData());
        float viewYaw = player.getYaw(tickDelta);
        float viewPitch = player.getPitch(tickDelta);
        float bodyYaw = lerpAngle(tickDelta, player.prevBodyYaw, player.bodyYaw);
        float targetHeadYaw = MathHelper.wrapDegrees(viewYaw - bodyYaw);
        float targetHeadPitch = viewPitch;
        double renderTick = player.age + tickDelta;
        float deltaTicks = 1.0F;
        if (Double.isFinite(data.lastRenderTick)) {
            deltaTicks = (float) MathHelper.clamp(renderTick - data.lastRenderTick, 0.0D, 1.0D);
        }
        data.lastRenderTick = renderTick;

        if (!data.initialized) {
            data.headYaw = Double.isFinite(targetHeadYaw) ? targetHeadYaw : fallbackHeadYaw;
            data.headPitch = Double.isFinite(targetHeadPitch) ? targetHeadPitch : fallbackHeadPitch;
            data.initialized = true;
            return new NeckAngles(data.headYaw, data.headPitch);
        }
        if (deltaTicks <= 0.0F) {
            return new NeckAngles(data.headYaw, data.headPitch);
        }

        float yawLerp = MathHelper.clamp(deltaTicks * 0.45F, 0.0F, 1.0F);
        float pitchLerp = MathHelper.clamp(deltaTicks * 0.35F, 0.0F, 1.0F);
        data.headYaw = lerpAngleAwayFrom(yawLerp, data.headYaw, targetHeadYaw, 180.0F);
        data.headPitch = MathHelper.lerp(pitchLerp, data.headPitch, targetHeadPitch);

        if (!Float.isFinite(data.headYaw)) {
            data.headYaw = fallbackHeadYaw;
        }
        if (!Float.isFinite(data.headPitch)) {
            data.headPitch = fallbackHeadPitch;
        }
        return new NeckAngles(data.headYaw, data.headPitch);
    }

    private static float lerpAngle(float delta, float start, float end) {
        return start + MathHelper.wrapDegrees(end - start) * delta;
    }

    private static float lerpAngleAwayFrom(float delta, float start, float end, float avoidAngle) {
        if (Math.abs(MathHelper.wrapDegrees(avoidAngle - end)) < 0.0001F) {
            return lerpAngle(delta, start, end);
        }

        start = MathHelper.wrapDegrees(start);
        end = MathHelper.wrapDegrees(end);

        float diff = MathHelper.wrapDegrees(end - start);
        float avoidDiff = MathHelper.wrapDegrees(avoidAngle - start);
        boolean flipDir = Math.signum(diff) == Math.signum(avoidDiff) && Math.abs(diff) > Math.abs(avoidDiff);

        if (flipDir) {
            diff = Math.copySign(360.0F - Math.abs(diff), -diff);
        }

        return MathHelper.wrapDegrees(start + diff * delta);
    }

    @Override
    public void afterRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        tailData td = tailDataMap.computeIfAbsent(player.getUuid(), k -> new tailData());
        td.tailDragAmountO = td.tailDragAmount;
        td.tailDragAmount *= 0.75F;
        td.tailDragAmount -= (float) (Math.toRadians((player.bodyYaw - player.prevBodyYaw)) * 0.55F);
        td.tailDragAmount = MathHelper.clamp(td.tailDragAmount, -1.6F, 1.6F);
        float verticalSpeed = (float) player.getVelocity().y;
        float targetVerticalDrag = MathHelper.clamp(verticalSpeed * 1.5f, -1.6f, 1.6f);
        td.tailDragAmountVertical *= 0.8F;
        td.tailDragAmountVertical += targetVerticalDrag * 0.15F;
        td.tailDragAmountVertical = MathHelper.clamp(td.tailDragAmountVertical, -1.6f, 1.6f);
        td.tailDragAmountVerticalO = td.tailDragAmountVertical;
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
