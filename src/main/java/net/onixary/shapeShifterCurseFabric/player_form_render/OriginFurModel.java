package net.onixary.shapeShifterCurseFabric.player_form_render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kosmx.playerAnim.core.util.Vec3f;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import it.unimi.dsi.fastutil.longs.Long2ReferenceLinkedOpenHashMap;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OriginFurModel extends GeoModel<OriginFurAnimatable> {
    public static final OriginFurModel NULL_OR_EMPTY_MODEL = new OriginFurModel(JsonParser.parseString("{}").getAsJsonObject());
    PlayerEntity entity;
    public JsonObject json;
    public OriginFurModel(JsonObject json) {
        this.recompile(json);
    }

    public Vec3f getPositionForBone(String bone) {
        var b = getCachedGeoBone(bone);
        if (b == null) { return Vec3f.ZERO;}
        var pos = b.getLocalPosition();
        return new Vec3f((float) pos.x, (float) pos.y, (float) pos.z);
    }
    public Vec3d getPositionForBoneD(String bone) {
        var b = getCachedGeoBone(bone);
        if (b == null) { return Vec3d.ZERO;}
        var pos = b.getModelPosition();
        return new Vec3d((float) pos.x, (float) pos.y, (float) pos.z);
    }

    public GeoBone setPositionForBone(String bone_name, Vec3f dTransform) {
        return setPositionForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }
    public GeoBone setWorldPositionForBone(String bone_name, Vec3f dTransform) {
        return setWorldPositionForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }

    private GeoBone setWorldPositionForBone(String bone_name, Vec3d vec3d) {
        var b = getCachedGeoBone(bone_name);
        if (b == null) { return null;}
        Vector4f vec = b.getWorldSpaceMatrix().mul(new Matrix4f()).transform(new Vector4f((float) vec3d.x, (float) vec3d.y, (float) vec3d.z, 1.0f));

        return b;
    }

    public GeoBone setPivotForBone(String bone_name, Vec3f dTransform) {
        return setPivotForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }
    public GeoBone setModelPositionForBone(String bone_name, Vec3f dTransform) {
        return setModelPositionForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }

    public GeoBone setRotationForBone(String bone_name, Vec3f dTransform) {
        return setRotationForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }
    public GeoBone setRotationForBone(String bone_name, Vec3f dTransform, boolean iX, boolean iY, boolean iZ) {
        return setRotationForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()), iX, iY, iZ);
    }

    public GeoBone translatePositionForBone(String bone_name, Vec3f dTransform) {
        return translatePositionForBone(bone_name, new Vec3d(dTransform.getX(), dTransform.getY(), dTransform.getZ()));
    }
    public GeoBone translateRotationForBone(String bipedLeftLeg, Vec3f leftLeg) {
        return translateRotationForBone(bipedLeftLeg, new Vec3d(leftLeg.getX(),leftLeg.getY(), leftLeg.getZ()));
    }

    public enum VMP {
        leftArm, rightArm, rightSleeve, leftSleeve, rightLeg, leftLeg, rightPants, leftPants, hat, head, body, jacket;

        public static final EnumSet<VMP> ALL_OPTS = EnumSet.allOf(VMP.class);
    }
    public EnumSet<VMP> hiddenParts = EnumSet.noneOf(VMP.class);
    public EnumSet<VMP> getHiddenParts() {
        return hiddenParts;
    }
    private boolean dirty = false;
    public void markDirty() {
        dirty = true;
    }
    public void preprocess(Origin origin, PlayerEntityRenderer playerRenderer, IPlayerEntityMixins playerEntity, ModelRootAccessor model, AbstractClientPlayerEntity abstractClientPlayerEntity) {
        getAnimationProcessor().getRegisteredBones().forEach(coreGeoBone -> {
            // unfortunately, you cannot do this with switch statements..
            coreGeoBone.setHidden(false);
            coreGeoBone.setHidden(coreGeoBone.getName().endsWith("thin_only") && !model.originalFur$isSlim());
            if (coreGeoBone.isHidden()) {
                return;
            }
            coreGeoBone.setHidden(coreGeoBone.getName().endsWith("wide_only") && model.originalFur$isSlim());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("elytra_hides"));
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("helmet_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("chestplate_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("leggings_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.LEGS).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("boots_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            for (var modid : FabricLoader.getInstance().getAllMods()){
                var id = modid.getMetadata().getId();
                if (coreGeoBone.getName().contains("mod_hides_IDS_QUIVERS") && Arrays.asList(OriginalFur.QUIVER_MODS).contains(id)) {
                    coreGeoBone.setHidden(true);
                    return;
                }
                if (coreGeoBone.getName().contains("mod_hides_"+id)) {
                    coreGeoBone.setHidden(true);
                    return;
                }
            }
        });
    }
    public void preRender$mixinOnly(PlayerEntity player) {
        this.entity = player;
        this.currentOverride = this.getPredicateResources(player);
    }
    @SuppressWarnings("SpellCheckingInspection")
    public void parseHiddenParts() {
        var set = EnumSet.noneOf(VMP.class);
        if (json.has("hidden")) {
            var h = json.getAsJsonArray("hidden");
            h.forEach(jsonElement -> {
                switch (jsonElement.getAsString().toLowerCase()) {
                    case "rightsleeve"-> set.add(VMP.rightSleeve);
                    case "leftsleeve"-> set.add(VMP.leftSleeve);
                    case "rightarm"-> set.add(VMP.rightArm);
                    case "leftarm"-> set.add(VMP.leftArm);
                    case "leftpant", "leftpants" -> set.add(VMP.leftPants);
                    case "rightpant", "rightpants" -> set.add(VMP.rightPants);
                    case "rightleg" -> set.add(VMP.rightLeg);
                    case "leftleg" -> set.add(VMP.leftLeg);
                    case "hat", "hair" -> set.add(VMP.hat);
                    case "head" -> set.add(VMP.head);
                    case "body", "torso" -> set.add(VMP.body);
                    case "jacket" -> set.add(VMP.jacket);
                }
            });
        }
        hiddenParts.clear();
        hiddenParts.addAll(set);
    }
    public void recompile(JsonObject json) {
        this.json = json;
        hiddenParts.clear();
        parseHiddenParts();
        boneCache.clear();
//        AzureLibCache.getBakedModels().remove(this.getModelResource(null));
        // Force cache this model! This is so getCachedGeoModel will not throw an exception unless the bone doesn't exist!
//        var bM = AzureLibCache.getBakedModels().put(this.getModelResource(null), this.getBakedModel(this.getModelResource(null)));
//        assert bM != null;
        if (this.json.has("overrides") && this.json.get("overrides").isJsonArray()) {
            JsonHelper.getArray(this.json,"overrides").forEach(jsonElement -> {
                var o = ResourceOverride.deserialize(jsonElement.getAsJsonObject());
                overrides.add(o);
            });
            overrides.sort((o1, o2) -> Float.compare(o1.weight, o2.weight));
        }

    }
    public boolean hasRenderingOffsets() {
        return json.has("rendering_offsets");
    }
    public boolean hasSubRenderingOffset(String id) {
        return hasRenderingOffsets() && json.getAsJsonObject("rendering_offsets").has(id);
    }
    public Vec3d getSubRenderingOffset(String id) {
        return hasSubRenderingOffset(id) ? alib.VectorFromJson(json.getAsJsonObject("rendering_offsets").get(id)) : Vec3d.ZERO;
    }
    public final Vec3d getRightOffset() {
        if (!hasSubRenderingOffset("right")) {
            return Vec3d.ZERO;
        }
        return alib.VectorFromJson(json.getAsJsonObject("rendering_offsets").get("right"));
    }
    public final Identifier getOverlayTexture(boolean slim) {
        if (!slim || !json.has("overlay_slim")) {
            if (json.has("overlay")) {
                return Identifier.tryParse(JsonHelper.getString(json, "overlay"));
            }
        } else {
            if (json.has("overlay_slim")) {
                return Identifier.tryParse(JsonHelper.getString(json, "overlay_slim"));
            }
        }
        return null;
    }
    public final Identifier getEmissiveTexture(boolean slim) {
        if (!slim || !json.has("emissive_overlay_slim")) {
            if (json.has("emissive_overlay")) {
                return Identifier.tryParse(JsonHelper.getString(json, "emissive_overlay"));
            }
        } else {

            if (json.has("emissive_overlay_slim")) {
                return Identifier.tryParse(JsonHelper.getString(json, "emissive_overlay_slim"));
            }
        }
        return null;
    }
    public final Vec3d getLeftOffset() {
        if (!hasSubRenderingOffset("left")) {
            return Vec3d.ZERO;
        }
        return alib.VectorFromJson(json.getAsJsonObject("rendering_offsets").get("left"));
    }
    public final boolean isPlayerModelInvisible() {return JsonHelper.getBoolean(json, "playerInvisible", false);}
    protected final LinkedHashMap<String,Stack<Vec3d>> posStack = new LinkedHashMap<>();
    protected final LinkedHashMap<String,Stack<Vec3d>> rotStack = new LinkedHashMap<>();
    protected final LinkedHashMap<String,Stack<Vec3d>> sclStack = new LinkedHashMap<>();
    public final void pushPos(String bone_name, Vec3d pos) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, posStack).push(new Vec3d(bone.getPosX(), bone.getPosY(), bone.getPosZ()));
        bone.setPosX((float) pos.x);
        bone.setPosY((float) pos.y);
        bone.setPosZ((float) pos.z);
    }
    public final void pushRot(String bone_name, Vec3d rot) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, rotStack).push(new Vec3d(bone.getRotX(), bone.getRotY(), bone.getRotZ()));
        bone.setRotX((float) rot.x);
        bone.setRotY((float) rot.y);
        bone.setRotZ((float) rot.z);
    }
    public final void pushScl(String bone_name, Vec3d scale) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, sclStack).push(new Vec3d(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()));
        bone.setScaleX((float) scale.x);
        bone.setScaleY((float) scale.y);
        bone.setScaleZ((float) scale.z);
    }
    public final void popPos(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, posStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));

    }
    public final void popRot(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, rotStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setRotX((float) pos.x);
        bone.setRotY((float) pos.y);
        bone.setRotZ((float) pos.z);
    }
    public final void popScl(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, sclStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setScaleX((float) pos.x);
        bone.setScaleY((float) pos.y);
        bone.setScaleZ((float) pos.z);
    }
    public final GeoBone getCachedGeoBone(String bone_name) {
        long hash = alib.getHash64(bone_name);
        if (boneCache.containsKey(hash)) {
            return boneCache.get(hash);
        }
        GeoBone b = this.getBone(bone_name).orElse(null);
        if (b == null) {
            return null;
        }
        boneCache.putAndMoveToFirst(hash, b);
        return b;
    }
    private Stack<Vec3d> getStackFor(String bone_name, LinkedHashMap<String, Stack<Vec3d>> stackMap) {
        if (!stackMap.containsKey(bone_name)) {
            posStack.put(bone_name, new Stack<>());
        }
        return posStack.get(bone_name);
    }
    public Long2ReferenceLinkedOpenHashMap<GeoBone> boneCache =  new Long2ReferenceLinkedOpenHashMap<>();
    public final GeoBone resetBone(String bone_name) {
        setPositionForBone(bone_name, new Vec3d(0,0,0));
        setRotationForBone(bone_name, new Vec3d(0,0,0));
        setModelPositionForBone(bone_name, Vec3d.ZERO);
        return setScaleForBone(bone_name, new Vec3d(1,1,1));
    }

    public final GeoBone setPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
