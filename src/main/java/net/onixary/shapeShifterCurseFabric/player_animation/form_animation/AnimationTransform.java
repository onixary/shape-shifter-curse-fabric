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
            //ShapeShifterCurseFabric.LOGGER.info("getFormAnimToPlay called with null curForm or null toForm, returning default animation.");
            return anim_on_transform_default;
        }

        try {
            // 确保 RegFormConfig.CONFIGS 已经被正确初始化
            if (RegFormConfig.CONFIGS.isEmpty()) {
                ShapeShifterCurseFabric.LOGGER.warn("RegFormConfig.CONFIGS is empty, returning default animation");
                return anim_on_transform_default;
            }

            // 检查当前形态和目标形态是否存在于配置中
            if (!RegFormConfig.CONFIGS.containsKey(curForm) || !RegFormConfig.CONFIGS.containsKey(toForm)) {
                ShapeShifterCurseFabric.LOGGER.warn("Form not found in configs - curForm: " + curForm + ", toForm: " + toForm);
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
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error in getFormAnimToPlay: " + e.getMessage());
            return anim_on_transform_default;
        }
    }

    public static void registerAnims() {
        anim_on_transform_default = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform"), true);
        anim_on_transform_normal_to_feral = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_normal_to_feral"), true);
        anim_on_transform_feral_to_normal = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform_feral_to_normal"), true);
    }
}
