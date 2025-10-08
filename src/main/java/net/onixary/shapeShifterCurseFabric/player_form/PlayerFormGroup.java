package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerFormGroup {
    public Identifier GroupID;
    public Map<Integer, PlayerFormBase> Forms = new HashMap<>();

    public PlayerFormGroup(Identifier GroupID) {
        this.GroupID = GroupID;
    }

    public PlayerFormGroup addForm(PlayerFormBase form, int Index) {
        Forms.put(Index, form);
        form.setGroup(this, Index);
        return this;
    }

    public PlayerFormBase getForm(int Index) {
        PlayerFormBase form = Forms.get(Index);
        if (!RegPlayerForms.playerForms.containsKey(form.FormID) && !(form.getGroup() == this) && !(form.getIndex() == Index)) {
            Forms.remove(Index);
            ShapeShifterCurseFabric.LOGGER.warn("Form {} is not registered in the registry, removing it from group {}", form.FormID, GroupID);
            return null;
        }
        return Forms.get(Index);
    }

    public Optional<Integer> getFormIndex(PlayerFormBase form) {
        return getFormIndex(form.FormID);
    }

    public Optional<Integer> getFormIndex(Identifier formID) {
        for (Map.Entry<Integer, PlayerFormBase> entry : Forms.entrySet()) {
            if (entry.getValue().FormID.equals(formID) && getForm(entry.getKey()) != null) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public boolean hasForm(Identifier formID) {
        return getFormIndex(formID).isPresent();
    }

    public boolean hasForm(PlayerFormBase form) {
        return getFormIndex(form).isPresent();
    }

    public boolean hasForm(int Index) {
        return Forms.containsKey(Index);
    }

    public PlayerFormBase getNextForm(PlayerFormBase form) {
        return getFormIndex(form).map(index -> getForm(index + 1)).orElse(null);
    }

    public PlayerFormBase getPrevForm(PlayerFormBase form) {
        return getFormIndex(form).map(index -> getForm(index - 1)).orElse(null);
    }
}