//        b.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));
        b.setPosX((float)pos.x);
        b.setPosY((float)pos.y);
        b.setPosZ((float)pos.z);
        return (GeoBone) b;
    }
    public final GeoBone setPositionForBone(GeoBone b, Vec3d pos) {
        if (b == null) {
            return null;
        }
//        b.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));
        b.setPosX((float)pos.x);
        b.setPosY((float)pos.y);
        b.setPosZ((float)pos.z);
        return (GeoBone) b;
    }
    public final <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
    void renderBone(String bone_name, ModelPart part, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light,
                    RenderLayer layer, OriginalFurClient.OriginFur geoModel) {
        Vec3d pos = ((IMojModelPart)(Object)part).originfurs$getPosition().multiply(-1/16f);
        Vec3d rot = ((IMojModelPart)(Object)part).originfurs$getRotation();
        setPositionForBone(bone_name, pos);
        setRotationForBone(bone_name, rot);
        geoModel.renderBone(bone_name, matrixStack, vertexConsumerProvider, layer, null, light);
    }
    public final <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
    void renderBone(String bone_name, ModelPart part, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light,
                    RenderLayer layer, OriginalFurClient.OriginFur geoModel, Vec3d pos, Vec3d rot) {
        setPositionForBone(bone_name, pos);
        setRotationForBone(bone_name, rot);
        geoModel.renderBone(bone_name, matrixStack, vertexConsumerProvider, layer, null, light);
    }
    public final GeoBone setPositionForBone(String bone_name, Vec3d pos, float div) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setModelPosition(new Vector3d(pos.x / div, pos.y / div, pos.z / div));
