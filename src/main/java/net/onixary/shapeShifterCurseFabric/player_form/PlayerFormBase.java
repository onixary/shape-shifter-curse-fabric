package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
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

    public PlayerFormBase(Identifier formID) {
        FormID = formID;
    }

    // 从 FormConfig 迁移
    public PlayerFormBodyType getBodyType() {
        return BodyType;
    };
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
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + "." + type.toString());
    }

    // 动画
    public void Anim_registerAnims() {
        return;
    }

    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        return null;
    }

    public boolean GetHasSlowFall() {
        return HasSlowFall;
    }

    public PlayerFormBase SetHasSlowFall(boolean hasSlowFall) {
        HasSlowFall = hasSlowFall;
        return this;
    }

    public boolean GetOverrideHandAnim() {
        return OverrideHandAnim;
    }

    public PlayerFormBase SetOverrideHandAnim(boolean overrideHandAnim) {
        OverrideHandAnim = overrideHandAnim;
        return this;
    }

    public boolean GetCanSneakRush() {
        return CanSneakRush;
    }

    public PlayerFormBase SetCanSneakRush(boolean canSneakRush) {
        CanSneakRush = canSneakRush;
        return this;
    }

    public boolean GetCanRushJump() {
        return CanRushJump;
    }

    public PlayerFormBase SetCanRushJump(boolean canRushJump) {
        CanRushJump = canRushJump;
        return this;
    }

    public boolean GetIsCustomForm() {
        return IsCustomForm;
    }

    public PlayerFormBase SetIsCustomForm(boolean isCustomForm) {
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

    public String name() {
        return FormID.toString();
    }

    public String getFormOriginID() {
        return "form_" + FormID.getPath();
    }
}
