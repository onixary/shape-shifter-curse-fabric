package net.onixary.shapeShifterCurseFabric.player_form;

import java.util.Arrays;

public enum PlayerForms {
    // group: form kind, index: form phase
    // SP form index is 5
    ORIGINAL_BEFORE_ENABLE("original_form", -2),
    ORIGINAL_SHIFTER("base_form", -1),
    BAT_0("bat_form", 0),
    BAT_1("bat_form", 1),
    BAT_2("bat_form", 2),
    BAT_3("bat_form", 3),
    AXOLOTL_0("axolotl_form", 0),
    AXOLOTL_1("axolotl_form", 1),
    AXOLOTL_2("axolotl_form", 2),
    AXOLOTL_3("axolotl_form", 3),
    OCELOT_0("ocelot_form", 0),
    OCELOT_1("ocelot_form", 1),
    OCELOT_2("ocelot_form", 2),
    OCELOT_3("ocelot_form", 3),
    FAMILIAR_FOX_0("familiar_fox_form", 0),
    FAMILIAR_FOX_1("familiar_fox_form", 1),
    FAMILIAR_FOX_2("familiar_fox_form", 2),
    FAMILIAR_FOX_3("familiar_fox_form", 3),
    SNOW_FOX_0("snow_fox_form", 0),
    SNOW_FOX_1("snow_fox_form", 1),
    SNOW_FOX_2("snow_fox_form", 2),
    SNOW_FOX_3("snow_fox_form", 3),
    ALLAY_SP("allay_form", 5),
    FERAL_CAT_SP("feral_cat_form", 5),

    // Custom empty forms
    ALPHA_0("alpha_form", 0),
    ALPHA_1("alpha_form", 1),
    ALPHA_2("alpha_form", 2),
    BETA_0("beta_form", 0),
    BETA_1("beta_form", 1),
    BETA_2("beta_form", 2),
    GAMMA_0("gamma_form", 0),
    GAMMA_1("gamma_form", 1),
    GAMMA_2("gamma_form", 2),
    // Custom empty special forms
    OMEGA_SP("omega_sp_form", 5),
    PSI_SP("psi_sp_form", 5),
    CHI_SP("chi_sp_form", 5),
    PHI_SP("phi_sp_form", 5);


    private final String group;
    private final int index;

    PlayerForms(String group, int index) {
        this.group = group;
        this.index = index;
    }

    public String getGroup() {
        return group;
    }

    public int getIndex() {
        return index;
    }

    public static PlayerForms[] getFormsByGroup(String group) {
        return Arrays.stream(values())
                .filter(form -> form.getGroup().equals(group))
                .toArray(PlayerForms[]::new);
    }
}
