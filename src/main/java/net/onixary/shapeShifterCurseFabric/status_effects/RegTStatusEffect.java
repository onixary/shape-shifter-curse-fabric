package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class RegTStatusEffect {
    private RegTStatusEffect(){}

    // 记录所有变身状态效果
    public static final List<Identifier> TRANSFORMATIVE_STATUS_EFFECTS = new ArrayList<>();

    public static final BaseTransformativeStatusEffect TO_BAT_0_EFFECT = register("to_bat_0_effect", new TransformativeStatus(RegPlayerForms.BAT_0));
    public static final BaseTransformativeStatusEffect TO_AXOLOTL_0_EFFECT = register("to_axolotl_0_effect", new TransformativeStatus(RegPlayerForms.AXOLOTL_0));
    public static final BaseTransformativeStatusEffect TO_OCELOT_0_EFFECT = register("to_ocelot_0_effect", new TransformativeStatus(RegPlayerForms.OCELOT_0));
    public static final BaseTransformativeStatusEffect TO_FAMILIAR_FOX_0_EFFECT = register("to_familiar_fox_0_effect", new TransformativeStatus(RegPlayerForms.FAMILIAR_FOX_0));
    public static final BaseTransformativeStatusEffect TO_SNOW_FOX_0_EFFECT = register("to_snow_fox_0_effect", new TransformativeStatus(RegPlayerForms.SNOW_FOX_0));
    public static final BaseTransformativeStatusEffect TO_ANUBIS_WOLF_0_EFFECT = register("to_anubis_wolf_0_effect", new TransformativeStatus(RegPlayerForms.ANUBIS_WOLF_0));
    public static final BaseTransformativeStatusEffect TO_ALLAY_SP_EFFECT = register("to_allay_sp_effect", new TransformativeStatus(RegPlayerForms.ALLAY_SP));
    public static final BaseTransformativeStatusEffect TO_FERAL_CAT_SP_EFFECT = register("to_feral_cat_sp_effect", new TransformativeStatus(RegPlayerForms.FERAL_CAT_SP));

    public static final BaseTransformativeStatusEffect TO_CUSTOM_STATUE_EFFECT = register("to_custom_statue_effect", new CustomTransformativeStatue());

    // empty custom forms
    /* 未支持数据包时代的占位形态 现在可以使用数据添加形态了
    public static final BaseTransformativeStatusEffect TO_ALPHA_0_EFFECT = register("to_alpha_0_effect", new TransformativeStatus(RegPlayerForms.ALPHA_0));
    public static final BaseTransformativeStatusEffect TO_BETA_0_EFFECT = register("to_beta_0_effect", new TransformativeStatus(RegPlayerForms.BETA_0));
    public static final BaseTransformativeStatusEffect TO_GAMMA_0_EFFECT = register("to_gamma_0_effect", new TransformativeStatus(RegPlayerForms.GAMMA_0));
    public static final BaseTransformativeStatusEffect TO_OMEGA_SP_EFFECT = register("to_omega_sp_effect", new TransformativeStatus(RegPlayerForms.OMEGA_SP));
    public static final BaseTransformativeStatusEffect TO_PSI_SP_EFFECT = register("to_psi_sp_effect", new TransformativeStatus(RegPlayerForms.PSI_SP));
    public static final BaseTransformativeStatusEffect TO_CHI_SP_EFFECT = register("to_chi_sp_effect", new TransformativeStatus(RegPlayerForms.CHI_SP));
    public static final BaseTransformativeStatusEffect TO_PHI_SP_EFFECT = register("to_phi_sp_effect", new TransformativeStatus(RegPlayerForms.PHI_SP));
     */

    private static <T extends BaseTransformativeStatusEffect> T register(String path, T effect) {
        // 固定MOD_ID的注册方法
        Identifier id = new Identifier(ShapeShifterCurseFabric.MOD_ID, path);
        return register(id, effect);
    }

    public static <T extends BaseTransformativeStatusEffect> T register(Identifier path, T effect) {
        // 拓展Mod的注册方法
        TRANSFORMATIVE_STATUS_EFFECTS.add(path);
        return Registry.register(Registries.STATUS_EFFECT, path, effect);
    }

    /* 使用EffectManager.hasTransformativeEffect(PlayerEntity)代替
    public static boolean hasAnyEffect(PlayerEntity player) {
        // is player has any transformative effect
        for (Identifier id : TRANSFORMATIVE_STATUS_EFFECTS) {
            if (player.hasStatusEffect(Registries.STATUS_EFFECT.get(id))) {
                return true;
            }
        }
        return false;
    }
     */

    /* 使用EffectManager.clearTransformativeEffect(PlayerEntity)代替
    public static void removeVisualEffects(PlayerEntity player) {
        // remove all transformative effects potion icon
        for (Identifier id : TRANSFORMATIVE_STATUS_EFFECTS) {
            player.removeStatusEffect(Registries.STATUS_EFFECT.get(id));
        }
    }
     */

    public static void initialize() {}
}
