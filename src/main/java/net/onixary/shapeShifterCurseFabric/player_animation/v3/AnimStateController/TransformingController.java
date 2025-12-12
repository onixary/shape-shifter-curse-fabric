package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class TransformingController extends AbstractAnimStateController {
    private static AnimationHolder anim_on_transform_default = AnimationHolder.EMPTY;
    private static AnimationHolder anim_on_transform_normal_to_feral = AnimationHolder.EMPTY;
    private static AnimationHolder anim_on_transform_feral_to_normal = AnimationHolder.EMPTY;

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        String fromFormName = ShapeShifterCurseFabricClient.getClientTransformFromForm(player.getUuid());
        String toFormName = ShapeShifterCurseFabricClient.getClientTransformToForm(player.getUuid());
        PlayerFormBase transformCurrentForm = null;
        PlayerFormBase transformToForm = null;
        try {
            transformCurrentForm = fromFormName != null ? RegPlayerForms.getPlayerForm(fromFormName) : null;
            transformToForm = toFormName != null ? RegPlayerForms.getPlayerForm(toFormName) : null;
        } catch (IllegalArgumentException e) {
            // 如果解析失败，使用当前形态作为 fallback
            transformCurrentForm = data.playerForm;
            transformToForm = data.playerForm;
        }
        if(transformCurrentForm == null || transformToForm == null){
            //ShapeShifterCurseFabric.LOGGER.info("getFormAnimToPlay called with null curForm or null toForm, returning default animation.");
            return anim_on_transform_default;
        }
        try {
            boolean curIsFeral = transformCurrentForm.getBodyType() == PlayerFormBodyType.FERAL;
            boolean toIsFeral = transformToForm.getBodyType() == PlayerFormBodyType.FERAL;
            if(!curIsFeral && toIsFeral)
            {
                return anim_on_transform_normal_to_feral;
            }
            else if(curIsFeral && !toIsFeral)
            {
                return anim_on_transform_feral_to_normal;
            }
            return anim_on_transform_default;
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error in getFormAnimToPlay: " + e.getMessage());
            return anim_on_transform_default;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        anim_on_transform_default = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform"), true);
        anim_on_transform_normal_to_feral = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_normal_to_feral"), true);
        anim_on_transform_feral_to_normal = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_feral_to_normal"), true);
        super.registerAnim(player, data);
    }

    @Override
    public boolean isEnabled(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return ShapeShifterCurseFabricClient.isClientTransforming(player.getUuid());
    }
}
