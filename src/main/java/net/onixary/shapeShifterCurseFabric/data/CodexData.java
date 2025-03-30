package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.networking.ClientEffectAttachmentCache;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import net.minecraft.client.resource.language.I18n;

import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.EFFECT_ATTACHMENT;

public class CodexData {
    // 集中管理Codex的数据

    public static enum ContentType{
        TITLE,
        APPEARANCE,
        PROS,
        CONS,
        INSTINCTS
    }
    // static texts
    // headers
    public static final Text headerStatus = Text.translatable("codex.header.status");
    public static final Text headerAppearance = Text.translatable("codex.header.appearance");
    public static final Text headerPros = Text.translatable("codex.header.pros");
    public static final Text headerCons = Text.translatable("codex.header.cons");
    public static final Text headerInstincts = Text.translatable("codex.header.instincts");
    // status
    private static final Text statusNormal = Text.translatable("codex.status.normal");
    private static final Text statusInfected = Text.translatable("codex.status.infected");
    private static final Text statusBeforeMoon = Text.translatable("codex.status.before_moon");
    private static final Text statusUnderMoon = Text.translatable("codex.status.under_moon");
    // description text before content
    private static final Text descAppearance_normal = Text.translatable("codex.desc.appearance_normal");
    private static final Text descPros_normal = Text.translatable("codex.desc.pros_normal");
    private static final Text descCons_normal = Text.translatable("codex.desc.cons_normal");
    private static final Text descInstincts_normal = Text.translatable("codex.desc.instincts_normal");
    private static final Text descAppearance_0 = Text.translatable("codex.desc.appearance_0");
    private static final Text descPros_0 = Text.translatable("codex.desc.pros_0");
    private static final Text descCons_0 = Text.translatable("codex.desc.cons_0");
    private static final Text descInstincts_0 = Text.translatable("codex.desc.instincts_0");
    private static final Text descAppearance_1 = Text.translatable("codex.desc.appearance_1");
    private static final Text descPros_1 = Text.translatable("codex.desc.pros_1");
    private static final Text descCons_1 = Text.translatable("codex.desc.cons_1");
    private static final Text descInstincts_1 = Text.translatable("codex.desc.instincts_1");
    private static final Text descAppearance_2 = Text.translatable("codex.desc.appearance_2");
    private static final Text descPros_2 = Text.translatable("codex.desc.pros_2");
    private static final Text descCons_2 = Text.translatable("codex.desc.cons_2");
    private static final Text descInstincts_2 = Text.translatable("codex.desc.instincts_2");

    // form related text
    // original
    private static final Text original_title = Text.translatable("codex.form.original.title");
    private static final Text original_appearance = Text.translatable("codex.form.original.appearance");
    private static final Text original_pros = Text.translatable("codex.form.original.pros");
    private static final Text original_cons = Text.translatable("codex.form.original.cons");
    private static final Text original_instincts = Text.translatable("codex.form.original.instincts");
    // form bat
    private static final Text bat_0_title = Text.translatable("codex.form.bat0.title");
    private static final Text bat_0_appearance = Text.translatable("codex.form.bat0.appearance");
    private static final Text bat_0_pros = Text.translatable("codex.form.bat0.pros");
    private static final Text bat_0_cons = Text.translatable("codex.form.bat0.cons");
    private static final Text bat_0_instincts = Text.translatable("codex.form.bat0.instincts");
    private static final Text bat_1_title = Text.translatable("codex.form.bat1.title");
    private static final Text bat_1_appearance = Text.translatable("codex.form.bat1.appearance");
    private static final Text bat_1_pros = Text.translatable("codex.form.bat1.pros");
    private static final Text bat_1_cons = Text.translatable("codex.form.bat1.cons");
    private static final Text bat_1_instincts = Text.translatable("codex.form.bat1.instincts");
    private static final Text bat_2_title = Text.translatable("codex.form.bat2.title");
    private static final Text bat_2_appearance = Text.translatable("codex.form.bat2.appearance");
    private static final Text bat_2_pros = Text.translatable("codex.form.bat2.pros");
    private static final Text bat_2_cons = Text.translatable("codex.form.bat2.cons");
    private static final Text bat_2_instincts = Text.translatable("codex.form.bat2.instincts");

