package net.onixary.shapeShifterCurseFabric.player_form.transform;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleDirectTransform;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.EFFECT_ATTACHMENT;

public class TransformRelatedItems {
    private TransformRelatedItems() {
    }

    public static final Item TRANSFORM_CURE = Items.GOLDEN_APPLE;
    public static final Item TRANSFORM_CURE_FINAL = Items.ENCHANTED_GOLDEN_APPLE;
    // todo: 之后使用自定义物品，目前暂时用蜘蛛眼占位
    public static final Item TRANSFORM_CATALYST = Items.SPIDER_EYE;

    public static void OnUseCure(PlayerEntity player) {
        // 如果不是最终阶段，则回退一个阶段
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 无用
                break;
            case -1:
                // 无用
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_cure").formatted(Formatting.YELLOW));
                break;
            case 0:
                toForm = PlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure").formatted(Formatting.YELLOW));
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure").formatted(Formatting.YELLOW));
                break;
            case 2:
                // 不会生效
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_cure").formatted(Formatting.YELLOW));
            default:
                break;
        }
        if (toForm == null) {
            return;
        }

        handleDirectTransform(player, toForm, true);
    }

    public static void OnUseCureFinal(PlayerEntity player) {
        // 可以回退到最初阶段
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 无用
                break;
            case -1:
                // 无用
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_cure_final").formatted(Formatting.YELLOW));
                break;
            case 0:
                toForm = PlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_final").formatted(Formatting.YELLOW));
                break;
            case 1:
                toForm = PlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_final").formatted(Formatting.YELLOW));
                break;
            case 2:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_cure_final").formatted(Formatting.YELLOW));
            default:
                break;
        }
        if (toForm == null) {
            return;
        }

        handleDirectTransform(player, toForm, true);
    }

    public static void OnUseCatalyst(PlayerEntity player) {
        // 直接增加一个阶段
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 无用
                break;
            case -1:
                // 查看当前是否有在生效的效果，有的话则应用，没有的话则无用
                PlayerEffectAttachment attachment = player.getAttached(EFFECT_ATTACHMENT);
                if (attachment != null && attachment.currentEffect != null){
                    EffectManager.applyEffect(player);
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst_attached").formatted(Formatting.YELLOW));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst").formatted(Formatting.YELLOW));
                }
                break;
            case 0:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_catalyst").formatted(Formatting.YELLOW));
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_catalyst").formatted(Formatting.YELLOW));
                break;
            case 2:
                // todo: 也许之后会有额外阶段的逻辑..类似于彻底改变游戏玩法，但是在死亡后回退的
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_catalyst").formatted(Formatting.YELLOW));
            default:
                break;
        }
        if (toForm == null) {
            return;
        }

        handleDirectTransform(player, toForm, false);
    }
}
