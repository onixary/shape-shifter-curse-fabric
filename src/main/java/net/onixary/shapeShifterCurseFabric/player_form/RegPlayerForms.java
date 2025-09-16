package net.onixary.shapeShifterCurseFabric.player_form;


import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_form.forms.Form_Bat1;
import net.onixary.shapeShifterCurseFabric.player_form.forms.Form_Bat2;
import net.onixary.shapeShifterCurseFabric.player_form.forms.Form_Bat3;

public class RegPlayerForms {
    public static Registry<PlayerFormBase> playerForms;
    public static Registry<PlayerFormGroup> playerFormGroups;

    // Origins.MODID 等我之后再改
    // Builtin PlayerForms
    public static PlayerFormBase ORIGINAL_BEFORE_ENABLE = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_original_before_enable")));
    public static PlayerFormBase ORIGINAL_SHIFTER = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_original_shifter")));
    // Bat
    public static PlayerFormBase BAT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(Origins.MODID, "form_bat_0")).SetPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase BAT_1 = registerPlayerForm(new Form_Bat1(new Identifier(Origins.MODID, "form_bat_1")).SetPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase BAT_2 = registerPlayerForm(new Form_Bat2(new Identifier(Origins.MODID, "form_bat_2")).SetPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase BAT_3 = registerPlayerForm(new Form_Bat3(new Identifier(Origins.MODID, "form_bat_3")).SetPhase(PlayerFormPhase.PHASE_3));

    // Builtin PlayerFormGroups
    public static PlayerFormGroup ORIGINAL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_form")).addForm(ORIGINAL_BEFORE_ENABLE, -2));
    public static PlayerFormGroup BASE_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "base_form")).addForm(ORIGINAL_SHIFTER, -1));
    public static PlayerFormGroup BAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_form")).addForm(BAT_0, 0).addForm(BAT_1, 1).addForm(BAT_2, 2).addForm(BAT_3, 3));

    public static <T extends PlayerFormBase> T registerPlayerForm(T form) {
        return Registry.register(playerForms, form.FormID, form);
    }

    public static <T extends PlayerFormGroup> T registerPlayerFormGroup(T formGroup) {
        return Registry.register(playerFormGroups, formGroup.GroupID, formGroup);
    }
}
