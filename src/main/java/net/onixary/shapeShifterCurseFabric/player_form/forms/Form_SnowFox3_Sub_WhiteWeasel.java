package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.player_form.NormalSubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.Nullable;

public class Form_SnowFox3_Sub_WhiteWeasel extends NormalSubForm {
    public Form_SnowFox3_Sub_WhiteWeasel(Identifier formID) {
        super(formID, RegPlayerForms.SNOW_FOX_3);
    }

    @Override
    public @Nullable Pair<Identifier, Identifier> getRenderLayerOverride() {
        return new Pair<>(Identifier.of("origins", "origin"), Identifier.of(this.getFormID().getNamespace(), "form_" + this.getFormID().getPath()));
    }
}
