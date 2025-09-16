package net.onixary.shapeShifterCurseFabric.player_form;


import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.DefaultedRegistry;
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
    public static PlayerFormBase OCELOT_3 = registerPlayerForm(new Form_FeralBase(new Identifier(Origins.MODID, "form_ocelot_3")).SetPhase(PlayerFormPhase.PHASE_3).SetCanSneakRush(true));
    // FamiliarFox
    public static PlayerFormBase FAMILIAR_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_familiar_fox_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase FAMILIAR_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_familiar_fox_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase FAMILIAR_FOX_2 = registerPlayerForm(new Form_FamiliarFox2(new Identifier(Origins.MODID, "form_familiar_fox_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase FAMILIAR_FOX_3 = registerPlayerForm(new Form_FeralBase(new Identifier(Origins.MODID, "form_familiar_fox_3")).SetPhase(PlayerFormPhase.PHASE_3));

    // Builtin PlayerFormGroups
    public static PlayerFormGroup ORIGINAL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_form")).addForm(ORIGINAL_BEFORE_ENABLE, -2));
    public static PlayerFormGroup BASE_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "base_form")).addForm(ORIGINAL_SHIFTER, -1));
    public static PlayerFormGroup BAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_form")).addForm(BAT_0, 0).addForm(BAT_1, 1).addForm(BAT_2, 2).addForm(BAT_3, 3));
    public static PlayerFormGroup AXOLOTL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_form")).addForm(AXOLOTL_0, 0).addForm(AXOLOTL_1, 1).addForm(AXOLOTL_2, 2).addForm(AXOLOTL_3, 3));
    public static PlayerFormGroup OCELOT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_form")).addForm(OCELOT_0, 0).addForm(OCELOT_1, 1).addForm(OCELOT_2, 2).addForm(OCELOT_3, 3));
    public static PlayerFormGroup FAMILIAR_FOX_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_form")).addForm(FAMILIAR_FOX_0, 0).addForm(FAMILIAR_FOX_1, 1).addForm(FAMILIAR_FOX_2, 2).addForm(FAMILIAR_FOX_3, 3));

    public static <T extends PlayerFormBase> T registerPlayerForm(T form) {
        return Registry.register(playerForms, form.FormID, form);
    }

    public static <T extends PlayerFormGroup> T registerPlayerFormGroup(T formGroup) {
        return Registry.register(playerFormGroups, formGroup.GroupID, formGroup);
    }
}
