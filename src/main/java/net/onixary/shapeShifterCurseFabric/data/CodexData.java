package net.onixary.shapeShifterCurseFabric.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;

public class CodexData {
    // 集中管理Codex的数据

    public static enum ContentType{
        TITLE,
        APPEARANCE,
        PROS,
        CONS,
        INSTINCTS,
        NAME
    }
    // static texts
    // headers
    public static final Text headerStatus = Text.translatable("codex.header.status");
    public static final Text headerAppearance = Text.translatable("codex.header.appearance");
    public static final Text headerPros = Text.translatable("codex.header.pros");
    public static final Text headerCons = Text.translatable("codex.header.cons");
    public static final Text headerInstincts = Text.translatable("codex.header.instincts");
    // status —— 保留原版无后缀 key 供 tier<=0（original 原形）兜底
    private static final Text statusNormal = Text.translatable("codex.status.normal");
    private static final Text statusInfected = Text.translatable("codex.status.infected");
    private static final Text statusBeforeMoon = Text.translatable("codex.status.before_moon");
    private static final Text statusUnderMoon = Text.translatable("codex.status.under_moon");
    // 原形人类(original human, tier<=0)在诅咒之月时的专属文案——它不是被诅咒的生物，月相对它而言只是异常天象
    private static final Text statusNormalHuman = Text.translatable("codex.status.normal_human");
    private static final Text statusBeforeMoonHuman = Text.translatable("codex.status.before_moon_human");
    private static final Text statusUnderMoonHuman = Text.translatable("codex.status.under_moon_human");
    // 按 phase 分层的 status 文案：index 0=phase0(tier1,初变) 1=phase1(tier2) 2=phase2(tier3,临界) 3=phase3(tier4,永久)
    private static final Text[] statusNormalByPhase = {
            Text.translatable("codex.status.normal_0"),
            Text.translatable("codex.status.normal_1"),
            Text.translatable("codex.status.normal_2"),
            Text.translatable("codex.status.normal_3")
    };
    private static final Text[] statusBeforeMoonByPhase = {
            Text.translatable("codex.status.before_moon_0"),
            Text.translatable("codex.status.before_moon_1"),
            Text.translatable("codex.status.before_moon_2"),
            Text.translatable("codex.status.before_moon_3")
    };
    private static final Text[] statusUnderMoonByPhase = {
            Text.translatable("codex.status.under_moon_0"),
            Text.translatable("codex.status.under_moon_1"),
            Text.translatable("codex.status.under_moon_2"),
            Text.translatable("codex.status.under_moon_3")
    };
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
    
    public static Text getPlayerStatusText(PlayerEntity player){
        // 根据当前角色状态与环境返回对应的状态文本
        StringBuilder statusTextBuilder = new StringBuilder();
        boolean hasAnyStatus = false;

        /* 重构后不需要了 仅用于参考旧实现逻辑
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
         */
        // 按当前形态 tier 选文案：tier>=1 走 phase 分层(tier1-4 → phase0-3)；tier<=0（原形人类）走 human 专属文案
        int tier = FormUtils.getPlayerForm(player).getFormTier();
        int phaseIndex = Math.min(Math.max(tier - 1, 0), 3);
        boolean usePhase = tier >= 1;

        if (EffectManager.hasTransformativeEffect(player)) {
            statusTextBuilder.append(statusInfected.getString());
            hasAnyStatus = true;
        }

        if(CursedMoon.isCursedMoonDay(player.getWorld())){
            if(CursedMoon.isNight(player.getWorld())){
                statusTextBuilder.append((usePhase ? statusUnderMoonByPhase[phaseIndex] : statusUnderMoonHuman).getString());
                hasAnyStatus = true;
            }
            else{
                statusTextBuilder.append((usePhase ? statusBeforeMoonByPhase[phaseIndex] : statusBeforeMoonHuman).getString());
                hasAnyStatus = true;
            }
        }

        if(!hasAnyStatus){
            statusTextBuilder.append((usePhase ? statusNormalByPhase[phaseIndex] : statusNormalHuman).getString());
        }

        return Text.literal(statusTextBuilder.toString());
    }

    //判断给定 Text 是否属于"无特殊状态"(normal 系列)文案，供附属区分接管时机。
     
    public static boolean isNormalStatus(Text text){
        if(text == null) return false;
        String s = text.getString();
        if(statusNormal.getString().equals(s)) return true;
        if(statusNormalHuman.getString().equals(s)) return true;
        for(Text t : statusNormalByPhase){
            if(t.getString().equals(s)) return true;
        }
        return false;
    }

    public static Text getDescText(ContentType type, PlayerEntity player) {
        int tier = FormUtils.getPlayerForm(player).getFormTier();
        switch (type) {
            case TITLE -> {
                return Text.empty();
            }
            case APPEARANCE -> {
                return switch (tier) {
                    case -1, 0 -> descAppearance_normal;
                    case 1 -> descAppearance_0;
                    case 2 -> descAppearance_1;
                    case 3 -> descAppearance_2;
                    case 4 -> descAppearance_3;
                    default -> Text.empty();
                };
            }
            case PROS -> {
                return switch (tier) {
                    case -1, 0 -> descPros_normal;
                    case 1 -> descPros_0;
                    case 2 -> descPros_1;
                    case 3 -> descPros_2;
                    case 4 -> descPros_3;
                    default -> Text.empty();
                };
            }
            case CONS -> {
                return switch (tier) {
                    case -1, 0 -> descCons_normal;
                    case 1 -> descCons_0;
                    case 2 -> descCons_1;
                    case 3 -> descCons_2;
                    case 4 -> descCons_3;
                    default -> Text.empty();
                };
            }
            case INSTINCTS -> {
                return switch (tier) {
                    case -1, 0 -> descInstincts_normal;
                    case 1 -> descInstincts_0;
                    case 2 -> descInstincts_1;
                    case 3 -> descInstincts_2;
                    case 4 -> descInstincts_3;
                    default -> Text.empty();
                };
            }
        }
        return Text.empty();
    }

    public static Text getContentText(ContentType type, PlayerEntity player){
        return FormUtils.getPlayerForm(player).getContentText(type);
    }
}
