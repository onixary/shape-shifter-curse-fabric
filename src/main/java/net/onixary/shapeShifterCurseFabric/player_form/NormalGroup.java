package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalGroup implements IFormGroup {
    public final Identifier id;
    public Map<Integer, List<Pair<Integer, IForm>>> groupData = new HashMap<>();

    public NormalGroup(Identifier id) {
        this.id = id;
    }

    @Override
    public @NotNull Identifier getGroupID() {
        return this.id;
    }

    @Override
    public @NotNull Map<Integer, List<Pair<Integer, IForm>>> getGroupData() {
        return groupData;
    }
}