//        b.setPosX((float)pos.x);
//        b.setPosY((float)pos.y);
//        b.setPosZ((float)pos.z);
        return (GeoBone) b;
    }
    public final GeoBone setPivotForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setPivotX((float)pos.x);
        b.setPivotY((float)pos.y);
        b.setPivotZ((float)pos.z);
        return (GeoBone) b;
    }
    public final GeoBone setModelPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));
        return (GeoBone) b;
    }
    public final GeoBone translatePositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        var posOut = new Vec3d(pos.x + b.getPosX(), (float)pos.y + b.getPosY(),(float)pos.z + b.getPosZ());
        return this.setPositionForBone(bone_name, posOut);
    }
    public final GeoBone translateModelPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        Vector3d o = new Vector3d(pos.x + b.getPosX(), pos.z + b.getPosZ(), pos.z + b.getPosZ());
        b.setModelPosition(o);
        return (GeoBone) b;
    }
    public final GeoBone translateRotationForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setRotX((float)pos.x + b.getRotX());
        b.setRotY((float)pos.y + b.getRotY());
        b.setRotZ((float)pos.z + b.getRotZ());
        return (GeoBone) b;
    }
    public final GeoBone setRotationForBone(String bone_name, Vec3d rot) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setRotX((float)rot.x);
        b.setRotY((float)rot.y);
        b.setRotZ((float)rot.z);
        return (GeoBone) b;
    }
    public final GeoBone setRotationForBone(String bone_name, Vec3d rot, boolean iX, boolean iY, boolean iZ) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setRotX((float)rot.x * (iX ? -1 : 1));
        b.setRotY((float)rot.y * (iY ? -1 : 1));
        b.setRotZ((float)rot.z * (iZ ? -1 : 1));
        return (GeoBone) b;
    }

    public final void setRotationForTailBones(float limbAngle, float limbDistance, float age, float tailDragAmount, float tailDragAmountVertical){
        // logic fetched from minecraft Changed mod
        // tail bone name as "tail_0", "tail_1", "tail_2", "tail_3", "tail_4", "tail_5"
        // max num is 6
        // need to inverse the y rotation of the tail bones
        // first tail part
        float SWAY_RATE = 0.33333334F * 0.5F;
        float SWAY_SCALE = 0.05F;
        JsonObject tailChain = json.getAsJsonObject("tail_chain");
        if(tailChain == null) {return;}
        // Iterate over all members of tail_chain
        for (Map.Entry<String, JsonElement> entry : tailChain.entrySet()) {
            String memberName = entry.getKey();
            JsonArray memberArray = entry.getValue().getAsJsonArray();
            int indexCount = memberArray.size();

            var tail = this.getCachedGeoBone(memberName + "_" + 0);
            if (tail != null) {
                float tailSway = SWAY_SCALE * MathHelper.cos(age * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
                float tailBalance = MathHelper.cos(limbAngle * 0.6662F) * 0.325F * limbDistance;
                tail.setRotY(-MathHelper.lerp(limbDistance, tailSway, tailBalance) - tailDragAmount * 0.75F);
                tail.setRotX(-tailDragAmountVertical * 0.75f);

                float offset = 0.0F;
                for(int i = 1; i < indexCount; i++){
                    var bone = this.getCachedGeoBone(memberName + "_" + i);
                    if (bone == null) {continue;}
                    bone.setRotY(- MathHelper.lerp(limbDistance, SWAY_SCALE * MathHelper.cos(age * SWAY_RATE -
                            (((float)Math.PI / 3.0F) * offset)), 0.0f) - tailDragAmount * 0.75F);
                    bone.setRotX(-tailDragAmountVertical * 0.75f * (offset + 0.75f));

                    offset += 0.75F;
                }
            }
        }
    }

    public final void setRotationForHeadTailBones(float headAngle, float age, float tailDragAmount, float tailDragAmountVertical){
        // When tail bone is attached on head:

        // logic fetched from minecraft Changed mod
        // tail bone name as "tail_head_0", "tail_head_1", "tail_head_2", "tail_head_3", "tail_head_4", "tail_head_5"
        // max num is 6
        // need to inverse the y rotation of the tail bones
        // first tail part
        float SWAY_RATE = 0.33333334F * 0.5F;
        float SWAY_SCALE = 0.05F;
        JsonObject tailChain = json.getAsJsonObject("tail_chain_head");
        if(tailChain == null) {return;}
        // Iterate over all members of tail_chain
        for (Map.Entry<String, JsonElement> entry : tailChain.entrySet()) {
            String memberName = entry.getKey();
            JsonArray memberArray = entry.getValue().getAsJsonArray();
            int indexCount = memberArray.size();

            var tail = this.getCachedGeoBone(memberName + "_" + 0);
            if (tail != null) {
                float tailSway = SWAY_SCALE * MathHelper.cos(age * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
                float tailBalance = MathHelper.cos(headAngle * 0.6662F) * 0.325F * 0.1f;
                tail.setRotY(-MathHelper.lerp(0.1f, tailSway, tailBalance) - tailDragAmount * 0.75F);
                tail.setRotX(-tailDragAmountVertical * 0.75f);

                float offset = 0.0F;
                for(int i = 1; i < indexCount; i++){
                    var bone = this.getCachedGeoBone(memberName + "_" + i);
                    if (bone == null) {continue;}
                    bone.setRotY(- MathHelper.lerp(0.1f, SWAY_SCALE * MathHelper.cos(age * SWAY_RATE -
                            (((float)Math.PI / 3.0F) * offset)), 0.0f) - tailDragAmount * 0.75F);
                    bone.setRotX(-tailDragAmountVertical * 0.75f * (offset + 0.75f));

                    offset += 0.75F;
                }
            }
        }
    }

    public final void setRotationForWingBones(float limbAngle, float limbDistance, float age, float tailDragAmountVertical){
        // json define as wing_chain_l and wing_chain_r
        // wing bone name as "wing_l_0", "wing_l_1", "wing_l_2", "wing_r_0", "wing_r_1", "wing_r_2"
        // max num is 6

        float swayAngle = age * 20.0F * (float) (Math.PI / 180.0) + limbAngle;
        float sway_base = MathHelper.cos(swayAngle) * (float) Math.PI * 0.15F + limbDistance;
        float sway_l = (float) -(Math.PI / 4) + sway_base;
        float sway_r = (float) (Math.PI / 4) - sway_base;

        JsonObject wingChainL = json.getAsJsonObject("wing_chain_l");
        if(wingChainL == null) {return;}
        // Iterate over all members of wing_chain
        for (Map.Entry<String, JsonElement> entry : wingChainL.entrySet()) {
            String memberName = entry.getKey();
            JsonArray memberArray = entry.getValue().getAsJsonArray();
            int indexCount = memberArray.size();

            var wing = this.getCachedGeoBone(memberName + "_" + 0);
            if (wing != null) {
                wing.setRotY(sway_l);
                wing.setRotX(-tailDragAmountVertical * 0.35f);

                float offset = 0.0F;
                for(int i = 1; i < indexCount; i++){
                    var bone = this.getCachedGeoBone(memberName + "_" + i);
                    if (bone == null) {continue;}
                    bone.setRotX(-tailDragAmountVertical * 0.75f * offset);

                    offset += 0.75F;
                }
            }
        }
        JsonObject wingChainR = json.getAsJsonObject("wing_chain_r");
        if(wingChainR == null) {return;}
        // Iterate over all members of wing_chain
        for (Map.Entry<String, JsonElement> entry : wingChainR.entrySet()) {
            String memberName = entry.getKey();
            JsonArray memberArray = entry.getValue().getAsJsonArray();
            int indexCount = memberArray.size();

            var wing = this.getCachedGeoBone(memberName + "_" + 0);
            if (wing != null) {
                wing.setRotY(sway_r);
                wing.setRotX(-tailDragAmountVertical * 0.35f);

                float offset = 0.0F;
                for(int i = 1; i < indexCount; i++){
                    var bone = this.getCachedGeoBone(memberName + "_" + i);
                    if (bone == null) {continue;}
                    bone.setRotX(-tailDragAmountVertical * 0.75f * offset);

                    offset += 0.75F;
                }
            }
        }
    }




    public final GeoBone setScaleForBone(String bone_name, Vec3d scale) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setScaleX((float)scale.x);
        b.setScaleY((float)scale.y);
        b.setScaleZ((float)scale.z);
        return (GeoBone) b;
    }
    public final GeoBone setTransformationForBone(String bone_name, Vec3d pos, Vec3d scale, Vec3d eulers){
        setModelPositionForBone(bone_name, pos);
        setRotationForBone(bone_name, eulers);
        return setScaleForBone(bone_name,scale);

    }
    public final GeoBone copyFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d pos = new Vec3d(part.pivotX, part.pivotY, part.pivotZ);
        Vec3d scale = new Vec3d(part.xScale, part.yScale, part.zScale);
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        return setTransformationForBone(bone_name, pos, scale, rott);
    }
    public final GeoBone copyFromModelTransformation(String bone_name, FurRenderFeature.ModelTransformation part) {
        return setTransformationForBone(bone_name, part.position, new Vec3d(1,1,1), part.rotation);
    }
    public final GeoBone copyFromMojangModelPart(String bone_name, ModelPart part, boolean inverted) {
        Vec3d pos = new Vec3d(part.pivotX, part.pivotY, part.pivotZ);
        Vec3d scale = new Vec3d(part.xScale, part.yScale, part.zScale);
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        if (inverted) {
            rott = new Vec3d(part.pitch, part.yaw, part.roll);
        }
        return setTransformationForBone(bone_name, pos, scale, rott);
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part, boolean inverted) {
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        if (inverted) {
            rott = new Vec3d(part.pitch, part.yaw, part.roll);
        }
        return setRotationForBone(bone_name,rott);
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part, boolean invertedX, boolean invertedY, boolean invertedZ) {
        Vec3d rott = new Vec3d(-part.getTransform().pitch, -part.getTransform().yaw, -part.getTransform().roll);
        return setRotationForBone(bone_name,rott, invertedX, invertedY, invertedZ);
    }
    public final GeoBone invertRotForPart(String bone_name, boolean x, boolean y, boolean z) {
        var b = getCachedGeoBone(bone_name);
        if (b == null) {return null;}
        var r =b.getRotationVector().mul(x ? -1 : 1, y ? -1 : 1, z ? -1 : 1);
        b.setRotX((float) r.x);
        b.setRotY((float) r.y);
        b.setRotZ((float) r.z);
        return b;
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        return setRotationForBone(bone_name,rott);
    }
    public final GeoBone copyPosFromMojangModelPart(String bone_name, ModelPart part) {
        var t = part.getTransform();
        Vec3d rott = new Vec3d(t.pivotX / 16.0f, t.pivotY / 16.0f, t.pivotZ / 16.0f);
        return setWorldPositionForBone(bone_name,rott);

    }
    public final GeoBone copyPivotFromMojangModelPart(String bone_name, ModelPart part) {
        var t = part.getTransform();
        Vec3d rott = new Vec3d(t.pivotX, t.pivotY, t.pivotZ);
        return setPivotForBone(bone_name,rott);

    }
    public final GeoBone copyScaleFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d rott = new Vec3d(part.xScale, part.yScale, part.zScale);
        return setPositionForBone(bone_name,rott);
    }
