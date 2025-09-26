package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormGroup;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

// 此Utils仅供测试使用 会随时删除
public class TestUtils {
    static class PlayerFormDyanmic extends PlayerFormBase {
        Identifier formOriginID;

        public PlayerFormDyanmic(Identifier formID, Identifier formOriginID) {
            super(formID);
            this.formOriginID = formOriginID;
        }

        @Override
        public Identifier getFormOriginID() {
            return formOriginID;
        }
    }


    public static PlayerFormBase CreateDynamicPlayerForm(Identifier formID, Identifier formOriginID, boolean NeedRegister) {
        PlayerFormBase form = new PlayerFormDyanmic(formID, formOriginID);
        PlayerFormGroup group = new PlayerFormGroup(formID).addForm(form, 0);
        if (NeedRegister) {
            RegPlayerForms.registerPlayerForm(form);
            RegPlayerForms.registerPlayerFormGroup(group);
        }
        return form;
    }

    public static void RemovePlayerForm(Identifier formID) {
        if (RegPlayerForms.playerForms.get(formID) == null) {
            ShapeShifterCurseFabric.LOGGER.warn("RemovePlayerForm: formID is not registered");
            return;
        }
        RegPlayerForms.playerForms.remove(formID);
    }
}
