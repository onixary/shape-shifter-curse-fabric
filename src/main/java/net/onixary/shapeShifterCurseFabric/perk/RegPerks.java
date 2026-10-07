package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.util.util.cost.BaseCost;
import net.onixary.shapeShifterCurseFabric.util.util.cost.ICost;
import net.onixary.shapeShifterCurseFabric.util.util.cost.ItemCost;
import net.onixary.shapeShifterCurseFabric.util.util.cost.RegCostType;
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

    private static final ItemStack moonDust = new ItemStack(RegCustomItem.UNTREATED_MOONDUST);
    static {
        moonDust.getOrCreateNbt().putBoolean("Unbreakable", true);
    }

    public static final Identifier P_FoxRoot = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fox_root"))
                    .addPower(ShapeShifterCurseFabric.identifier("_test_perk01"))
                    .removePower(ShapeShifterCurseFabric.identifier("form_familiar_fox_3_health"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fox_root.png"))
                    .cost(new ItemCost(RegCostType.COST_ITEM, moonDust, 16))
    );

    public static final Identifier P_FireBallPlusL1 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_1"))
                    .addPower()
                    .removePower(ShapeShifterCurseFabric.identifier("_test_perk01"))
                    .setName(Text.literal("Fire Ball Lv1"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_1.png"))
                    .cost(new BaseCost(RegCostType.COST_XP, 6000))
    );

    public static final Identifier P_FireBallPlusL2 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_ball_plus_2"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Ball Lv2"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_ball_plus_2.png"))
                    .cost(new BaseCost(RegCostType.COST_XP, 9000))
    );

    public static final Identifier P_FireArrowPlusL1 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_arrow_plus_1"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Arrow Lv1"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_1.png"))
                    .cost(new BaseCost(RegCostType.COST_XP, 9000))
    );

    public static final Identifier P_FireArrowPlusL2 = registerPerkCommon(
            new NormalPerk(ShapeShifterCurseFabric.identifier("fire_arrow_plus_2"))
                    .addPower()
                    .removePower()
                    .setName(Text.literal("Fire Arrow Lv2"))
                    .setDesc(Text.literal("Just A Example Perk!"))
                    .setIcon(ShapeShifterCurseFabric.identifier("textures/perk/fire_arrow_plus_2.png"))
                    .cost(new BaseCost(RegCostType.COST_XP, 12000))
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
    // SSC Studio: begin perk registrations
    public static final Identifier SSC_P_shape_shifter_curse_snowfox_root = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snowfox_root"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snowfox_root.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snowfox_root.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 0))
            .addPower().removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_posture_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_posture_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_posture_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_posture_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_posture_1")).removePower(new Identifier("shape-shifter-curse:form_bat_3_ground_speed_down")));
    public static final Identifier SSC_P_shape_shifter_curse_bat_posture_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_posture_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_posture_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_posture_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_posture_2")).removePower(new Identifier("shape-shifter-curse:form_bat_3_ground_speed_down"), new Identifier("shape-shifter-curse:perks/bat_posture_1")));
    public static final Identifier SSC_P_shape_shifter_curse_bat_echolocation = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_echolocation"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_echolocation.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_echolocation.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_echolocation")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_wingbeat = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_wingbeat"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_wingbeat.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_wingbeat.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_wingbeat")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_arrow_throw = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_arrow_throw"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_arrow_throw.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_arrow_throw.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_arrow_throw")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_air_blast = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_air_blast"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_air_blast.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_air_blast.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_air_blast")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_sun_resistance_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_sun_resistance_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_sun_resistance_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_sun_resistance_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_sun_resistance_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_bat_sun_resistance_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_sun_resistance_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_sun_resistance_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_sun_resistance_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_sun_resistance_2")).removePower(new Identifier("shape-shifter-curse:perks/bat_sun_resistance_1")));
    public static final Identifier SSC_P_shape_shifter_curse_bat_night_veil = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:bat_night_veil"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.bat_night_veil.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.bat_night_veil.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/bat_night_veil")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_vegetation = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_vegetation"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_vegetation.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_vegetation.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_vegetation")).removePower(new Identifier("shape-shifter-curse:form_axolotl_3_ground_speed_down")));
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_dry_tolerance = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_dry_tolerance"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_dry_tolerance.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_dry_tolerance.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 250))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_dry_tolerance")).removePower(new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_0"), new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_1"), new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_2"), new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_3"), new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_4"), new Identifier("shape-shifter-curse:form_axolotl_2_new_oxygen_health_5")));
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_tidal_pull = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_tidal_pull"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_tidal_pull.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_tidal_pull.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_tidal_pull")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_moisture_return_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_moisture_return_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_moisture_return_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_moisture_return_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_moisture_return_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_moisture_return_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_moisture_return_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_moisture_return_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_moisture_return_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_moisture_return_2")).removePower(new Identifier("shape-shifter-curse:perks/axolotl_moisture_return_1")));
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_collect_water = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_collect_water"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_collect_water.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_collect_water.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_collect_water")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_propulsion_efficiency = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_propulsion_efficiency"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_propulsion_efficiency.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_propulsion_efficiency.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_propulsion_efficiency")).removePower(new Identifier("shape-shifter-curse:form_axolotl_3_sprinting_jump")));
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_wave_efficiency = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_wave_efficiency"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_wave_efficiency.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_wave_efficiency.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_wave_efficiency")).removePower(new Identifier("shape-shifter-curse:form_axolotl_3_sprinting_cost_oxygen")));
    public static final Identifier SSC_P_shape_shifter_curse_axolotl_water_magic_efficiency = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:axolotl_water_magic_efficiency"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_water_magic_efficiency.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.axolotl_water_magic_efficiency.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 500))
            .addPower(new Identifier("shape-shifter-curse:perks/axolotl_water_magic_efficiency")).removePower(new Identifier("shape-shifter-curse:form_axolotl_3_sprinting_attack"), new Identifier("shape-shifter-curse:form_axolotl_3_sprinting_sneaking_water_explode")));
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_armor_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_armor_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_armor_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_armor_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_armor_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_armor_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_armor_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_armor_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_armor_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_armor_2")).removePower(new Identifier("shape-shifter-curse:perks/ocelot_armor_1")));
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_metabolism_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_metabolism_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolism_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolism_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_metabolism_1")).removePower(new Identifier("shape-shifter-curse:form_ocelot_3_hunger")));
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_metabolic_overload = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_metabolic_overload"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolic_overload.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolic_overload.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_metabolic_overload")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_metabolism_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_metabolism_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolism_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_metabolism_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_metabolism_2")).removePower(new Identifier("shape-shifter-curse:more_exhaustion")));
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_ambush = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_ambush"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_ambush.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_ambush.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_ambush")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_hungry_pounce = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_hungry_pounce"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_hungry_pounce.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_hungry_pounce.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_hungry_pounce")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_ocelot_long_pounce = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:ocelot_long_pounce"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_long_pounce.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.ocelot_long_pounce.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/ocelot_long_pounce")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_deflection = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_deflection"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_deflection.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_deflection.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_deflection")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_return_shield = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_return_shield"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_return_shield.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_return_shield.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_return_shield")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_siphon_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_siphon_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphon_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphon_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_siphon_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_siphon_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_siphon_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphon_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphon_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 250))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_siphon_2")).removePower(new Identifier("shape-shifter-curse:perks/familiar_fox_siphon_1")));
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_siphoning_ring = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_siphoning_ring"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphoning_ring.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_siphoning_ring.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_siphoning_ring")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_mana_capacity_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_mana_capacity_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_mana_capacity_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_mana_capacity_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_mana_capacity_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_mana_capacity_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_mana_capacity_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_mana_capacity_2")).removePower(new Identifier("shape-shifter-curse:perks/familiar_fox_mana_capacity_1")));
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_reservoir = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_reservoir"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_reservoir.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_reservoir.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_reservoir")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_permeable_field_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_permeable_field_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_permeable_field_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_permeable_field_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_permeable_field_1")).removePower(new Identifier("shape-shifter-curse:form_familiar_fox_3_no_buff_effect")));
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_permeable_field_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_permeable_field_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_permeable_field_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_permeable_field_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_permeable_field_2")).removePower(new Identifier("shape-shifter-curse:form_familiar_fox_3_no_buff_effect"), new Identifier("shape-shifter-curse:perks/familiar_fox_permeable_field_1")));
    public static final Identifier SSC_P_shape_shifter_curse_familiar_fox_potion_charms = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:familiar_fox_potion_charms"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_potion_charms.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.familiar_fox_potion_charms.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/familiar_fox_potion_charms")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_revenge = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_revenge"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_revenge.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_revenge.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_revenge")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_elusive_paws = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_elusive_paws"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_elusive_paws.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_elusive_paws.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_elusive_paws")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_air_jump = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_air_jump"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_air_jump.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_air_jump.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_air_jump")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_frost_dive = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_frost_dive"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_frost_dive.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_frost_dive.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_frost_dive")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_cold_whirlwind_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_cold_whirlwind_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_whirlwind_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_whirlwind_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_cold_whirlwind_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_cold_whirlwind_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_cold_whirlwind_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_whirlwind_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_whirlwind_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_cold_whirlwind_2")).removePower(new Identifier("shape-shifter-curse:perks/snow_fox_cold_whirlwind_1")));
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_fire_training_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_fire_training_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_fire_training_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_fire_training_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower().removePower(new Identifier("shape-shifter-curse:form_snow_fox_3_near_lava_damage"), new Identifier("shape-shifter-curse:form_snow_fox_3_near_fire_damage")));
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_cold_recovery_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_cold_recovery_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_recovery_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_recovery_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_cold_recovery_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_cold_recovery_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_cold_recovery_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_recovery_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_cold_recovery_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/snow_fox_cold_recovery_2")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_snow_fox_fire_training_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:snow_fox_fire_training_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_fire_training_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.snow_fox_fire_training_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower().removePower(new Identifier("shape-shifter-curse:form_snow_fox_3_burn_damage_up")));
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_pack_response_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_pack_response_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_response_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_response_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_pack_response_1"), new Identifier("shape-shifter-curse:perks/anubis_wolf_summoning_1")).removePower(new Identifier("shape-shifter-curse:form_anubis_wolf_3_summon_wolf_on_hit"), new Identifier("shape-shifter-curse:form_anubis_wolf_3_summon_wolf_when_hit")));
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_soul_excitation = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_soul_excitation"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_soul_excitation.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_soul_excitation.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_soul_excitation")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_pack_response_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_pack_response_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_response_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_response_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_summoning_2")).removePower(new Identifier("shape-shifter-curse:perks/anubis_wolf_summoning_1"), new Identifier("shape-shifter-curse:form_anubis_wolf_3_summon_wolf_on_hit"), new Identifier("shape-shifter-curse:form_anubis_wolf_3_summon_wolf_when_hit")));
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_pack_amplification = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_pack_amplification"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_amplification.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_pack_amplification.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_pack_amplification")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_wither_tolerance_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_wither_tolerance_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_wither_tolerance_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_wither_tolerance_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_wither_tolerance_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_wither_tolerance_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_wither_tolerance_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_wither_tolerance_2")).removePower(new Identifier("shape-shifter-curse:perks/anubis_wolf_wither_tolerance_1")));
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_soul_recall = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_soul_recall"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_soul_recall.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_soul_recall.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/anubis_wolf_soul_recall")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_anubis_wolf_undead_discernment = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:anubis_wolf_undead_discernment"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_undead_discernment.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.anubis_wolf_undead_discernment.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower().removePower(new Identifier("shape-shifter-curse:form_anubis_wolf_3_undead_damage_down")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_four_legged_adaptation = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_four_legged_adaptation"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_four_legged_adaptation.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_four_legged_adaptation.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_four_legged_adaptation")).removePower(new Identifier("shape-shifter-curse:form_spider_3_speed_down")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_cocoon_digestion_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_cocoon_digestion_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_cocoon_digestion_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_cocoon_digestion_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_cocoon_digestion_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_spider_cocoon_digestion_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_cocoon_digestion_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_cocoon_digestion_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_cocoon_digestion_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_cocoon_digestion_2")).removePower(new Identifier("shape-shifter-curse:perks/spider_cocoon_digestion_1")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_bridge_weaver = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_bridge_weaver"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_bridge_weaver.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_bridge_weaver.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_bridge_weaver")).removePower(new Identifier("shape-shifter-curse:form_spider_3_web_bridge")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_perceptual_web = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_perceptual_web"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_perceptual_web.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_perceptual_web.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_perceptual_web")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_spider_rapid_spinner = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_rapid_spinner"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_rapid_spinner.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_rapid_spinner.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_rapid_spinner")).removePower(new Identifier("shape-shifter-curse:form_spider_3_web_projectile")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_stabilized_projectile = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_stabilized_projectile"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_stabilized_projectile.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_stabilized_projectile.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_stabilized_projectile")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_spider_silk_secretion_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_silk_secretion_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_secretion_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_secretion_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_silk_secretion_1")).removePower(new Identifier("shape-shifter-curse:form_spider_3_mana_recover")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_silk_secretion_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_silk_secretion_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_secretion_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_secretion_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 300))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_silk_secretion_2")).removePower(new Identifier("shape-shifter-curse:form_spider_3_mana_recover"), new Identifier("shape-shifter-curse:perks/spider_silk_secretion_1")));
    public static final Identifier SSC_P_shape_shifter_curse_spider_silk_grapple = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:spider_silk_grapple"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_grapple.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.spider_silk_grapple.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/spider_silk_grapple")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_double_air_jump = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_double_air_jump"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_double_air_jump.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_double_air_jump.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_double_air_jump")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_precise_momentum = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_precise_momentum"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_precise_momentum.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_precise_momentum.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_precise_momentum")).removePower(new Identifier("shape-shifter-curse:form_snow_fox_3_enhanced_falling_attack")));
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_leaping_dash = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_leaping_dash"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_leaping_dash.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_leaping_dash.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 1))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_leaping_dash")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_hunter_metabolism = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_hunter_metabolism"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_hunter_metabolism.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_hunter_metabolism.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_hunter_metabolism")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_combat_noodle = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_combat_noodle"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_combat_noodle.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_combat_noodle.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_combat_noodle")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_marbled_polecat_keen_scent = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:marbled_polecat_keen_scent"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_keen_scent.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.marbled_polecat_keen_scent.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/marbled_polecat_keen_scent")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_avali_nano_coating_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_nano_coating_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_nano_coating_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_nano_coating_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_nano_coating_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_avali_nano_coating_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_nano_coating_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_nano_coating_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_nano_coating_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_nano_coating_2")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_avali_environmental_protection_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_environmental_protection_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_environmental_protection_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_environmental_protection_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_environmental_protection_1")).removePower(new Identifier("shape-shifter-curse:sub_form_avali_water_slowness")));
    public static final Identifier SSC_P_shape_shifter_curse_avali_environmental_protection_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_environmental_protection_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_environmental_protection_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_environmental_protection_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower().removePower(new Identifier("shape-shifter-curse:sub_form_avali_hot_health_down")));
    public static final Identifier SSC_P_shape_shifter_curse_avali_tool_modification_1 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_tool_modification_1"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_tool_modification_1.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_tool_modification_1.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_tool_modification_1")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_avali_tool_modification_2 = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_tool_modification_2"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_tool_modification_2.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_tool_modification_2.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_tool_modification_2")).removePower());
    public static final Identifier SSC_P_shape_shifter_curse_avali_emergency_protocol = registerPerkCommon(new NormalPerk(new Identifier("shape-shifter-curse:avali_emergency_protocol"))
            .setName(Text.translatable("ssc_perk.shape-shifter-curse.avali_emergency_protocol.name")).setDesc(Text.translatable("ssc_perk.shape-shifter-curse.avali_emergency_protocol.desc"))
            .setIcon(new Identifier("shape-shifter-curse:textures/perk/fallback.png")).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(net.minecraft.registry.Registries.ITEM.get(new Identifier("shape-shifter-curse:glint_prism"))), 2))
            .addPower(new Identifier("shape-shifter-curse:perks/avali_emergency_protocol")).removePower());
    public static final Identifier SSC_T_shape_shifter_curse_f_snowfox_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:f_snowfox_tree"))
            .addNode(new Identifier("shape-shifter-curse:snowfox_root"), 0, 0)
    );
    public static final Identifier SSC_T_shape_shifter_curse_bat_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:bat_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:bat_posture_1"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:bat_posture_2"), 2, 0, new Identifier("shape-shifter-curse:bat_posture_1"))
            .addNode(new Identifier("shape-shifter-curse:bat_echolocation"), 3, 0, new Identifier("shape-shifter-curse:bat_posture_2"))
            .addNode(new Identifier("shape-shifter-curse:bat_wingbeat"), 4, 0, new Identifier("shape-shifter-curse:bat_echolocation"))
            .addNode(new Identifier("shape-shifter-curse:bat_arrow_throw"), 2, 60, new Identifier("shape-shifter-curse:bat_posture_1"))
            .addNode(new Identifier("shape-shifter-curse:bat_air_blast"), 4, 60, new Identifier("shape-shifter-curse:bat_arrow_throw"))
            .addNode(new Identifier("shape-shifter-curse:bat_sun_resistance_1"), 1, 140)
            .addNode(new Identifier("shape-shifter-curse:bat_sun_resistance_2"), 2, 140, new Identifier("shape-shifter-curse:bat_sun_resistance_1"))
            .addNode(new Identifier("shape-shifter-curse:bat_night_veil"), 2, 200, new Identifier("shape-shifter-curse:bat_sun_resistance_1"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_axolotl_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:axolotl_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_vegetation"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:axolotl_dry_tolerance"), 2, 0, new Identifier("shape-shifter-curse:axolotl_vegetation"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_tidal_pull"), 2, 60, new Identifier("shape-shifter-curse:axolotl_vegetation"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_moisture_return_1"), 1, 140)
            .addNode(new Identifier("shape-shifter-curse:axolotl_moisture_return_2"), 2, 140, new Identifier("shape-shifter-curse:axolotl_moisture_return_1"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_collect_water"), 4, 140, new Identifier("shape-shifter-curse:axolotl_moisture_return_2"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_propulsion_efficiency"), 1, 240)
            .addNode(new Identifier("shape-shifter-curse:axolotl_wave_efficiency"), 2, 240, new Identifier("shape-shifter-curse:axolotl_propulsion_efficiency"))
            .addNode(new Identifier("shape-shifter-curse:axolotl_water_magic_efficiency"), 3, 240, new Identifier("shape-shifter-curse:axolotl_wave_efficiency"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_ocelot_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:ocelot_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:ocelot_armor_1"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:ocelot_armor_2"), 2, 0, new Identifier("shape-shifter-curse:ocelot_armor_1"))
            .addNode(new Identifier("shape-shifter-curse:ocelot_metabolism_1"), 1, 80)
            .addNode(new Identifier("shape-shifter-curse:ocelot_metabolic_overload"), 2, 80, new Identifier("shape-shifter-curse:ocelot_metabolism_1"))
            .addNode(new Identifier("shape-shifter-curse:ocelot_metabolism_2"), 3, 140, new Identifier("shape-shifter-curse:ocelot_metabolism_1"))
            .addNode(new Identifier("shape-shifter-curse:ocelot_ambush"), 2, 220)
            .addNode(new Identifier("shape-shifter-curse:ocelot_hungry_pounce"), 3, 220, new Identifier("shape-shifter-curse:ocelot_ambush"))
            .addNode(new Identifier("shape-shifter-curse:ocelot_long_pounce"), 3, 280, new Identifier("shape-shifter-curse:ocelot_ambush"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_familiar_fox_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:familiar_fox_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_deflection"), 2, 0)
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_return_shield"), 3, 0, new Identifier("shape-shifter-curse:familiar_fox_deflection"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_siphon_1"), 1, 80)
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_siphon_2"), 3, 80, new Identifier("shape-shifter-curse:familiar_fox_siphon_1"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_siphoning_ring"), 4, 80, new Identifier("shape-shifter-curse:familiar_fox_siphon_2"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_1"), 1, 160)
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_2"), 3, 160, new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_1"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_reservoir"), 2, 160, new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_1"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_permeable_field_1"), 2, 240, new Identifier("shape-shifter-curse:familiar_fox_mana_capacity_1"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_permeable_field_2"), 3, 240, new Identifier("shape-shifter-curse:familiar_fox_permeable_field_1"))
            .addNode(new Identifier("shape-shifter-curse:familiar_fox_potion_charms"), 3, 320)
    );
    public static final Identifier SSC_T_shape_shifter_curse_snow_fox_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:snow_fox_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_revenge"), 2, 0)
            .addNode(new Identifier("shape-shifter-curse:snow_fox_elusive_paws"), 1, 80)
            .addNode(new Identifier("shape-shifter-curse:snow_fox_air_jump"), 2, 80, new Identifier("shape-shifter-curse:snow_fox_elusive_paws"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_frost_dive"), 4, 80, new Identifier("shape-shifter-curse:snow_fox_air_jump"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_cold_whirlwind_1"), 2, 160, new Identifier("shape-shifter-curse:snow_fox_elusive_paws"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_cold_whirlwind_2"), 3, 160, new Identifier("shape-shifter-curse:snow_fox_cold_whirlwind_1"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_fire_training_1"), 1, 240)
            .addNode(new Identifier("shape-shifter-curse:snow_fox_cold_recovery_1"), 2, 240, new Identifier("shape-shifter-curse:snow_fox_fire_training_1"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_cold_recovery_2"), 3, 240, new Identifier("shape-shifter-curse:snow_fox_cold_recovery_1"))
            .addNode(new Identifier("shape-shifter-curse:snow_fox_fire_training_2"), 2, 320, new Identifier("shape-shifter-curse:snow_fox_fire_training_1"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_anubis_wolf_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:anubis_wolf_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_pack_response_1"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_soul_excitation"), 2, 0, new Identifier("shape-shifter-curse:anubis_wolf_pack_response_1"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_pack_response_2"), 3, 80, new Identifier("shape-shifter-curse:anubis_wolf_pack_response_1"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_pack_amplification"), 4, 80, new Identifier("shape-shifter-curse:anubis_wolf_pack_response_2"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_1"), 1, 160)
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_2"), 3, 160, new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_1"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_soul_recall"), 2, 240, new Identifier("shape-shifter-curse:anubis_wolf_wither_tolerance_1"))
            .addNode(new Identifier("shape-shifter-curse:anubis_wolf_undead_discernment"), 1, 320)
    );
    public static final Identifier SSC_T_shape_shifter_curse_spider_3_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:spider_3_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:spider_four_legged_adaptation"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:spider_cocoon_digestion_1"), 1, 80)
            .addNode(new Identifier("shape-shifter-curse:spider_cocoon_digestion_2"), 2, 80, new Identifier("shape-shifter-curse:spider_cocoon_digestion_1"))
            .addNode(new Identifier("shape-shifter-curse:spider_bridge_weaver"), 2, 160)
            .addNode(new Identifier("shape-shifter-curse:spider_perceptual_web"), 3, 160, new Identifier("shape-shifter-curse:spider_bridge_weaver"))
            .addNode(new Identifier("shape-shifter-curse:spider_rapid_spinner"), 3, 240, new Identifier("shape-shifter-curse:spider_bridge_weaver"))
            .addNode(new Identifier("shape-shifter-curse:spider_stabilized_projectile"), 4, 240, new Identifier("shape-shifter-curse:spider_rapid_spinner"))
            .addNode(new Identifier("shape-shifter-curse:spider_silk_secretion_1"), 1, 320)
            .addNode(new Identifier("shape-shifter-curse:spider_silk_secretion_2"), 3, 320, new Identifier("shape-shifter-curse:spider_silk_secretion_1"))
            .addNode(new Identifier("shape-shifter-curse:spider_silk_grapple"), 2, 400, new Identifier("shape-shifter-curse:spider_silk_secretion_1"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_marbled_polecat_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:marbled_polecat_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_double_air_jump"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_precise_momentum"), 2, 0, new Identifier("shape-shifter-curse:marbled_polecat_double_air_jump"))
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_leaping_dash"), 3, 80, new Identifier("shape-shifter-curse:marbled_polecat_double_air_jump"))
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_hunter_metabolism"), 1, 160)
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_combat_noodle"), 2, 160, new Identifier("shape-shifter-curse:marbled_polecat_hunter_metabolism"))
            .addNode(new Identifier("shape-shifter-curse:marbled_polecat_keen_scent"), 2, 240, new Identifier("shape-shifter-curse:marbled_polecat_hunter_metabolism"))
    );
    public static final Identifier SSC_T_shape_shifter_curse_avali_perk_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:avali_perk_tree"))
            .addNode(new Identifier("shape-shifter-curse:avali_nano_coating_1"), 1, 0)
            .addNode(new Identifier("shape-shifter-curse:avali_nano_coating_2"), 2, 0, new Identifier("shape-shifter-curse:avali_nano_coating_1"))
            .addNode(new Identifier("shape-shifter-curse:avali_environmental_protection_1"), 1, 80)
            .addNode(new Identifier("shape-shifter-curse:avali_environmental_protection_2"), 2, 80, new Identifier("shape-shifter-curse:avali_environmental_protection_1"))
            .addNode(new Identifier("shape-shifter-curse:avali_tool_modification_1"), 2, 160)
            .addNode(new Identifier("shape-shifter-curse:avali_tool_modification_2"), 3, 160, new Identifier("shape-shifter-curse:avali_tool_modification_1"))
            .addNode(new Identifier("shape-shifter-curse:avali_emergency_protocol"), 3, 240)
    );
    // SSC Studio: end perk registrations
}