    public static Text getPlayerStatusText(PlayerEntity player){
        // 根据当前角色状态与环境返回对应的状态文本
        String statusTextContent = "";
        boolean hasAnyStatus = false;

        PlayerEffectAttachment currentTransformEffect = (player instanceof ClientPlayerEntity) ?
                ClientEffectAttachmentCache.getAttachment() :
                player.getAttached(EFFECT_ATTACHMENT);

        if(currentTransformEffect != null && currentTransformEffect.currentToForm != null){
            ShapeShifterCurseFabric.LOGGER.info("current effect successfully receive: " + currentTransformEffect.currentEffect);
            statusTextContent += net.minecraft.client.resource.language.I18n.translate(statusInfected.getString());
            hasAnyStatus = true;
        }

        if(CursedMoon.isCursedMoon()){
            if(CursedMoon.isNight()){
                statusTextContent += net.minecraft.client.resource.language.I18n.translate(statusUnderMoon.getString());
                hasAnyStatus = true;
            }
            else{
                statusTextContent += net.minecraft.client.resource.language.I18n.translate(statusBeforeMoon.getString());
                hasAnyStatus = true;
            }
        }

        if(!hasAnyStatus){
            statusTextContent = net.minecraft.client.resource.language.I18n.translate(statusNormal.getString());
        }

        return Text.literal(statusTextContent);
    }

    public static Text getDescText(ContentType type, PlayerEntity player){
        PlayerFormComponent formComp = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormPhase currentPhase = RegFormConfig.CONFIGS.get(formComp.getCurrentForm()).getPhase();
        switch (type){
            case TITLE:
                return Text.empty();
            case APPEARANCE:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descAppearance_normal;
                    case PHASE_0 -> descAppearance_0;
                    case PHASE_1 -> descAppearance_1;
                    case PHASE_2 -> descAppearance_2;
                };
            case PROS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descPros_normal;
                    case PHASE_0 -> descPros_0;
                    case PHASE_1 -> descPros_1;
                    case PHASE_2 -> descPros_2;
                };
            case CONS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descCons_normal;
                    case PHASE_0 -> descCons_0;
                    case PHASE_1 -> descCons_1;
                    case PHASE_2 -> descCons_2;
                };
            case INSTINCTS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descInstincts_normal;
                    case PHASE_0 -> descInstincts_0;
                    case PHASE_1 -> descInstincts_1;
                    case PHASE_2 -> descInstincts_2;
                };
        }
        return Text.empty();
    }

    public static Text getContentText(ContentType type, PlayerEntity player){
        PlayerFormComponent formComp = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerForms currentForm = formComp.getCurrentForm();
        switch (currentForm){
            case ORIGINAL_SHIFTER:
                return switch (type) {
                    case TITLE -> original_title;
                    case APPEARANCE -> original_appearance;
                    case PROS -> original_pros;
                    case CONS -> original_cons;
                    case INSTINCTS -> original_instincts;
                };
            case BAT_0:
                return switch (type) {
                    case TITLE -> bat_0_title;
                    case APPEARANCE -> bat_0_appearance;
                    case PROS -> bat_0_pros;
                    case CONS -> bat_0_cons;
                    case INSTINCTS -> bat_0_instincts;
                };
            case BAT_1:
                return switch (type) {
                    case TITLE -> bat_1_title;
                    case APPEARANCE -> bat_1_appearance;
                    case PROS -> bat_1_pros;
                    case CONS -> bat_1_cons;
                    case INSTINCTS -> bat_1_instincts;
                };
            case BAT_2:
                return switch (type){
                    case TITLE -> bat_2_title;
                    case APPEARANCE -> bat_2_appearance;
                    case PROS -> bat_2_pros;
                    case CONS -> bat_2_cons;
                    case INSTINCTS -> bat_2_instincts;
                };
        }
        return Text.empty();
    }
}
