package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.NormalSubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.Nullable;

// TODO 发布时一定得移除
public class ExampleSubForm extends NormalSubForm {
    public ExampleSubForm(Identifier formID) {
        super(formID, RegPlayerForms.FERAL_CAT_SP);
        this.removePower(ShapeShifterCurseFabric.identifier("form_disable_leg_armor"), ShapeShifterCurseFabric.identifier("form_disable_feet_armor"));
    }

    @Override
    public @Nullable Pair<Identifier, Identifier> getRenderLayerOverride() {
        return RegPlayerForms.ALLAY_SP.getFormLayer();
    }
}
