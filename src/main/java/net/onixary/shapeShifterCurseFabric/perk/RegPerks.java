package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegPerks {
    public static final HashMap<Identifier, IPerk> PerkRegistry = new HashMap<>();
    public static final HashMap<Identifier, Identifier> PerkIconRegistry = new HashMap<>();
    public static final HashMap<Identifier, PerkTree> PerkTreeRegistry = new HashMap<>();

    public static final Identifier FALLBACK_PERK_ICON = ShapeShifterCurseFabric.identifier("textures/perk/fallback.png");

    public static final Identifier P_FireBallPlusL1 = registerPerk(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_1"))
                    .addPower()
                    .removePower()
    );

    public static final Identifier P_FireBallPlusL2 = registerPerk(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_2"))
                    .addPower()
                    .removePower()
    );

    public static final Identifier P_FireArrowPlusL1 = registerPerk(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_arrow_plus_1"))
                    .addPower()
                    .removePower()
    );

    public static final Identifier T_FFoxTree = registerPerkTree(
            new PerkTree(ShapeShifterCurseFabric.identifier("f_fox_tree"))
                    .addNode(P_FireBallPlusL1, 1, 0, null)
                    .addNode(P_FireBallPlusL2, 2, -25, ShapeShifterCurseFabric.identifier("fire_ball_plus_1"))
                    .addNode(P_FireArrowPlusL1, 2, 25, ShapeShifterCurseFabric.identifier("fire_ball_plus_1"))
    );

    static {
        registerPerkIcon(P_FireBallPlusL1, ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_1.png"));
        registerPerkIcon(P_FireBallPlusL2, ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_2.png"));
        registerPerkIcon(P_FireArrowPlusL1, ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_1.png"));
    }

    public static Identifier registerPerk(IPerk perk) {
        PerkRegistry.put(perk.getID(), perk);
        return perk.getID();
    }

    public static @Nullable IPerk getPerk(Identifier perkID) {
        return PerkRegistry.get(perkID);
    }

    public static Identifier registerPerkTree(PerkTree perkTree) {
        PerkTreeRegistry.put(perkTree.getID(), perkTree);
        return perkTree.getID();
    }

    public static @Nullable PerkTree getPerkTree(Identifier perkTreeID) {
        return PerkTreeRegistry.get(perkTreeID);
    }

    public static void registerPerkIcon(Identifier perkID, Identifier iconID) {
        PerkIconRegistry.put(perkID, iconID);
    }

    public static @Nullable Identifier getPerkIcon(Identifier perkID) {
        return PerkIconRegistry.get(perkID);
    }
}
