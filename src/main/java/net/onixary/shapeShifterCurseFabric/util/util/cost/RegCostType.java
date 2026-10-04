package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class RegCostType {
    public static final HashMap<Identifier, ICostType<?>> costTypes = new HashMap<>();
    public static final HashMap<Identifier, Function<NbtCompound, ICost>> costs = new HashMap<>();

    public static final EmptyCostType NO_COST = register(new EmptyCostType());
    public static final XPCostType COST_XP = register(new XPCostType());
    public static final ItemCostType COST_ITEM = register(new ItemCostType());

    static {
        costs.put(BaseCost.id, BaseCost::new);
        costs.put(ItemCost.id, ItemCost::new);
    }

    public static <T extends ICostType<T>> T register(@NotNull T costType) {
        Identifier id = costType.getID();
        costTypes.put(id, costType);
        return costType;
    }

    public static @Nullable ICostType<?> getCostType(Identifier id) {
        return costTypes.get(id);
    }
}
