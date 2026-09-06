package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

import java.util.ArrayList;
import java.util.List;

public class NormalPerk implements IPerk {
    public final Identifier perkID;
    public final List<Identifier> powerAdd = new ArrayList<>();
    public final List<Identifier> powerRemove = new ArrayList<>();

    public NormalPerk(Identifier perkID) {
        this.perkID = perkID;
    }

    public NormalPerk addPower(Identifier... powerIDs) {
        for (Identifier powerID : powerIDs) {
            if (!powerAdd.contains(powerID)) {
                powerAdd.add(powerID);
            }
        }
        return this;
    }

    public NormalPerk removePower(Identifier... powerIDs) {
        for (Identifier powerID : powerIDs) {
            if (!powerRemove.contains(powerID)) {
                powerRemove.add(powerID);
            }
        }
        return this;
    }

    @Override
    public Identifier getID() {
        return this.perkID;
    }

    @Override
    public void onLoad(PlayerEntity player, IForm form) {
        Identifier powerSource = form.getFormLayer().getRight();
        for (Identifier powerID : powerAdd) {
            FormUtils.applyPower(player, powerID, powerSource);
        }
        for (Identifier powerID : powerRemove) {
            FormUtils.removePower(player, powerID, powerSource);
        }
    }
}