//                public GeoModel setTransformationForBone(String bone_name, Vec3d pos, Vec3d scale, Quaterniond quaternion){}
    private static class ResourceOverride {
    public NbtElement requirements;
    public Identifier textureResource = Identifier.tryParse("orif-defaults:textures/missing.png");
    public Identifier modelResource = Identifier.tryParse("orif-defaults:geo/missing.geo.json");
    public Identifier animationResource = Identifier.tryParse("orif-defaults:animations/missing.animation.json");
        public float weight;
        private static ResourceOverride deserializeBase(JsonObject object, ResourceOverride r) {
            r.requirements = alib.json2NBT(object.get("condition"));
            r.weight = JsonHelper.getFloat(object, "weight", 1f);
            return r;
        }
        public static ResourceOverride deserialize(JsonObject object) {
            var r = deserializeBase(object, new ResourceOverride());
            r.textureResource = Identifier.tryParse(JsonHelper.getString(object, "texture", "orif-defaults:textures/missing.png"));
            r.modelResource = Identifier.tryParse(JsonHelper.getString(object, "model", "orif-defaults:geo/missing.geo.json"));
            r.animationResource = Identifier.tryParse(JsonHelper.getString(object, "animation", "orif-defaults:animations/missing.animation.json"));
            return r;
        }
    }
    public List<ResourceOverride> overrides = new ArrayList<>();
    Identifier getTextureResource_P(PlayerEntity entity) {
        AtomicReference<Identifier> override = new AtomicReference<>();
        if (!overrides.isEmpty()) {
            AtomicBoolean _continue = new AtomicBoolean(true);
            var nbt = entity.writeNbt(new NbtCompound());
            entity.writeCustomDataToNbt(nbt);
            overrides.forEach(m_override -> {
                if (!_continue.get()){return;}
                if (alib.checkNBTEquals(m_override.requirements, nbt)){
                    _continue.set(false);
                    override.set(m_override.textureResource);
                }
            });
        }
        return override.get();
    }
    Identifier getModelResource_P(PlayerEntity entity) {
        AtomicReference<Identifier> override = new AtomicReference<>();
        if (!overrides.isEmpty()) {
            AtomicBoolean _continue = new AtomicBoolean(true);
            var nbt = entity.writeNbt(new NbtCompound());
            entity.writeCustomDataToNbt(nbt);
            overrides.forEach(m_override -> {
                if (!_continue.get()){return;}
                if (alib.checkNBTEquals(m_override.requirements, nbt)){
                    _continue.set(false);
                    override.set(m_override.modelResource);
                }
            });
        }
        return override.get();
    }
    Identifier getAnimationResource_P(PlayerEntity entity) {
        AtomicReference<Identifier> override = new AtomicReference<>();
        if (!overrides.isEmpty()) {
            AtomicBoolean _continue = new AtomicBoolean(true);
            var nbt = entity.writeNbt(new NbtCompound());
            entity.writeCustomDataToNbt(nbt);
            overrides.forEach(m_override -> {
                if (!_continue.get()){return;}
                if (alib.checkNBTEquals(m_override.requirements, nbt)){
                    _continue.set(false);
                    override.set(m_override.animationResource);
                }
            });
        }
        return override.get();
    }
    Identifier mRLast = null;
    public ResourceOverride getPredicateResources(PlayerEntity entity){
//        var mR = getModelResource_P(entity);
//        var tR = getTextureResource_P(entity);
//        var aR = getAnimationResource_P(entity);
//        if (mRLast != mR) {
//            boneCache.clear();
//            for (var bone : this.getAnimationProcessor().getRegisteredBones()) {
//                System.out.println(bone.getName());
//                boneCache.put(alib.getHash64(bone.getName()), (GeoBone) bone);
//            }
//            mRLast = mR;
//        }
//        currentOverride.modelResource = mR != null && mR.compareTo(dMR(json)) != 0 ? mR : dMR(json);
//        currentOverride.textureResource = tR != null && tR.compareTo(dTR(json)) != 0 ? tR : dTR(json);
//        currentOverride.animationResource = aR != null && aR.compareTo(dAR(json)) != 0 ? aR : dAR(json);

//
        return currentOverride;
    }
    ResourceOverride currentOverride = new ResourceOverride();
    public static Identifier dMR(JsonObject json) {
        return Identifier.tryParse(JsonHelper.getString(json, "model", "orif-defaults:geo/missing.geo.json"));
    }
    public static Identifier dTR(JsonObject json) {
        return Identifier.tryParse(JsonHelper.getString(json, "texture", "orif-defaults:textures/missing.png"));
    }
    public static Identifier dAR(JsonObject json) {
        return Identifier.tryParse(JsonHelper.getString(json, "animation", "orif-defaults:animations/missing.animation.json"));
    }
    @Override
    public Identifier getModelResource(OriginFurAnimatable geoAnimatable) {
        return dMR(json);
    }
    @Override
    public Identifier getTextureResource(OriginFurAnimatable geoAnimatable) {
        return dTR(json);
    }
    @Override
    public Identifier getAnimationResource(OriginFurAnimatable geoAnimatable) {
        return dAR(json);
    }
    public Identifier getFullbrightTextureResource(OriginFurAnimatable geoAnimatable) {
        var id = Identifier.tryParse(JsonHelper.getString(json, "fullbrightTexture", "orif-defaults:textures/missing.png"));
        return id;
    }
    public boolean hasCustomElytraTexture() {
        return json.has("elytraTexture");
    }
    public Identifier getElytraTexture() {
        return Identifier.tryParse(JsonHelper.getString(json, "elytraTexture", "textures/entity/elytra.png"));
    }
    public Identifier getHurtSoundResource() {
        return Identifier.tryParse(JsonHelper.getString(json, "hurtSound", "null"));
    }
}
