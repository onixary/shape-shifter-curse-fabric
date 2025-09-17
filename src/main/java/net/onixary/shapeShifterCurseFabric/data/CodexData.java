package net.onixary.shapeShifterCurseFabric.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.networking.ClientEffectAttachmentCache;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
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
    private static final Text bat_3_title = Text.translatable("codex.form.bat3.title");
    private static final Text bat_3_appearance = Text.translatable("codex.form.bat3.appearance");
    private static final Text bat_3_pros = Text.translatable("codex.form.bat3.pros");
    private static final Text bat_3_cons = Text.translatable("codex.form.bat3.cons");
    private static final Text bat_3_instincts = Text.translatable("codex.form.bat3.instincts");

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
    private static final Text axolotl_3_title = Text.translatable("codex.form.axolotl3.title");
    private static final Text axolotl_3_appearance = Text.translatable("codex.form.axolotl3.appearance");
    private static final Text axolotl_3_pros = Text.translatable("codex.form.axolotl3.pros");
    private static final Text axolotl_3_cons = Text.translatable("codex.form.axolotl3.cons");
    private static final Text axolotl_3_instincts = Text.translatable("codex.form.axolotl3.instincts");
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
    private static final Text ocelot_3_title = Text.translatable("codex.form.ocelot3.title");
    private static final Text ocelot_3_appearance = Text.translatable("codex.form.ocelot3.appearance");
    private static final Text ocelot_3_pros = Text.translatable("codex.form.ocelot3.pros");
    private static final Text ocelot_3_cons = Text.translatable("codex.form.ocelot3.cons");
    private static final Text ocelot_3_instincts = Text.translatable("codex.form.ocelot3.instincts");
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
    // form snow fox
    private static final Text snow_fox_0_title = Text.translatable("codex.form.snow_fox0.title");
    private static final Text snow_fox_0_appearance = Text.translatable("codex.form.snow_fox0.appearance");
    private static final Text snow_fox_0_pros = Text.translatable("codex.form.snow_fox0.pros");
    private static final Text snow_fox_0_cons = Text.translatable("codex.form.snow_fox0.cons");
    private static final Text snow_fox_0_instincts = Text.translatable("codex.form.snow_fox0.instincts");
    private static final Text snow_fox_1_title = Text.translatable("codex.form.snow_fox1.title");
    private static final Text snow_fox_1_appearance = Text.translatable("codex.form.snow_fox1.appearance");
    private static final Text snow_fox_1_pros = Text.translatable("codex.form.snow_fox1.pros");
    private static final Text snow_fox_1_cons = Text.translatable("codex.form.snow_fox1.cons");
    private static final Text snow_fox_1_instincts = Text.translatable("codex.form.snow_fox1.instincts");
    private static final Text snow_fox_2_title = Text.translatable("codex.form.snow_fox2.title");
    private static final Text snow_fox_2_appearance = Text.translatable("codex.form.snow_fox2.appearance");
    private static final Text snow_fox_2_pros = Text.translatable("codex.form.snow_fox2.pros");
    private static final Text snow_fox_2_cons = Text.translatable("codex.form.snow_fox2.cons");
    private static final Text snow_fox_2_instincts = Text.translatable("codex.form.snow_fox2.instincts");
    private static final Text snow_fox_3_title = Text.translatable("codex.form.snow_fox3.title");
    private static final Text snow_fox_3_appearance = Text.translatable("codex.form.snow_fox3.appearance");
    private static final Text snow_fox_3_pros = Text.translatable("codex.form.snow_fox3.pros");
    private static final Text snow_fox_3_cons = Text.translatable("codex.form.snow_fox3.cons");
    private static final Text snow_fox_3_instincts = Text.translatable("codex.form.snow_fox3.instincts");
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
        StringBuilder statusTextBuilder = new StringBuilder();
        boolean hasAnyStatus = false;

        PlayerEffectAttachment currentTransformEffect;

        // 使用环境检测而不是玩家类型检测
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            currentTransformEffect = ClientEffectAttachmentCache.getAttachment();
        } else {
            currentTransformEffect = player.getAttached(EFFECT_ATTACHMENT);
        }

        if(currentTransformEffect != null && currentTransformEffect.currentToForm != null){
            ShapeShifterCurseFabric.LOGGER.info("current effect successfully receive: " + currentTransformEffect.currentEffect);
            statusTextBuilder.append(statusInfected.getString());
            hasAnyStatus = true;
        }

        // 使用环境检测来正确获取CursedMoon状态
        boolean isCursedMoon, isNight;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            // 客户端使用同步的状态
            isCursedMoon = CursedMoon.clientIsCursedMoon;
            isNight = CursedMoon.clientIsNight;
        } else {
            // 服务端使用原始逻辑
            isCursedMoon = CursedMoon.isCursedMoon(player.getWorld());
            isNight = CursedMoon.isNight();
        }

        if(isCursedMoon){
            if(isNight){
                statusTextBuilder.append(statusUnderMoon.getString());
                hasAnyStatus = true;
            }
            else{
                statusTextBuilder.append(statusBeforeMoon.getString());
                hasAnyStatus = true;
            }
        }

        if(!hasAnyStatus){
            statusTextBuilder.append(statusNormal.getString());
        }

        return Text.literal(statusTextBuilder.toString());
    }

    public static Text getDescText(ContentType type, PlayerEntity player){
        PlayerFormComponent formComp = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormPhase currentPhase = formComp.getCurrentForm().getPhase();
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
        return formComp.getCurrentForm().getContentText(type);
    }
}
