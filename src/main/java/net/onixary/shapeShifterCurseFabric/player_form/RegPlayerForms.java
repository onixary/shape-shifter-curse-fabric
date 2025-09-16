package net.onixary.shapeShifterCurseFabric.player_form;


import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_form.forms.*;

public class RegPlayerForms {
    private static final Identifier playerForms_REGISTRY_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "player_form");
    public static Registry<PlayerFormBase> playerForms = new SimpleRegistry<PlayerFormBase>(RegistryKey.ofRegistry(playerForms_REGISTRY_ID), Lifecycle.stable());
    private static final Identifier playerFormGroups_REGISTRY_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "player_form_group");
    public static Registry<PlayerFormGroup> playerFormGroups = new SimpleRegistry<PlayerFormGroup>(RegistryKey.ofRegistry(playerFormGroups_REGISTRY_ID), Lifecycle.stable());

    // Origins.MODID 等我之后再改
    // Builtin PlayerForms
    public static PlayerFormBase ORIGINAL_BEFORE_ENABLE = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_original_before_enable")));
    public static PlayerFormBase ORIGINAL_SHIFTER = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_original_shifter")));
    // Bat
    public static PlayerFormBase BAT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_bat_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase BAT_1 = registerPlayerForm(new Form_Bat1(new Identifier(Origins.MODID, "form_bat_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase BAT_2 = registerPlayerForm(new Form_Bat2(new Identifier(Origins.MODID, "form_bat_2")).SetPhase(PlayerFormPhase.PHASE_2).SetHasSlowFall(true).SetOverrideHandAnim(true));
    public static PlayerFormBase BAT_3 = registerPlayerForm(new Form_Bat3(new Identifier(Origins.MODID, "form_bat_3")).SetPhase(PlayerFormPhase.PHASE_3).SetHasSlowFall(true).SetOverrideHandAnim(true));
    // Axolotl
    public static PlayerFormBase AXOLOTL_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_axolotl_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase AXOLOTL_1 = registerPlayerForm(new Form_Axolotl1(new Identifier(Origins.MODID, "form_axolotl_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase AXOLOTL_2 = registerPlayerForm(new Form_Axolotl2(new Identifier(Origins.MODID, "form_axolotl_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase AXOLOTL_3 = registerPlayerForm(new Form_Axolotl3(new Identifier(Origins.MODID, "form_axolotl_3")).SetPhase(PlayerFormPhase.PHASE_3));
    // Ocelot
    public static PlayerFormBase OCELOT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_ocelot_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase OCELOT_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_ocelot_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase OCELOT_2 = registerPlayerForm(new Form_Ocelot2(new Identifier(Origins.MODID, "form_ocelot_2")).SetPhase(PlayerFormPhase.PHASE_2).SetCanSneakRush(true).SetCanRushJump(true));
    public static PlayerFormBase OCELOT_3 = registerPlayerForm(new Form_Ocelot3(new Identifier(Origins.MODID, "form_ocelot_3")).SetPhase(PlayerFormPhase.PHASE_3).SetCanSneakRush(true));
    // FamiliarFox
    public static PlayerFormBase FAMILIAR_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_familiar_fox_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase FAMILIAR_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_familiar_fox_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase FAMILIAR_FOX_2 = registerPlayerForm(new Form_Fox2(new Identifier(Origins.MODID, "form_familiar_fox_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase FAMILIAR_FOX_3 = registerPlayerForm(new Form_FeralBase(new Identifier(Origins.MODID, "form_familiar_fox_3")).SetPhase(PlayerFormPhase.PHASE_3));
    // SnowFox
    public static PlayerFormBase SNOW_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_snow_fox_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase SNOW_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_snow_fox_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase SNOW_FOX_2 = registerPlayerForm(new Form_Fox2(new Identifier(Origins.MODID, "form_snow_fox_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase SNOW_FOX_3 = registerPlayerForm(new Form_SnowFox3(new Identifier(Origins.MODID, "form_snow_fox_3")).SetPhase(PlayerFormPhase.PHASE_3));
    // ALLAY_SP
    public static PlayerFormBase ALLAY_SP = registerPlayerForm(new Form_Allay(new Identifier(Origins.MODID, "form_allay_sp")).SetPhase(PlayerFormPhase.PHASE_SP));
    // FERAL_CAT_SP
    public static PlayerFormBase FERAL_CAT_SP = registerPlayerForm(new Form_FeralBase(new Identifier(Origins.MODID, "form_feral_cat_sp")).SetPhase(PlayerFormPhase.PHASE_SP));

    // Custom Form
    public static PlayerFormBase ALPHA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_alpha_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase ALPHA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_alpha_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase ALPHA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_alpha_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase BETA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_beta_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase BETA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_beta_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase BETA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_beta_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase GAMMA_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_gamma_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase GAMMA_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_gamma_2")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase GAMMA_2 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_gamma_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase OMEGA_SP = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_omega_sp")).SetPhase(PlayerFormPhase.PHASE_SP));
    public static PlayerFormBase PSI_SP = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_psi_sp")).SetPhase(PlayerFormPhase.PHASE_SP));
    public static PlayerFormBase CHI_SP = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_chi_sp")).SetPhase(PlayerFormPhase.PHASE_SP));
    public static PlayerFormBase PHI_SP = registerPlayerForm(new Form_FeralBase(new Identifier(Origins.MODID, "form_phi_sp")).SetPhase(PlayerFormPhase.PHASE_SP));

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
        return Registry.register(playerForms, form.FormID, form);
    }

    public static <T extends PlayerFormGroup> T registerPlayerFormGroup(T formGroup) {
        return Registry.register(playerFormGroups, formGroup.GroupID, formGroup);
    }
}
