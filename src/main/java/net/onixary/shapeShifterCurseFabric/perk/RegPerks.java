package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RegPerks {
    public static final HashMap<Identifier, IPerk> PerkRegistry = new HashMap<>();
    public static final HashMap<Identifier, Identifier> PerkIconRegistry = new HashMap<>();
    public static final HashMap<Identifier, PerkTree> PerkTreeRegistry = new HashMap<>();
    public static final HashMap<Identifier, Text> PerkTreeNameRegistry = new HashMap<>();
    public static final HashMap<Identifier, Text> PerkNameRegistry = new HashMap<>();
    public static final HashMap<Identifier, Text> PerkDescriptionRegistry = new HashMap<>();

    public static final Identifier FALLBACK_PERK_ICON = ShapeShifterCurseFabric.identifier("textures/perk/fallback.png");
    public static final Identifier EMPTY_PERK_TREE = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("empty")));

    public static final Identifier P_FoxRoot = registerPerk(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fox_root"))
    );

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

    public static final Identifier P_Reset = registerPerk(
            new NormalPerk(ShapeShifterCurseFabric.identifier("reset"))
                    .Repeat(((player, form) -> {
                        Identifier perkTreeID = PerkUtils.getPlayerNowPerkTreeID(player);
                        List<Identifier> perks = PerkUtils.getPlayerPerks(player, perkTreeID);
                        if (perks != null) {
                            perks.clear();
                            PerkUtils.removeInValidPerk(player, perkTreeID);
                            PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
                            component.sync();
                        }
                    }))
    );

    public static final Identifier T_FFoxTree = registerPerkTree(
            new PerkTree(ShapeShifterCurseFabric.identifier("f_fox_tree"))
                    .addNode(P_FoxRoot, 0, 0, null)
                    .addNode(P_FireBallPlusL1, 1, 25, P_FoxRoot)
                    .addNode(P_FireBallPlusL2, 2, 0, P_FireBallPlusL1)
                    .addNode(P_FireArrowPlusL1, 2, 50, P_FireBallPlusL1)
                    .addNode(P_Reset, 2, -50, null)
    );

    static {
        registerPerkIcon(P_FoxRoot, ShapeShifterCurseFabric.identifier("textures/perk/fox_root.png"));
        registerPerkIcon(P_FireBallPlusL1, ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_1.png"));
        registerPerkIcon(P_FireBallPlusL2, ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_2.png"));
        registerPerkIcon(P_FireArrowPlusL1, ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_1.png"));
        registerPerkIcon(P_Reset, ShapeShifterCurseFabric.identifier("textures/perk/reset.png"));
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

    // TODO 需要加一个注册函数
    public static @NotNull Text getPerkName(Identifier perkID) {
        if (PerkNameRegistry.containsKey(perkID)) {
            return PerkNameRegistry.get(perkID);
        }
        return Text.translatable("ssc_perk." + perkID.getNamespace() + "." + perkID.getPath() + ".name");
    }

    public static @NotNull Text getPerkDescription(Identifier perkID) {
        if (PerkDescriptionRegistry.containsKey(perkID)) {
            return PerkDescriptionRegistry.get(perkID);
        }
        return Text.translatable("ssc_perk." + perkID.getNamespace() + "." + perkID.getPath() + ".description");
    }
}
