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
        if (EffectManager.hasTransformativeEffect(player)) {
            statusTextBuilder.append(statusInfected.getString());
            hasAnyStatus = true;
        }

        if(CursedMoon.isCursedMoonDay(player.getWorld())){
            if(CursedMoon.isNight(player.getWorld())){
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
