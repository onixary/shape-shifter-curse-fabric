package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private boolean IsRegisteredPowerAnim = false;

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


    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        return null;
    }

    // 注册PowerAnim
    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        this.IsRegisteredPowerAnim = true;
    }

    // 是否注册了PowerAnim
    public boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return IsRegisteredPowerAnim;
    }

    // 获取PowerAnim 输出左为是否匹配(不匹配使用由PowerAnim注册表提供的默认动画) 右为动画
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        return new Pair<>(false, null);
    }

    // 1代2代动画控制器使用 等3代测试完就删除
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

    public String name() {
        return FormID.toString();
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
