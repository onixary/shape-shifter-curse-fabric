package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kosmx.playerAnim.core.util.Vec3f;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class FormModel extends GeoModel<FormAnimatable> {
    public static List<FormModel> loadedModel = new ArrayList<>();
    public static HashMap<PlayerEntity, Boolean> SlimMap = new HashMap<>();

    public static final String MissingGeoString = ShapeShifterCurseFabric.MOD_ID + ":geo/missing.geo.json";
    public static final String MissingTextureString = ShapeShifterCurseFabric.MOD_ID + ":textures/missing.png";
    public static final String MissingAnimationString = ShapeShifterCurseFabric.MOD_ID + ":animations/missing.animation.json";

    public PlayerEntity entity;

    public JsonObject modelJson;

    public String Name = "";  // 用于皮肤系统 先留一下API
    public Identifier Layer = null;  // 用于皮肤系统 先留一下API
    public Identifier Form = null;  // 用于皮肤系统 先留一下API

    public static int modelIDIter = 0;
    public int modelID = -1;

    public boolean SlimOnly = false;
    public boolean WideOnly = false;
    public boolean UseMultiplyMask = false;
    public Identifier ModelResource = ShapeShifterCurseFabric.identifier("geo/missing.geo.json");
    public Identifier ModelResource_Slim = ShapeShifterCurseFabric.identifier("geo/missing.geo.json");

    public Identifier TextureResource = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier TextureMaskResource = null;
    public Identifier TextureResource_Slim = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier TextureMaskResource_Slim = null;

    public Identifier OverlayTextureResource = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier OverlayTextureMaskResource = null;
    public Identifier OverlayTextureResource_Slim = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier OverlayTextureMaskResource_Slim = null;

    public Identifier EmissiveTextureResource = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier EmissiveTextureMaskResource = null;
    public Identifier EmissiveTextureResource_Slim = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier EmissiveTextureMaskResource_Slim = null;

    public Identifier FullBrightTextureResource = ShapeShifterCurseFabric.identifier("textures/missing.png");
    public Identifier FullBrightTextureResource_Slim = ShapeShifterCurseFabric.identifier("textures/missing.png");

    public Identifier Animation = ShapeShifterCurseFabric.identifier("animations/missing.animation.json");

    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_Textures = new HashMap<>();
    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_Textures_Slim = new HashMap<>();

    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_OverlayTexture = new HashMap<>();
    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_OverlayTexture_Slim = new HashMap<>();

    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_EmissiveTexture = new HashMap<>();
    public HashMap<FormTextureUtils.ColorSetting, Identifier> ColorMask_Baked_EmissiveTexture_Slim = new HashMap<>();

    // Hidden Parts
    public boolean Hidden_Hat = false;
    public boolean Hidden_Head = false;
    public boolean Hidden_Body = false;
    public boolean Hidden_Jacket = false;
    public boolean Hidden_LeftArm = false;
    public boolean Hidden_RightArm = false;
    public boolean Hidden_LeftSleeve = false;
    public boolean Hidden_RightSleeve = false;
    public boolean Hidden_LeftLeg = false;
    public boolean Hidden_RightLeg = false;
    public boolean Hidden_LeftPants = false;
    public boolean Hidden_RightPants = false;

    public IModelAnimationSystem AnimationSystem = null;

    // builtin_controller_data
    // chain -> [["tail0_0", "tail0_1"], [tail1_0", "tail1_1"]]
    public List<List<String>> BCD_TailChain = new ArrayList<>();
    public List<List<String>> BCD_TailChainHead = new ArrayList<>();
    public List<List<String>> BCD_WingChainL = new ArrayList<>();
    public List<List<String>> BCD_WingChainR = new ArrayList<>();

    public FormModel(JsonObject json) {
        this.modelJson = json;
        this.CompileModel();
        this.modelID = modelIDIter++;
    }

    public void CompileModel() {
        this.ColorMask_Baked_Textures.clear();
        this.ColorMask_Baked_Textures_Slim.clear();
        this.ColorMask_Baked_OverlayTexture.clear();
        this.ColorMask_Baked_OverlayTexture_Slim.clear();
        this.ColorMask_Baked_EmissiveTexture.clear();
        this.ColorMask_Baked_EmissiveTexture_Slim.clear();

        this.Name = JsonHelper.getString(this.modelJson, "name", "");
        if (this.modelJson.has("layer")) {
            this.Layer = Identifier.tryParse(JsonHelper.getString(this.modelJson, "layer", ""));
        } else {
            this.Layer = null;
        }
        if (this.modelJson.has("form")) {
            this.Form = Identifier.tryParse(JsonHelper.getString(this.modelJson, "form", ""));
        } else {
            this.Form = null;
        }
        this.SlimOnly = JsonHelper.getBoolean(this.modelJson, "slim_only", false);
        this.WideOnly = JsonHelper.getBoolean(this.modelJson, "wide_only", false);
        this.UseMultiplyMask = JsonHelper.getBoolean(this.modelJson, "use_multiply_mask", false);

        this.ModelResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "model", MissingGeoString));
        this.ModelResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "model_slim", MissingGeoString));

        this.TextureResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "texture", MissingTextureString));
        this.TextureResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "texture_slim", MissingTextureString));
        if (this.modelJson.has("texture_mask")) {
            this.TextureMaskResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "texture_mask", MissingTextureString));
        } else {
            this.TextureMaskResource = null;
        }
        if (this.modelJson.has("texture_mask_slim")) {
            this.TextureMaskResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "texture_mask_slim", MissingTextureString));
        } else {
            this.TextureMaskResource_Slim = null;
        }

        this.OverlayTextureResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "overlay", MissingTextureString));
        this.OverlayTextureResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "overlay_slim", MissingTextureString));
        if (this.modelJson.has("overlay_mask")) {
            this.OverlayTextureMaskResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "overlay_mask", MissingTextureString));
        } else {
            this.OverlayTextureMaskResource = null;
        }
        if (this.modelJson.has("overlay_mask_slim")) {
            this.OverlayTextureMaskResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "overlay_mask_slim", MissingTextureString));
        } else {
            this.OverlayTextureMaskResource_Slim = null;
        }

        this.EmissiveTextureResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "emissive_overlay", MissingTextureString));
        this.EmissiveTextureResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "emissive_overlay_slim", MissingTextureString));
        if (this.modelJson.has("emissive_overlay_mask")) {
            this.EmissiveTextureMaskResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "emissive_overlay_mask", MissingTextureString));
        } else {
            this.EmissiveTextureMaskResource = null;
        }
        if (this.modelJson.has("emissive_overlay_mask_slim")) {
            this.EmissiveTextureMaskResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "emissive_overlay_mask_slim", MissingTextureString));
        } else {
            this.EmissiveTextureMaskResource_Slim = null;
        }

        this.FullBrightTextureResource = Identifier.tryParse(JsonHelper.getString(this.modelJson, "fullbright_texture", MissingTextureString));
        this.FullBrightTextureResource_Slim = Identifier.tryParse(JsonHelper.getString(this.modelJson, "fullbright_texture_slim", MissingTextureString));

        this.Animation = Identifier.tryParse(JsonHelper.getString(this.modelJson, "animations", MissingAnimationString));

        this.Hidden_Hat = false;
        this.Hidden_Head = false;
        this.Hidden_Body = false;
        this.Hidden_Jacket = false;
        this.Hidden_LeftArm = false;
        this.Hidden_RightArm = false;
        this.Hidden_LeftSleeve = false;
        this.Hidden_RightSleeve = false;
        this.Hidden_LeftLeg = false;
        this.Hidden_RightLeg = false;
        this.Hidden_LeftPants = false;
        this.Hidden_RightPants = false;
        JsonArray hiddenArray = JsonHelper.getArray(this.modelJson, "hidden", null);
        if (hiddenArray != null) {
            for (int i = 0; i < hiddenArray.size(); i++) {
                String hidden = hiddenArray.get(i).getAsString();
                switch (hidden) {
                    case "hat" -> { this.Hidden_Hat = true; }
                    case "head" -> { this.Hidden_Head = true; }
                    case "body" -> { this.Hidden_Body = true; }
                    case "jacket" -> { this.Hidden_Jacket = true; }
                    case "leftArm" -> { this.Hidden_LeftArm = true; }
                    case "rightArm" -> { this.Hidden_RightArm = true; }
                    case "leftSleeve" -> { this.Hidden_LeftSleeve = true; }
                    case "rightSleeve" -> { this.Hidden_RightSleeve = true; }
                    case "leftLeg" -> { this.Hidden_LeftLeg = true; }
                    case "rightLeg" -> { this.Hidden_RightLeg = true; }
                    case "leftPants" -> { this.Hidden_LeftPants = true; }
                    case "rightPants" -> { this.Hidden_RightPants = true; }
                }
            }
        }
        this.AnimationSystem = null;
        if (this.modelJson.has("animation_system")) {
            this.AnimationSystem = FormRenderUtils.get_MAS(Identifier.tryParse(JsonHelper.getString(this.modelJson, "animation_system", null)), this.modelJson.getAsJsonObject("animation_system_config"));
        }
        if (AnimationSystem == null) {
            this.AnimationSystem = FormRenderUtils.get_MAS(FormRenderUtils.DEFAULT_MAS, null);
        }
        this.loadBCD();
    }

    public List<List<String>> loadChainData(JsonObject json) {
        List<List<String>> ChainData = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().isJsonArray()) {
                String base = entry.getKey();
                JsonArray array = entry.getValue().getAsJsonArray();
                List<String> chain = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    chain.add(base + "_" + array.get(i).getAsString());
                }
                ChainData.add(chain);
            }
        }
        return ChainData;
    }

    public void loadBCD() {
        BCD_TailChain.clear();
        BCD_TailChainHead.clear();
        BCD_WingChainL.clear();
        BCD_WingChainR.clear();
        JsonObject bcdJson = JsonHelper.getObject(this.modelJson, "builtin_controller_data", null);
        if (bcdJson != null) {
            if (bcdJson.has("tail_chain")) {
                BCD_TailChain = loadChainData(bcdJson.getAsJsonObject("tail_chain"));
            }
            if (bcdJson.has("tail_chain_head")) {
                BCD_TailChainHead = loadChainData(bcdJson.getAsJsonObject("tail_chain_head"));
            }
            if (bcdJson.has("wing_chain_l")) {
                BCD_WingChainL = loadChainData(bcdJson.getAsJsonObject("wing_chain_l"));
            }
            if (bcdJson.has("wing_chain_r")) {
                BCD_WingChainR = loadChainData(bcdJson.getAsJsonObject("wing_chain_r"));
            }
        }
    }

    public void setPlayer(PlayerEntity player, boolean slim) {
        this.entity = player;
        SlimMap.put(player, slim);
    }

    public boolean useSlim(boolean slim) {
        if (SlimOnly) {
            return true;
        }
        if (WideOnly) {
            return false;
        }
        return slim;
    }

    public Identifier getModelResource(boolean slim) {
        return useSlim(slim) ? ModelResource_Slim : ModelResource;
    }

    private Identifier readCacheOrBake(HashMap<FormTextureUtils.ColorSetting, Identifier> Cache, Identifier Resource, Identifier ResourceMask, FormTextureUtils.ColorSetting colorSetting) {
        Identifier CachedTexture = Cache.get(colorSetting);
        if (CachedTexture != null) {
            return CachedTexture;
        }
        CachedTexture = FormTextureUtils.BakeTexture(Resource, ResourceMask, colorSetting, UseMultiplyMask);
        if (CachedTexture == null) {
            CachedTexture = Resource;
        }
        Cache.put(colorSetting, CachedTexture);
        return CachedTexture;
    }

    public Identifier getTextureResource(boolean slim) {
        boolean uslim = useSlim(slim);
        Identifier Resource = uslim ? this.TextureResource_Slim : this.TextureResource;
        Identifier ResourceMask = uslim ? this.TextureMaskResource_Slim : this.TextureMaskResource;
        if (ResourceMask != null) {
            if (FormTextureUtils.useTempTexture && Objects.equals(this.entity, MinecraftClient.getInstance().player)) {
                return FormTextureUtils.tempTextureProcessor.getTexture(this.modelID, uslim ? "texture_slim" : "texture", Resource, ResourceMask, UseMultiplyMask);
            }
            FormTextureUtils.ColorSetting colorSetting = FormTextureUtils.getPlayerColorSetting(this.entity);
            if (colorSetting != null) {
                HashMap<FormTextureUtils.ColorSetting, Identifier> Cache = uslim ? ColorMask_Baked_Textures_Slim : ColorMask_Baked_Textures;
                return readCacheOrBake(Cache, Resource, ResourceMask, colorSetting);
            }
        }
        return Resource;
    }

    public Identifier getOverlayTextureResource(boolean slim) {
        boolean uslim = useSlim(slim);
        Identifier Resource = uslim ? this.OverlayTextureResource_Slim : this.OverlayTextureResource;
        Identifier ResourceMask = uslim ? this.OverlayTextureMaskResource_Slim : this.OverlayTextureMaskResource;
        if (ResourceMask != null) {
            if (FormTextureUtils.useTempTexture && Objects.equals(this.entity, MinecraftClient.getInstance().player)) {
                return FormTextureUtils.tempTextureProcessor.getTexture(this.modelID, uslim ? "overlay_texture_slim" : "overlay_texture", Resource, ResourceMask, UseMultiplyMask);
            }
            FormTextureUtils.ColorSetting colorSetting = FormTextureUtils.getPlayerColorSetting(this.entity);
            if (colorSetting != null) {
                HashMap<FormTextureUtils.ColorSetting, Identifier> Cache = uslim ? ColorMask_Baked_OverlayTexture_Slim : ColorMask_Baked_OverlayTexture;
                return readCacheOrBake(Cache, Resource, ResourceMask, colorSetting);
            }
        }
        return Resource;
    }

    public Identifier getEmissiveTextureResource(boolean slim) {
        boolean uslim = useSlim(slim);
        Identifier Resource = uslim ? this.EmissiveTextureResource_Slim : this.EmissiveTextureResource;
        Identifier ResourceMask = uslim ? this.EmissiveTextureMaskResource_Slim : this.EmissiveTextureMaskResource;
        if (ResourceMask != null) {
            if (FormTextureUtils.useTempTexture && Objects.equals(this.entity, MinecraftClient.getInstance().player)) {
                return FormTextureUtils.tempTextureProcessor.getTexture(this.modelID, uslim ? "emissive_texture_slim" : "emissive_texture", Resource, ResourceMask, UseMultiplyMask);
            }
            FormTextureUtils.ColorSetting colorSetting = FormTextureUtils.getPlayerColorSetting(this.entity);
            if (colorSetting != null && ResourceMask != null) {
                HashMap<FormTextureUtils.ColorSetting, Identifier> Cache = uslim ? ColorMask_Baked_EmissiveTexture_Slim : ColorMask_Baked_EmissiveTexture;
                return readCacheOrBake(Cache, Resource, ResourceMask, colorSetting);
            }
        }
        return Resource;
    }

    public final HashMap<String, GeoBone> geoBoneCache = new HashMap<>();

    public final @Nullable GeoBone getCachedGeoBone(String name) {
        GeoBone bone = geoBoneCache.get(name);
        if (bone == null) {
            Optional<GeoBone> boneOptional = this.getBone(name);
            if (boneOptional.isPresent()) {
                bone = boneOptional.get();
                geoBoneCache.put(name, bone);
            }
        }
        return bone;
    }

    public final void setRotationForTailBones(float limbAngle, float limbDistance, float age, float tailDragAmount, float tailDragAmountVertical) {
        PlayerFormBase curForm = FormTextureUtils.getPlayerForm_Render(entity);
        boolean isFeral = curForm.getBodyType() == PlayerFormBodyType.FERAL;
        float SWAY_RATE = 0.33333334F * 0.5F;
        float SWAY_SCALE = 0.05F;
        if(BCD_TailChain.isEmpty()) {return;}
        for (List<String> tailChain : BCD_TailChain) {
            GeoBone firstTail = this.getCachedGeoBone(tailChain.get(0));
            if (firstTail == null) {
                continue;
            }
            float tailSway = SWAY_SCALE * MathHelper.cos(age * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
            float tailBalance = MathHelper.cos(limbAngle * 0.6662F) * 0.325F * limbDistance;
            if(!isFeral){
                firstTail.setRotY(-MathHelper.lerp(limbDistance, tailSway, tailBalance) - tailDragAmount * 0.75F);
            } else {
                firstTail.setRotZ(MathHelper.lerp(limbDistance, tailSway, tailBalance) + tailDragAmount * 0.75F);
            }
            firstTail.setRotX(-tailDragAmountVertical * 0.75f);
            float offset = 0.0F;
            for(int i = 1; i < tailChain.size(); i++){
                GeoBone chainBone = this.getCachedGeoBone(tailChain.get(i));
                if (chainBone == null) {continue;}
                if(!isFeral){
                    chainBone.setRotY(- MathHelper.lerp(limbDistance, SWAY_SCALE * MathHelper.cos(age * SWAY_RATE - (((float)Math.PI / 3.0F) * offset)), 0.0f) - tailDragAmount * 0.75F);
                } else{
                    chainBone.setRotZ(MathHelper.lerp(limbDistance, SWAY_SCALE * MathHelper.cos(age * SWAY_RATE - (((float)Math.PI / 3.0F) * offset)), 0.0f) + tailDragAmount * 0.75F);
                }
                chainBone.setRotX(-tailDragAmountVertical * 0.75f * (offset + 0.75f));
                offset += 0.75F;
            }
        }
    }

    public final void setRotationForHeadTailBones(float headAngle, float age, float tailDragAmount, float tailDragAmountVertical){
        float SWAY_RATE = 0.33333334F * 0.5F;
        float SWAY_SCALE = 0.05F;
        if (BCD_TailChainHead.isEmpty()) {return;}
        for (List<String> tailChain : BCD_TailChainHead) {
            GeoBone firstHeadTail = this.getCachedGeoBone(tailChain.get(0));
            if (firstHeadTail == null) {
                continue;
            }
            float headTailSway = SWAY_SCALE * MathHelper.cos(age * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
            float headTailBalance = MathHelper.cos(headAngle * 0.6662F) * 0.325F * 0.1f;
            firstHeadTail.setRotY(-MathHelper.lerp(0.1f, headTailSway, headTailBalance) - tailDragAmount * 0.75F);
            firstHeadTail.setRotX(-tailDragAmountVertical * 0.75f);
            float offset = 0.0F;
            for (int i = 1; i < tailChain.size(); i++){
                GeoBone chainBone = this.getCachedGeoBone(tailChain.get(i));
                if (chainBone == null) {continue;}
                chainBone.setRotY(- MathHelper.lerp(0.1f, SWAY_SCALE * MathHelper.cos(age * SWAY_RATE - (((float)Math.PI / 3.0F) * offset)), 0.0f) - tailDragAmount * 0.75F);
                chainBone.setRotX(-tailDragAmountVertical * 0.75f * (offset + 0.75f));
                offset += 0.75F;
            }
        }
    }

    public final void setRotationForWingBones(float limbAngle, float limbDistance, float age, float tailDragAmountVertical){
        float swayAngle = age * 20.0F * (float) (Math.PI / 180.0) + limbAngle;
        float sway_base = MathHelper.cos(swayAngle) * (float) Math.PI * 0.15F + limbDistance;
        float sway_l = (float) -(Math.PI / 4) + sway_base;
        float sway_r = (float) (Math.PI / 4) - sway_base;

        if (BCD_WingChainL != null) {
            for (List<String> wingChain : BCD_WingChainL) {
                GeoBone firstWing = this.getCachedGeoBone(wingChain.get(0));
                if (firstWing == null) { continue; }
                firstWing.setRotY(sway_l);
                firstWing.setRotX(-tailDragAmountVertical * 0.35f);
                float offset = 0.0F;
                for (int i = 1; i < wingChain.size(); i++) {
                    GeoBone chainBone = this.getCachedGeoBone(wingChain.get(i));
                    if (chainBone == null) { continue; }
                    chainBone.setRotX(-tailDragAmountVertical * 0.75f * offset);
                    offset += 0.75F;
                }
            }
        }
        if (BCD_WingChainR != null) {
            for (List<String> wingChain : BCD_WingChainR) {
                GeoBone firstWing = this.getCachedGeoBone(wingChain.get(0));
                if (firstWing == null)  continue;
                firstWing.setRotY(sway_r);
                firstWing.setRotX(-tailDragAmountVertical * 0.35f);
                float offset = 0.0F;
                for (int i = 1; i < wingChain.size(); i++) {
                    GeoBone chainBone = this.getCachedGeoBone(wingChain.get(i));
                    if (chainBone == null) { continue; }
                    chainBone.setRotX(-tailDragAmountVertical * 0.75f * offset);
                    offset += 0.75F;
                }
            }
        }
    }

    public final GeoBone translatePositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        var posOut = new Vec3d(pos.x + b.getPosX(), (float)pos.y + b.getPosY(),(float)pos.z + b.getPosZ());
        return this.setPositionForBone(bone_name, posOut);
    }

    public final GeoBone setPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setPosX((float)pos.x);
        b.setPosY((float)pos.y);
        b.setPosZ((float)pos.z);
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

    public final GeoBone setRotationForBone(String bone_name, Vec3f rot) {
        return setRotationForBone(bone_name, new Vec3d(rot.getX(), rot.getY(), rot.getZ()));
    }

    public final GeoBone setModelPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {
            return null;
        }
        b.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));
        return (GeoBone) b;
    }

    public final GeoBone setModelPositionForBone(String bone_name, Vec3f pos) {
        return setModelPositionForBone(bone_name, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
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

    public final GeoBone setScaleForBone(String bone_name, Vec3f scale) {
        return setScaleForBone(bone_name, new Vec3d(scale.getX(), scale.getY(), scale.getZ()));
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

    public final GeoBone resetBone(String bone_name) {
        setPositionForBone(bone_name, new Vec3d(0,0,0));
        setRotationForBone(bone_name, new Vec3d(0,0,0));
        setModelPositionForBone(bone_name, Vec3d.ZERO);
        return setScaleForBone(bone_name, new Vec3d(1,1,1));
    }

    @Override
    public Identifier getModelResource(FormAnimatable animatable) {
        PlayerEntity player = animatable.e;
        return getModelResource(SlimMap.getOrDefault(player, false));
    }

    @Override
    public Identifier getTextureResource(FormAnimatable animatable) {
        PlayerEntity player = animatable.e;
        return getTextureResource(SlimMap.getOrDefault(player, false));
    }

    public Identifier getFullbrightTextureResource(FormAnimatable animatable) {
        PlayerEntity player = animatable.e;
        return useSlim(SlimMap.getOrDefault(player, false)) ? this.FullBrightTextureResource_Slim : this.FullBrightTextureResource;

    }

    @Override
    public Identifier getAnimationResource(FormAnimatable animatable) {
        return this.Animation;
    }

}
