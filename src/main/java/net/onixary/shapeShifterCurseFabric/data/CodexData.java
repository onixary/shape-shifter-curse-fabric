package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.networking.ClientEffectAttachmentCache;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

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
    private static final Text descAppearance_3 = Text.translatable("codex.desc.appearance_3");
    private static final Text descPros_3 = Text.translatable("codex.desc.pros_3");
    private static final Text descCons_3 = Text.translatable("codex.desc.cons_3");
    private static final Text descInstincts_3 = Text.translatable("codex.desc.instincts_3");

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
    // form axolotl
    private static final Text axolotl_0_title = Text.translatable("codex.form.axolotl0.title");
    private static final Text axolotl_0_appearance = Text.translatable("codex.form.axolotl0.appearance");
    private static final Text axolotl_0_pros = Text.translatable("codex.form.axolotl0.pros");
    private static final Text axolotl_0_cons = Text.translatable("codex.form.axolotl0.cons");
    private static final Text axolotl_0_instincts = Text.translatable("codex.form.axolotl0.instincts");
    private static final Text axolotl_1_title = Text.translatable("codex.form.axolotl1.title");
    private static final Text axolotl_1_appearance = Text.translatable("codex.form.axolotl1.appearance");
    private static final Text axolotl_1_pros = Text.translatable("codex.form.axolotl1.pros");
    private static final Text axolotl_1_cons = Text.translatable("codex.form.axolotl1.cons");
    private static final Text axolotl_1_instincts = Text.translatable("codex.form.axolotl1.instincts");
    private static final Text axolotl_2_title = Text.translatable("codex.form.axolotl2.title");
    private static final Text axolotl_2_appearance = Text.translatable("codex.form.axolotl2.appearance");
    private static final Text axolotl_2_pros = Text.translatable("codex.form.axolotl2.pros");
    private static final Text axolotl_2_cons = Text.translatable("codex.form.axolotl2.cons");
    private static final Text axolotl_2_instincts = Text.translatable("codex.form.axolotl2.instincts");
    // form ocelot
    private static final Text ocelot_0_title = Text.translatable("codex.form.ocelot0.title");
    private static final Text ocelot_0_appearance = Text.translatable("codex.form.ocelot0.appearance");
    private static final Text ocelot_0_pros = Text.translatable("codex.form.ocelot0.pros");
    private static final Text ocelot_0_cons = Text.translatable("codex.form.ocelot0.cons");
    private static final Text ocelot_0_instincts = Text.translatable("codex.form.ocelot0.instincts");
    private static final Text ocelot_1_title = Text.translatable("codex.form.ocelot1.title");
    private static final Text ocelot_1_appearance = Text.translatable("codex.form.ocelot1.appearance");
    private static final Text ocelot_1_pros = Text.translatable("codex.form.ocelot1.pros");
    private static final Text ocelot_1_cons = Text.translatable("codex.form.ocelot1.cons");
    private static final Text ocelot_1_instincts = Text.translatable("codex.form.ocelot1.instincts");
    private static final Text ocelot_2_title = Text.translatable("codex.form.ocelot2.title");
    private static final Text ocelot_2_appearance = Text.translatable("codex.form.ocelot2.appearance");
    private static final Text ocelot_2_pros = Text.translatable("codex.form.ocelot2.pros");
    private static final Text ocelot_2_cons = Text.translatable("codex.form.ocelot2.cons");
    private static final Text ocelot_2_instincts = Text.translatable("codex.form.ocelot2.instincts");
    // form familiar fox
    private static final Text familiar_fox_0_title = Text.translatable("codex.form.familiar_fox0.title");
    private static final Text familiar_fox_0_appearance = Text.translatable("codex.form.familiar_fox0.appearance");
    private static final Text familiar_fox_0_pros = Text.translatable("codex.form.familiar_fox0.pros");
    private static final Text familiar_fox_0_cons = Text.translatable("codex.form.familiar_fox0.cons");
    private static final Text familiar_fox_0_instincts = Text.translatable("codex.form.familiar_fox0.instincts");
    private static final Text familiar_fox_1_title = Text.translatable("codex.form.familiar_fox1.title");
    private static final Text familiar_fox_1_appearance = Text.translatable("codex.form.familiar_fox1.appearance");
    private static final Text familiar_fox_1_pros = Text.translatable("codex.form.familiar_fox1.pros");
    private static final Text familiar_fox_1_cons = Text.translatable("codex.form.familiar_fox1.cons");
    private static final Text familiar_fox_1_instincts = Text.translatable("codex.form.familiar_fox1.instincts");
    private static final Text familiar_fox_2_title = Text.translatable("codex.form.familiar_fox2.title");
    private static final Text familiar_fox_2_appearance = Text.translatable("codex.form.familiar_fox2.appearance");
    private static final Text familiar_fox_2_pros = Text.translatable("codex.form.familiar_fox2.pros");
    private static final Text familiar_fox_2_cons = Text.translatable("codex.form.familiar_fox2.cons");
    private static final Text familiar_fox_2_instincts = Text.translatable("codex.form.familiar_fox2.instincts");
    private static final Text familiar_fox_3_title = Text.translatable("codex.form.familiar_fox3.title");
    private static final Text familiar_fox_3_appearance = Text.translatable("codex.form.familiar_fox3.appearance");
    private static final Text familiar_fox_3_pros = Text.translatable("codex.form.familiar_fox3.pros");
    private static final Text familiar_fox_3_cons = Text.translatable("codex.form.familiar_fox3.cons");
    private static final Text familiar_fox_3_instincts = Text.translatable("codex.form.familiar_fox3.instincts");
    // sp form alley
    private static final Text allay_sp_title = Text.translatable("codex.form.allay_sp.title");
    private static final Text allay_sp_appearance = Text.translatable("codex.form.allay_sp.appearance");
    private static final Text allay_sp_pros = Text.translatable("codex.form.allay_sp.pros");
    private static final Text allay_sp_cons = Text.translatable("codex.form.allay_sp.cons");
    private static final Text allay_sp_instincts = Text.translatable("codex.form.allay_sp.instincts");
    // sp form feral cat
    private static final Text feral_cat_sp_title = Text.translatable("codex.form.feral_cat_sp.title");
    private static final Text feral_cat_sp_appearance = Text.translatable("codex.form.feral_cat_sp.appearance");
    private static final Text feral_cat_sp_pros = Text.translatable("codex.form.feral_cat_sp.pros");
    private static final Text feral_cat_sp_cons = Text.translatable("codex.form.feral_cat_sp.cons");
    private static final Text feral_cat_sp_instincts = Text.translatable("codex.form.feral_cat_sp.instincts");
    // custom empty forms
    private static final Text alpha_0_title = Text.translatable("codex.form.alpha0.title");
    private static final Text alpha_0_appearance = Text.translatable("codex.form.alpha0.appearance");
    private static final Text alpha_0_pros = Text.translatable("codex.form.alpha0.pros");
    private static final Text alpha_0_cons = Text.translatable("codex.form.alpha0.cons");
    private static final Text alpha_0_instincts = Text.translatable("codex.form.alpha0.instincts");
    private static final Text alpha_1_title = Text.translatable("codex.form.alpha1.title");
    private static final Text alpha_1_appearance = Text.translatable("codex.form.alpha1.appearance");
    private static final Text alpha_1_pros = Text.translatable("codex.form.alpha1.pros");
    private static final Text alpha_1_cons = Text.translatable("codex.form.alpha1.cons");
    private static final Text alpha_1_instincts = Text.translatable("codex.form.alpha1.instincts");
    private static final Text alpha_2_title = Text.translatable("codex.form.alpha2.title");
    private static final Text alpha_2_appearance = Text.translatable("codex.form.alpha2.appearance");
    private static final Text alpha_2_pros = Text.translatable("codex.form.alpha2.pros");
    private static final Text alpha_2_cons = Text.translatable("codex.form.alpha2.cons");
    private static final Text alpha_2_instincts = Text.translatable("codex.form.alpha2.instincts");
    private static final Text beta_0_title = Text.translatable("codex.form.beta0.title");
    private static final Text beta_0_appearance = Text.translatable("codex.form.beta0.appearance");
    private static final Text beta_0_pros = Text.translatable("codex.form.beta0.pros");
    private static final Text beta_0_cons = Text.translatable("codex.form.beta0.cons");
    private static final Text beta_0_instincts = Text.translatable("codex.form.beta0.instincts");
    private static final Text beta_1_title = Text.translatable("codex.form.beta1.title");
    private static final Text beta_1_appearance = Text.translatable("codex.form.beta1.appearance");
    private static final Text beta_1_pros = Text.translatable("codex.form.beta1.pros");
    private static final Text beta_1_cons = Text.translatable("codex.form.beta1.cons");
    private static final Text beta_1_instincts = Text.translatable("codex.form.beta1.instincts");
    private static final Text beta_2_title = Text.translatable("codex.form.beta2.title");
    private static final Text beta_2_appearance = Text.translatable("codex.form.beta2.appearance");
    private static final Text beta_2_pros = Text.translatable("codex.form.beta2.pros");
    private static final Text beta_2_cons = Text.translatable("codex.form.beta2.cons");
    private static final Text beta_2_instincts = Text.translatable("codex.form.beta2.instincts");
    private static final Text gamma_0_title = Text.translatable("codex.form.gamma0.title");
    private static final Text gamma_0_appearance = Text.translatable("codex.form.gamma0.appearance");
    private static final Text gamma_0_pros = Text.translatable("codex.form.gamma0.pros");
    private static final Text gamma_0_cons = Text.translatable("codex.form.gamma0.cons");
    private static final Text gamma_0_instincts = Text.translatable("codex.form.gamma0.instincts");
    private static final Text gamma_1_title = Text.translatable("codex.form.gamma1.title");
    private static final Text gamma_1_appearance = Text.translatable("codex.form.gamma1.appearance");
    private static final Text gamma_1_pros = Text.translatable("codex.form.gamma1.pros");
    private static final Text gamma_1_cons = Text.translatable("codex.form.gamma1.cons");
    private static final Text gamma_1_instincts = Text.translatable("codex.form.gamma1.instincts");
    private static final Text gamma_2_title = Text.translatable("codex.form.gamma2.title");
    private static final Text gamma_2_appearance = Text.translatable("codex.form.gamma2.appearance");
    private static final Text gamma_2_pros = Text.translatable("codex.form.gamma2.pros");
    private static final Text gamma_2_cons = Text.translatable("codex.form.gamma2.cons");
    private static final Text gamma_2_instincts = Text.translatable("codex.form.gamma2.instincts");
    private static final Text omega_sp_title = Text.translatable("codex.form.omega_sp.title");
    private static final Text omega_sp_appearance = Text.translatable("codex.form.omega_sp.appearance");
    private static final Text omega_sp_pros = Text.translatable("codex.form.omega_sp.pros");
    private static final Text omega_sp_cons = Text.translatable("codex.form.omega_sp.cons");
    private static final Text omega_sp_instincts = Text.translatable("codex.form.omega_sp.instincts");
    private static final Text psi_sp_title = Text.translatable("codex.form.psi_sp.title");
    private static final Text psi_sp_appearance = Text.translatable("codex.form.psi_sp.appearance");
    private static final Text psi_sp_pros = Text.translatable("codex.form.psi_sp.pros");
    private static final Text psi_sp_cons = Text.translatable("codex.form.psi_sp.cons");
    private static final Text psi_sp_instincts = Text.translatable("codex.form.psi_sp.instincts");
    private static final Text chi_sp_title = Text.translatable("codex.form.chi_sp.title");
    private static final Text chi_sp_appearance = Text.translatable("codex.form.chi_sp.appearance");
    private static final Text chi_sp_pros = Text.translatable("codex.form.chi_sp.pros");
    private static final Text chi_sp_cons = Text.translatable("codex.form.chi_sp.cons");
    private static final Text chi_sp_instincts = Text.translatable("codex.form.chi_sp.instincts");
    private static final Text phi_sp_title = Text.translatable("codex.form.phi_sp.title");
    private static final Text phi_sp_appearance = Text.translatable("codex.form.phi_sp.appearance");
    private static final Text phi_sp_pros = Text.translatable("codex.form.phi_sp.pros");
    private static final Text phi_sp_cons = Text.translatable("codex.form.phi_sp.cons");
    private static final Text phi_sp_instincts = Text.translatable("codex.form.phi_sp.instincts");



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
                    case PHASE_3 -> descAppearance_3;
                    case PHASE_SP -> Text.empty();
                };
            case PROS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descPros_normal;
                    case PHASE_0 -> descPros_0;
                    case PHASE_1 -> descPros_1;
                    case PHASE_2 -> descPros_2;
                    case PHASE_3 -> descPros_3;
                    case PHASE_SP -> Text.empty();
                };
            case CONS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descCons_normal;
                    case PHASE_0 -> descCons_0;
                    case PHASE_1 -> descCons_1;
                    case PHASE_2 -> descCons_2;
                    case PHASE_3 -> descCons_3;
                    case PHASE_SP -> Text.empty();
                };
            case INSTINCTS:
                return switch (currentPhase) {
                    case PHASE_CLEAR -> descInstincts_normal;
                    case PHASE_0 -> descInstincts_0;
                    case PHASE_1 -> descInstincts_1;
                    case PHASE_2 -> descInstincts_2;
                    case PHASE_3 -> descInstincts_3;
                    case PHASE_SP -> Text.empty();
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
            case AXOLOTL_0:
                return switch (type) {
                    case TITLE -> axolotl_0_title;
                    case APPEARANCE -> axolotl_0_appearance;
                    case PROS -> axolotl_0_pros;
                    case CONS -> axolotl_0_cons;
                    case INSTINCTS -> axolotl_0_instincts;
                };
            case AXOLOTL_1:
                return switch (type) {
                    case TITLE -> axolotl_1_title;
                    case APPEARANCE -> axolotl_1_appearance;
                    case PROS -> axolotl_1_pros;
                    case CONS -> axolotl_1_cons;
                    case INSTINCTS -> axolotl_1_instincts;
                };
            case AXOLOTL_2:
                return switch (type) {
                    case TITLE -> axolotl_2_title;
                    case APPEARANCE -> axolotl_2_appearance;
                    case PROS -> axolotl_2_pros;
                    case CONS -> axolotl_2_cons;
                    case INSTINCTS -> axolotl_2_instincts;
                };
            case OCELOT_0:
                return switch (type) {
                    case TITLE -> ocelot_0_title;
                    case APPEARANCE -> ocelot_0_appearance;
                    case PROS -> ocelot_0_pros;
                    case CONS -> ocelot_0_cons;
                    case INSTINCTS -> ocelot_0_instincts;
                };
            case OCELOT_1:
                return switch (type) {
                    case TITLE -> ocelot_1_title;
                    case APPEARANCE -> ocelot_1_appearance;
                    case PROS -> ocelot_1_pros;
                    case CONS -> ocelot_1_cons;
                    case INSTINCTS -> ocelot_1_instincts;
                };
            case OCELOT_2:
                return switch (type) {
                    case TITLE -> ocelot_2_title;
                    case APPEARANCE -> ocelot_2_appearance;
                    case PROS -> ocelot_2_pros;
                    case CONS -> ocelot_2_cons;
                    case INSTINCTS -> ocelot_2_instincts;
                };
            case FAMILIAR_FOX_0:
                return switch (type) {
                    case TITLE -> familiar_fox_0_title;
                    case APPEARANCE -> familiar_fox_0_appearance;
                    case PROS -> familiar_fox_0_pros;
                    case CONS -> familiar_fox_0_cons;
                    case INSTINCTS -> familiar_fox_0_instincts;
                };
            case FAMILIAR_FOX_1:
                return switch (type) {
                    case TITLE -> familiar_fox_1_title;
                    case APPEARANCE -> familiar_fox_1_appearance;
                    case PROS -> familiar_fox_1_pros;
                    case CONS -> familiar_fox_1_cons;
                    case INSTINCTS -> familiar_fox_1_instincts;
                };
            case FAMILIAR_FOX_2:
                return switch (type) {
                    case TITLE -> familiar_fox_2_title;
                    case APPEARANCE -> familiar_fox_2_appearance;
                    case PROS -> familiar_fox_2_pros;
                    case CONS -> familiar_fox_2_cons;
                    case INSTINCTS -> familiar_fox_2_instincts;
                };
            case FAMILIAR_FOX_3:
                return switch (type) {
                    case TITLE -> familiar_fox_3_title;
                    case APPEARANCE -> familiar_fox_3_appearance;
                    case PROS -> familiar_fox_3_pros;
                    case CONS -> familiar_fox_3_cons;
                    case INSTINCTS -> familiar_fox_3_instincts;
                };

            case ALLAY_SP:
                return switch (type) {
                    case TITLE -> allay_sp_title;
                    case APPEARANCE -> allay_sp_appearance;
                    case PROS -> allay_sp_pros;
                    case CONS -> allay_sp_cons;
                    case INSTINCTS -> allay_sp_instincts;
                };
            case FERAL_CAT_SP:
                return switch (type) {
                    case TITLE -> feral_cat_sp_title;
                    case APPEARANCE -> feral_cat_sp_appearance;
                    case PROS -> feral_cat_sp_pros;
                    case CONS -> feral_cat_sp_cons;
                    case INSTINCTS -> feral_cat_sp_instincts;
                };

                // custom empty forms
            case ALPHA_0:
                return switch (type) {
                    case TITLE -> alpha_0_title;
                    case APPEARANCE -> alpha_0_appearance;
                    case PROS -> alpha_0_pros;
                    case CONS -> alpha_0_cons;
                    case INSTINCTS -> alpha_0_instincts;
                };
            case ALPHA_1:
                return switch (type) {
                    case TITLE -> alpha_1_title;
                    case APPEARANCE -> alpha_1_appearance;
                    case PROS -> alpha_1_pros;
                    case CONS -> alpha_1_cons;
                    case INSTINCTS -> alpha_1_instincts;
                };
            case ALPHA_2:
                return switch (type) {
                    case TITLE -> alpha_2_title;
                    case APPEARANCE -> alpha_2_appearance;
                    case PROS -> alpha_2_pros;
                    case CONS -> alpha_2_cons;
                    case INSTINCTS -> alpha_2_instincts;
                };
            case BETA_0:
                return switch (type) {
                    case TITLE -> beta_0_title;
                    case APPEARANCE -> beta_0_appearance;
                    case PROS -> beta_0_pros;
                    case CONS -> beta_0_cons;
                    case INSTINCTS -> beta_0_instincts;
                };
            case BETA_1:
                return switch (type) {
                    case TITLE -> beta_1_title;
                    case APPEARANCE -> beta_1_appearance;
                    case PROS -> beta_1_pros;
                    case CONS -> beta_1_cons;
                    case INSTINCTS -> beta_1_instincts;
                };
            case BETA_2:
                return switch (type) {
                    case TITLE -> beta_2_title;
                    case APPEARANCE -> beta_2_appearance;
                    case PROS -> beta_2_pros;
                    case CONS -> beta_2_cons;
                    case INSTINCTS -> beta_2_instincts;
                };
            case GAMMA_0:
                return switch (type) {
                    case TITLE -> gamma_0_title;
                    case APPEARANCE -> gamma_0_appearance;
                    case PROS -> gamma_0_pros;
                    case CONS -> gamma_0_cons;
                    case INSTINCTS -> gamma_0_instincts;
                };
            case GAMMA_1:
                return switch (type) {
                    case TITLE -> gamma_1_title;
                    case APPEARANCE -> gamma_1_appearance;
                    case PROS -> gamma_1_pros;
                    case CONS -> gamma_1_cons;
                    case INSTINCTS -> gamma_1_instincts;
                };
            case GAMMA_2:
                return switch (type) {
                    case TITLE -> gamma_2_title;
                    case APPEARANCE -> gamma_2_appearance;
                    case PROS -> gamma_2_pros;
                    case CONS -> gamma_2_cons;
                    case INSTINCTS -> gamma_2_instincts;
                };
            case OMEGA_SP:
                return switch (type) {
                    case TITLE -> omega_sp_title;
                    case APPEARANCE -> omega_sp_appearance;
                    case PROS -> omega_sp_pros;
                    case CONS -> omega_sp_cons;
                    case INSTINCTS -> omega_sp_instincts;
                };
            case PSI_SP:
                return switch (type) {
                    case TITLE -> psi_sp_title;
                    case APPEARANCE -> psi_sp_appearance;
                    case PROS -> psi_sp_pros;
                    case CONS -> psi_sp_cons;
                    case INSTINCTS -> psi_sp_instincts;
                };
            case CHI_SP:
                return switch (type) {
                    case TITLE -> chi_sp_title;
                    case APPEARANCE -> chi_sp_appearance;
                    case PROS -> chi_sp_pros;
                    case CONS -> chi_sp_cons;
                    case INSTINCTS -> chi_sp_instincts;
                };
            case PHI_SP:
                return switch (type) {
                    case TITLE -> phi_sp_title;
                    case APPEARANCE -> phi_sp_appearance;
                    case PROS -> phi_sp_pros;
                    case CONS -> phi_sp_cons;
                    case INSTINCTS -> phi_sp_instincts;
                };
        }
        return Text.empty();
    }
}
