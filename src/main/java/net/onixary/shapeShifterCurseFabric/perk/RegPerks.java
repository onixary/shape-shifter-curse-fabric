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
    public static final HashMap<Identifier, PerkTree> PerkTreeRegistry = new HashMap<>();
    public static final HashMap<Identifier, IPerkClient> PerkClientRegistry = new HashMap<>();
    public static final HashMap<Identifier, Text> PerkTreeNameRegistry = new HashMap<>();

    public static final Identifier FALLBACK_PERK_ICON = ShapeShifterCurseFabric.identifier("textures/perk/fallback.png");
    public static final Identifier EMPTY_PERK_TREE = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("empty")));

    public static final Identifier P_FoxRoot = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fox_root"))
                    .addPower(ShapeShifterCurseFabric.identifier("_test_perk01"))
                    .removePower(ShapeShifterCurseFabric.identifier("form_familiar_fox_3_health"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fox_root.png"))
    );

    public static final Identifier P_FireBallPlusL1 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_1"))
                    .addPower()
                    .removePower(ShapeShifterCurseFabric.identifier("_test_perk01"))
                    .setName(Text.literal("Fire Ball Lv1"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_1.png"))
    );

    public static final Identifier P_FireBallPlusL2 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_2"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Ball Lv2"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_2.png"))
    );

    public static final Identifier P_FireArrowPlusL1 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_arrow_plus_1"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Arrow Lv1"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_1.png"))
    );

    public static final Identifier P_FireArrowPlusL2 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_arrow_plus_2"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Arrow Lv2"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_2.png"))
    );

    // 注意一下 Perk不可删除的 这个只是调试用的 没做Power还原
    public static final Identifier P_Reset = registerPerkCommon(
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
                        player.sendMessage(Text.literal("Perks reset!"), false);
                    }))
                    .canGain((player, form) -> {
                        List<Identifier> perks = PerkUtils.getPlayerPerks(player, PerkUtils.getPlayerNowPerkTreeID(player));
                        return perks != null && !perks.isEmpty();
                    })
                    .setName(Text.literal("RESET"))
                    .setDesc(Text.literal("Reset all perks!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/reset.png"))
    );

    public static final Identifier P_Reset_DEBUG = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("reset_debug"))
                    .Repeat(((player, form) -> {
                        Identifier perkTreeID = PerkUtils.getPlayerNowPerkTreeID(player);
                        List<Identifier> perks = PerkUtils.getPlayerPerks(player, perkTreeID);
                        if (perks != null) {
                            perks.clear();
                            PerkUtils.removeInValidPerk(player, perkTreeID);
                            PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
                            component.sync();
                        }
                        player.sendMessage(Text.literal("Perks reset!"), false);
                    }))
                    .canGain((player, form) -> {
                        List<Identifier> perks = PerkUtils.getPlayerPerks(player, PerkUtils.getPlayerNowPerkTreeID(player));
                        return perks != null && !perks.isEmpty();
                    })
                    .setName(Text.literal("RESET_DEBUG"))
                    .setDesc(Text.literal("Reset all perks! Only for DEBUG!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/reset.png"))
    );

    public static final Identifier T_FFoxTree = registerPerkTree(
            new PerkTree(ShapeShifterCurseFabric.identifier("f_fox_tree"))
                    .addNode(P_FoxRoot, 0, 0)
                    .addNode(P_Reset_DEBUG, 0, -50)
                    .addNode(P_FireBallPlusL1, 1, 25, P_FoxRoot)
                    .addNode(P_FireBallPlusL2, 2, 0, P_FireBallPlusL1)
                    .addNode(P_FireArrowPlusL1, 2, 50, P_FireBallPlusL1)
                    .addNode(P_FireArrowPlusL2, 3, 25, P_FireBallPlusL2, P_FireArrowPlusL1)
                    .addNode(P_Reset, 2, -50, P_FoxRoot)
    );

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

    public static void registerPerkClientData(IPerkClient perkClient) {
        PerkClientRegistry.put(perkClient.getID(), perkClient);
    }

    public static @Nullable IPerkClient getPerkClientData(Identifier perkID) {
        return PerkClientRegistry.get(perkID);
    }

    public static <PERK extends IPerk & IPerkClient> Identifier registerPerkCommon(PERK perk) {
        PerkRegistry.put(perk.getID(), perk);
        PerkClientRegistry.put(perk.getID(), perk);
        return perk.getID();
    }


    public static @Nullable Identifier getPerkIcon(Identifier perkID) {
        IPerkClient perk = getPerkClientData(perkID);
        return perk != null ? perk.getIcon() : null;
    }

    public static @NotNull Text getPerkName(Identifier perkID) {
        IPerkClient perk = getPerkClientData(perkID);
        return perk != null ? perk.getName() : IPerkClient.getDefaultName(perkID);
    }

    public static @NotNull Text getPerkDescription(Identifier perkID) {
        IPerkClient perk = getPerkClientData(perkID);
        return perk != null ? perk.getDesc() : IPerkClient.getDefaultDesc(perkID);
    }
}
