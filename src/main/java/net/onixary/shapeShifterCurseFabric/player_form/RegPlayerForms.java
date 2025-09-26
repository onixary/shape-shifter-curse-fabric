package net.onixary.shapeShifterCurseFabric.player_form;


import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_form.forms.*;

import java.util.LinkedHashMap;

public class RegPlayerForms {
//    private static final Identifier playerForms_REGISTRY_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "player_form");
//    public static Registry<PlayerFormBase> playerForms = new SimpleRegistry<PlayerFormBase>(RegistryKey.ofRegistry(playerForms_REGISTRY_ID), Lifecycle.stable());
//    private static final Identifier playerFormGroups_REGISTRY_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "player_form_group");
//    public static Registry<PlayerFormGroup> playerFormGroups = new SimpleRegistry<PlayerFormGroup>(RegistryKey.ofRegistry(playerFormGroups_REGISTRY_ID), Lifecycle.stable());
    public static LinkedHashMap<Identifier, PlayerFormBase> playerForms = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, PlayerFormGroup> playerFormGroups = new LinkedHashMap<>();

    // Builtin PlayerForms
    // 在Java中null不能使用equals方法
    // Original
    public static PlayerFormBase ORIGINAL_BEFORE_ENABLE = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_before_enable")));
    public static PlayerFormBase ORIGINAL_SHIFTER = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_shifter")));
    // Bat
    public static PlayerFormBase BAT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase BAT_1 = registerPlayerForm(new Form_Bat1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase BAT_2 = registerPlayerForm(new Form_Bat2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_2")).setPhase(PlayerFormPhase.PHASE_2).SetHasSlowFall(true).SetOverrideHandAnim(true));
    public static PlayerFormBase BAT_3 = registerPlayerForm(new Form_Bat3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_3")).setPhase(PlayerFormPhase.PHASE_3).SetHasSlowFall(true).SetOverrideHandAnim(true));
    // Axolotl
    public static PlayerFormBase AXOLOTL_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase AXOLOTL_1 = registerPlayerForm(new Form_Axolotl1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase AXOLOTL_2 = registerPlayerForm(new Form_Axolotl2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase AXOLOTL_3 = registerPlayerForm(new Form_Axolotl3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_3")).setPhase(PlayerFormPhase.PHASE_3)).SetCanRushJump(true);
    // Ocelot
    public static PlayerFormBase OCELOT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase OCELOT_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase OCELOT_2 = registerPlayerForm(new Form_Ocelot2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_2")).setPhase(PlayerFormPhase.PHASE_2).SetCanSneakRush(true).SetCanRushJump(true));
    public static PlayerFormBase OCELOT_3 = registerPlayerForm(new Form_Ocelot3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_3")).setPhase(PlayerFormPhase.PHASE_3).SetCanSneakRush(true));
    // FamiliarFox
    public static PlayerFormBase FAMILIAR_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase FAMILIAR_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase FAMILIAR_FOX_2 = registerPlayerForm(new Form_Fox2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase FAMILIAR_FOX_3 = registerPlayerForm(new Form_FeralBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_3")).setPhase(PlayerFormPhase.PHASE_3));
    // SnowFox
    public static PlayerFormBase SNOW_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase SNOW_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase SNOW_FOX_2 = registerPlayerForm(new Form_Fox2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase SNOW_FOX_3 = registerPlayerForm(new Form_SnowFox3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_3")).setPhase(PlayerFormPhase.PHASE_3));
    // ALLAY_SP
    public static PlayerFormBase ALLAY_SP = registerPlayerForm(new Form_Allay(new Identifier(ShapeShifterCurseFabric.MOD_ID, "allay_sp")).setPhase(PlayerFormPhase.PHASE_SP));
    // FERAL_CAT_SP
    public static PlayerFormBase FERAL_CAT_SP = registerPlayerForm(new Form_FeralBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "feral_cat_sp")).setPhase(PlayerFormPhase.PHASE_SP));

    // Custom Form
    public static PlayerFormBase ALPHA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "alpha_0")).setPhase(PlayerFormPhase.PHASE_0).SetIsCustomForm(true));
    public static PlayerFormBase ALPHA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "alpha_1")).setPhase(PlayerFormPhase.PHASE_1).SetIsCustomForm(true));
    public static PlayerFormBase ALPHA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "alpha_2")).setPhase(PlayerFormPhase.PHASE_2).SetIsCustomForm(true));
    public static PlayerFormBase BETA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "beta_0")).setPhase(PlayerFormPhase.PHASE_0).SetIsCustomForm(true));
    public static PlayerFormBase BETA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "beta_1")).setPhase(PlayerFormPhase.PHASE_1).SetIsCustomForm(true));
    public static PlayerFormBase BETA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "beta_2")).setPhase(PlayerFormPhase.PHASE_2).SetIsCustomForm(true));
    public static PlayerFormBase GAMMA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "gamma_0")).setPhase(PlayerFormPhase.PHASE_0).SetIsCustomForm(true));
    public static PlayerFormBase GAMMA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "gamma_1")).setPhase(PlayerFormPhase.PHASE_1).SetIsCustomForm(true));
    public static PlayerFormBase GAMMA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "gamma_2")).setPhase(PlayerFormPhase.PHASE_2).SetIsCustomForm(true));
    public static PlayerFormBase OMEGA_SP = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "omega_sp")).setPhase(PlayerFormPhase.PHASE_SP).SetIsCustomForm(true));
    public static PlayerFormBase PSI_SP = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "psi_sp")).setPhase(PlayerFormPhase.PHASE_SP).SetIsCustomForm(true));
    public static PlayerFormBase CHI_SP = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "chi_sp")).setPhase(PlayerFormPhase.PHASE_SP).SetIsCustomForm(true));
    public static PlayerFormBase PHI_SP = registerPlayerForm(new Form_FeralBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "phi_sp")).setPhase(PlayerFormPhase.PHASE_SP).SetIsCustomForm(true));

    // Builtin PlayerFormGroups
    public static PlayerFormGroup ORIGINAL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_form")).addForm(ORIGINAL_BEFORE_ENABLE, -2));
    public static PlayerFormGroup BASE_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "base_form")).addForm(ORIGINAL_SHIFTER, -1));
    public static PlayerFormGroup BAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_form")).addForm(BAT_0, 0).addForm(BAT_1, 1).addForm(BAT_2, 2).addForm(BAT_3, 3));
    public static PlayerFormGroup AXOLOTL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_form")).addForm(AXOLOTL_0, 0).addForm(AXOLOTL_1, 1).addForm(AXOLOTL_2, 2).addForm(AXOLOTL_3, 3));
    public static PlayerFormGroup OCELOT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_form")).addForm(OCELOT_0, 0).addForm(OCELOT_1, 1).addForm(OCELOT_2, 2).addForm(OCELOT_3, 3));
    public static PlayerFormGroup FAMILIAR_FOX_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_form")).addForm(FAMILIAR_FOX_0, 0).addForm(FAMILIAR_FOX_1, 1).addForm(FAMILIAR_FOX_2, 2).addForm(FAMILIAR_FOX_3, 3));
    public static PlayerFormGroup SNOW_FOX_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_form")).addForm(SNOW_FOX_0, 0).addForm(SNOW_FOX_1, 1).addForm(SNOW_FOX_2, 2).addForm(SNOW_FOX_3, 3));
    public static PlayerFormGroup ALLAY_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "allay_form")).addForm(ALLAY_SP, 5));
    public static PlayerFormGroup FERAL_CAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "feral_cat_form")).addForm(FERAL_CAT_SP, 5));

    // Custom Form
    public static PlayerFormGroup ALPHA_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "alpha_form")).addForm(ALPHA_0, 0).addForm(ALPHA_1, 1).addForm(ALPHA_2, 2));
    public static PlayerFormGroup BETA_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "beta_form")).addForm(BETA_0, 0).addForm(BETA_1, 1).addForm(BETA_2, 2));
    public static PlayerFormGroup GAMMA_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "gamma_form")).addForm(GAMMA_0, 0).addForm(GAMMA_1, 1).addForm(GAMMA_2, 2));
    public static PlayerFormGroup OMEGA_SP_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "omega_sp_form")).addForm(OMEGA_SP, 5));
    public static PlayerFormGroup PSI_SP_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "psi_sp_form")).addForm(PSI_SP, 5));
    public static PlayerFormGroup CHI_SP_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "chi_sp_form")).addForm(CHI_SP, 5));
    public static PlayerFormGroup PHI_SP_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "phi_sp_form")).addForm(PHI_SP, 5));


    public static <T extends PlayerFormBase> T registerPlayerForm(T form) {
//        return Registry.register(playerForms, form.FormID, form);
        playerForms.put(form.FormID, form);
        return form;
    }

    public static <T extends PlayerFormGroup> T registerPlayerFormGroup(T formGroup) {
//        return Registry.register(playerFormGroups, formGroup.GroupID, formGroup);
        playerFormGroups.put(formGroup.GroupID, formGroup);
        return formGroup;
    }

    public static PlayerFormBase getPlayerForm(Identifier id) {
        return playerForms.get(id);
    }

    public static PlayerFormBase getPlayerForm(String id) {
        return playerForms.get(Identifier.tryParse(id));
    }

    public static PlayerFormBase getPlayerFormOrThrow(Identifier id) {
        PlayerFormBase form = getPlayerForm(id);
        if (form == null) {
            throw new IllegalArgumentException("Unknown player form: " + id);
        }
        return form;
    }

    public static PlayerFormBase getPlayerFormOrThrow(String id) {
        return getPlayerFormOrThrow(Identifier.tryParse(id));
    }

    public static PlayerFormBase getPlayerFormOrDefault(Identifier id, PlayerFormBase defaultForm) {
        PlayerFormBase form = getPlayerForm(id);
        if (form == null) {
            return defaultForm;

        }
        return form;
    }

    public static PlayerFormBase getPlayerFormOrDefault(String id, PlayerFormBase defaultForm) {
        return getPlayerFormOrDefault(Identifier.tryParse(id), defaultForm);
    }

    // 比较两个PlayerForm是否相等 支持null
    public static Boolean IsPlayerFormEqual(PlayerFormBase form1, PlayerFormBase form2) {
        if (form1 == null || form2 == null) {
            return form1 == null && form2 == null;
        }
        return form1.equals(form2);
    }

    public static PlayerFormGroup getPlayerFormGroup(Identifier id) {
        return playerFormGroups.get(id);
    }

    public static PlayerFormGroup getPlayerFormGroup(String id) {
        return playerFormGroups.get(Identifier.tryParse(id));
    }
}
