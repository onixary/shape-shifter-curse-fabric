package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ITransformReason;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.jetbrains.annotations.Nullable;

public class TransformRelatedItems {
    public static void OnUseCure(PlayerEntity player, @Nullable ItemStack stack) {

    }

    public static void OnUseCureFinal(PlayerEntity player, @Nullable ItemStack stack) {

    }

    public static void OnUseCreativeCure(PlayerEntity player, @Nullable ItemStack stack) {
        PlayerFormComponent.COMPONENT.get(player).lastTransformByCure = true;
        PlayerFormComponent.COMPONENT.sync(player);
        if(!RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player) && !RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)){
            TransformManager.startTransform(player, RegPlayerForms.ORIGINAL_SHIFTER, null);
        }
    }

    public static void OnUseCatalyst(PlayerEntity player, @Nullable ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        IForm nowForm = FormUtils.getPlayerForm(player);
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) { }
        else if (RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player)) {
            if (EffectManager.hasTransformativeEffect(player)) {
                EffectManager.ActiveTransformativeEffect(serverPlayer);
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst_attached").formatted(Formatting.YELLOW));
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CATALYST.trigger(serverPlayer);
            }
            else{
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst").formatted(Formatting.YELLOW));
            }
        }
        else if (FormUtils.SpecialForm.hasFlag(nowForm)) {
            // 为了这句文本 专门加了一个flag
            player.sendMessage(Text.translatable("info.shape-shifter-curse.sp_form_used_catalyst").formatted(Formatting.YELLOW));
        }
        else if (FormUtils.NoAnyCatalyst.hasFlag(nowForm)) {
            player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst_when_ignore").formatted(Formatting.DARK_PURPLE));
        }
        else if (FormUtils.NoCatalyst.hasFlag(nowForm)) {
            player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_catalyst").formatted(Formatting.YELLOW));
        }
        else {
            player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst").formatted(Formatting.YELLOW));
        }
        IForm nextForm = nowForm._getNextForm(player, ITransformReason.ItemReasonBuilder.apply(stack));
        if (nextForm != nowForm) {
            TransformManager.startTransform(player, nextForm, null);
        }
    }

    public static void OnUsePowerfulCatalyst(PlayerEntity player, @Nullable ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        IForm nowForm = FormUtils.getPlayerForm(player);
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) { }
        else if (FormUtils.CanTFToFinalForm.hasFlag(nowForm)) {
            IForm nextForm = nowForm._getNextForm(player, ITransformReason.ItemReasonBuilder.apply(stack));
            if (nextForm != nowForm) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_powerful_catalyst").formatted(Formatting.YELLOW));
                TransformManager.startTransform(player, nextForm, null);
            } else {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.form_used_powerful_catalyst_failed").formatted(Formatting.YELLOW));
            }
        } else {
            player.sendMessage(Text.translatable("info.shape-shifter-curse.form_used_powerful_catalyst_failed").formatted(Formatting.YELLOW));
        }
    }
}
