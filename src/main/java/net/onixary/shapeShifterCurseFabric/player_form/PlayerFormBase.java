package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

public class PlayerFormBase {
    public Identifier FormID;
    public PlayerFormGroup Group = null;
    public int FormIndex = 0;

    private PlayerFormPhase Phase = PlayerFormPhase.PHASE_CLEAR;
    private PlayerFormBodyType BodyType = PlayerFormBodyType.NORMAL;
    private boolean HasSlowFall = false;
    private boolean OverrideHandAnim = false;
    private boolean CanSneakRush = false;
    private boolean CanRushJump = false;
    private boolean IsCustomForm = false;

    public String Origin_NameSpace_OverWrite = null;
    public Identifier OriginLayer_OverWrite = null; // Default: "origins:origin"

    public PlayerFormBase(Identifier formID) {
        FormID = formID;
    }

    // 从 FormConfig 迁移
    public PlayerFormBodyType getBodyType() {
        return BodyType;
    }

    public PlayerFormBase setBodyType(PlayerFormBodyType bodyType) {
        BodyType = bodyType;
        return this;
    }

    public PlayerFormPhase getPhase() {
        return Phase;
    }
    public PlayerFormBase setPhase(PlayerFormPhase phase) {
        Phase = phase;
        return this;
    }

    // 暂时在PlayerForm实现文本
    public Text getContentText(CodexData.ContentType type) {
        // Lang 格式 codex.form.<ModID>.<FormId>.<type>
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + "." + type.toString().toLowerCase());
    }

    // 动画
    public void Anim_registerAnims() {
        return;
    }

    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        return null;
    }

    public boolean getHasSlowFall() {
        return HasSlowFall;
    }

    public PlayerFormBase setHasSlowFall(boolean hasSlowFall) {
        HasSlowFall = hasSlowFall;
        return this;
    }

    public boolean getOverrideHandAnim() {
        return OverrideHandAnim;
    }

    public PlayerFormBase setOverrideHandAnim(boolean overrideHandAnim) {
        OverrideHandAnim = overrideHandAnim;
        return this;
    }

    public boolean getCanSneakRush() {
        return CanSneakRush;
    }

    public PlayerFormBase setCanSneakRush(boolean canSneakRush) {
        CanSneakRush = canSneakRush;
        return this;
    }

    public boolean getCanRushJump() {
        return CanRushJump;
    }

    public PlayerFormBase setCanRushJump(boolean canRushJump) {
        CanRushJump = canRushJump;
        return this;
    }

    public boolean getIsCustomForm() {
        return IsCustomForm;
    }

    public PlayerFormBase setIsCustomForm(boolean isCustomForm) {
        IsCustomForm = isCustomForm;
        return this;
    }

    public PlayerFormGroup getGroup() {
        return Group;
    }

    public int getIndex() {
        return FormIndex;
    }

    public void setGroup(PlayerFormGroup group, int formIndex) {
        if (Group != null) {
            throw new IllegalArgumentException("Group already set");
        }
        Group = group;
        FormIndex = formIndex;
    }

    public Vec3d getCapeIdleLoc(AbstractClientPlayerEntity player) {
        if (getBodyType() == PlayerFormBodyType.FERAL) {
            return new Vec3d(0.0f, -0.2f, 0.3f);
        }
        else {
            return new Vec3d(0.0, 0.0, 0.125);
        }
    }

    public float getCapeBaseRotateAngle(AbstractClientPlayerEntity player) {
        if (getBodyType() == PlayerFormBodyType.FERAL) {
            return 90.0f;
        }
        else {
            return 0.0f;
        }
    }

    public boolean NeedModifyXRotationAngle() {
        return getBodyType() == PlayerFormBodyType.FERAL;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerFormBase) {
            return ((PlayerFormBase)o).FormID.equals(FormID);
        }
        return false;
    }

    public String getIDString() {
        return FormID.toString();
    }

    public Text getFormName() {
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + ".name");
    }

    public PlayerFormBase setOriginNameSpaceOverWrite(String nameSpace) {
        Origin_NameSpace_OverWrite = nameSpace;
        return this;
    }

    public PlayerFormBase setOriginLayerOverWrite(Identifier OriginLayerID) {
        OriginLayer_OverWrite = OriginLayerID;
        return this;
    }

    public Identifier getFormOriginID() {
        String NameSpace = Origin_NameSpace_OverWrite != null ? Origin_NameSpace_OverWrite : FormID.getNamespace();
        return new Identifier(NameSpace, "form_" + FormID.getPath());
    }

    public Identifier getFormOriginLayerID() {
        return OriginLayer_OverWrite != null ? OriginLayer_OverWrite : new Identifier(Origins.MODID, "origin");
    }
}
