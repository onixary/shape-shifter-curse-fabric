package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

public class PlayerFormBase {
    public Identifier FormID;
    public PlayerFormGroup Group = null;
    public int FormIndex = 0;

    private PlayerFormPhase Phase = PlayerFormPhase.PHASE_CLEAR;
    private PlayerFormBodyType BodyType = PlayerFormBodyType.NORMAL;

    public PlayerFormBase(Identifier formID) {
        FormID = formID;
    }

    // 从 FormConfig 迁移
    public PlayerFormBodyType GetBodyType() {
        return BodyType;
    };
    public PlayerFormBase SetBodyType(PlayerFormBodyType bodyType) {
        BodyType = bodyType;
        return this;
    }

    public PlayerFormPhase GetPhase() {
        return Phase;
    }
    public PlayerFormBase SetPhase(PlayerFormPhase phase) {
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

    public boolean Anim_HasSlowFall() {
        return false;
    }

    public boolean Anim_OverrideHandAnim() {
        return false;
    }

    public boolean Anim_CanSneakRush() {
        return false;
    }

    public void SetGroup(PlayerFormGroup group, int formIndex) {
        if (Group != null) {
            throw new IllegalArgumentException("Group already set");
        }
        Group = group;
        FormIndex = formIndex;
    }
}
