package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class NormalPerk implements IPerk, IPerkClient {
    public final Identifier perkID;
    public final List<Identifier> powerAdd = new ArrayList<>();
    public final List<Identifier> powerRemove = new ArrayList<>();

    public boolean repeatable = false;
    public BiConsumer<PlayerEntity, IForm> onGainFunc = null;
    public BiPredicate<PlayerEntity, IForm> canGainCondition = null;

    public int xpCost = 0;

    public @Nullable Identifier Icon = null;
    public @Nullable Text Name = null;
    public @Nullable Text Desc = null;

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
    public void onGain(PlayerEntity player, IForm form) {
        if (onGainFunc != null) {
            onGainFunc.accept(player, form);
        } else {
            IPerk.super.onGain(player, form);
        }
    }

    @Override
    public boolean canRepeat() {
        return repeatable;
    }

    public NormalPerk Repeat(BiConsumer<PlayerEntity, IForm> onGainFunc) {
        if (onGainFunc == null) {
            repeatable = false;
        } else {
            repeatable = true;
        }
        this.onGainFunc = onGainFunc;
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

    @Override
    public boolean canGain(PlayerEntity player, IForm form) {
        return canGainCondition == null || canGainCondition.test(player, form);
    }

    @Override
    public int getXpCost() {
        return xpCost;
    }

    public NormalPerk XpCost(int xpCost) {
        this.xpCost = xpCost;
        return this;
    }

    public NormalPerk canGain(BiPredicate<PlayerEntity, IForm> canGainCondition) {
        this.canGainCondition = canGainCondition;
        return this;
    }

    public NormalPerk setIcon(Identifier icon) {
        this.Icon = icon;
        return this;
    }

    public NormalPerk setName(Text name) {
        this.Name = name;
        return this;
    }

    public NormalPerk setDesc(Text desc) {
        this.Desc = desc;
        return this;
    }

    @Override
    public @Nullable Identifier getIcon() {
        if (Icon != null) {
            return Icon;
        }
        return IPerkClient.super.getIcon();
    }

    @Override
    public @Nullable Text getName() {
        if (Name != null) {
            return Name;
        }
        return IPerkClient.super.getName();
    }

    @Override
    public @Nullable Text getDesc() {
        if (Desc != null) {
            return Desc;
        }
        return IPerkClient.super.getDesc();
    }
}
