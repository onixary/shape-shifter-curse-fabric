package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationTransform {
    private AnimationTransform() {
    }

    private static AnimationHolder anim_on_transform_default = AnimationHolder.EMPTY;
    private static AnimationHolder anim_on_transform_normal_to_feral = AnimationHolder.EMPTY;
    private static AnimationHolder anim_on_transform_feral_to_normal = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerForms curForm, PlayerForms toForm) {
        // 适配Feral，根据当前形态和目标形态返回对应的动画
        // 添加null捕获防止恶性bug
        if(curForm == null || toForm == null){
            ShapeShifterCurseFabric.LOGGER.info("getFormAnimToPlay called with null curForm or null toForm, returning default animation.");
            return anim_on_transform_default;
        }

        boolean curIsFeral = RegFormConfig.CONFIGS.get(curForm).getBodyType() == PlayerFormBodyType.FERAL;
        boolean toIsFeral = RegFormConfig.CONFIGS.get(toForm).getBodyType() == PlayerFormBodyType.FERAL;

        if(!curIsFeral && toIsFeral)
        {
            return anim_on_transform_normal_to_feral;
        }
        else if(curIsFeral && !toIsFeral)
        {
            return anim_on_transform_feral_to_normal;
        }
        return anim_on_transform_default;
    }

    public static void registerAnims() {
        anim_on_transform_default = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform"), true);
        anim_on_transform_normal_to_feral = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_normal_to_feral"), true);
        anim_on_transform_feral_to_normal = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_feral_to_normal"), true);
    }
}
