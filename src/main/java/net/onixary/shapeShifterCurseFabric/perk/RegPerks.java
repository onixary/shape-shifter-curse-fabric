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

    public static final Identifier FALLBACK_PERK_ICON = ShapeShifterCurseFabric.identifier("textures/perk/fallback.png");
    public static final Identifier EMPTY_PERK_TREE = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("empty")));

    public static final Identifier P_BatPosture_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_posture_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_posture_1")).removePower(ShapeShifterCurseFabric.identifier("form_bat_3_ground_speed_down")));
    public static final Identifier P_BatPosture_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_posture_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_posture_2")).removePower(ShapeShifterCurseFabric.identifier("form_bat_3_ground_speed_down"), ShapeShifterCurseFabric.identifier("perks/bat_posture_1")));
    public static final Identifier P_BatEcholocation = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_echolocation"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_echolocation")).removePower());
    public static final Identifier P_BatWingbeat = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_wingbeat"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_wingbeat")).removePower());
    public static final Identifier P_BatArrowThrow = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_arrow_throw"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_arrow_throw")).removePower());
    public static final Identifier P_BatAirBlast = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_air_blast"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_air_blast")).removePower());
    public static final Identifier P_BatSunResistance_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_sun_resistance_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_sun_resistance_1")).removePower());
    public static final Identifier P_BatSunResistance_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_sun_resistance_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_sun_resistance_2")).removePower(ShapeShifterCurseFabric.identifier("perks/bat_sun_resistance_1")));
    public static final Identifier P_BatNightVeil = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("bat_night_veil"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/bat_night_veil")).removePower());
    public static final Identifier P_AxolotlVegetation = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_vegetation"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_vegetation")).removePower(ShapeShifterCurseFabric.identifier("form_axolotl_3_ground_speed_down")));
    public static final Identifier P_AxolotlDryTolerance = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_dry_tolerance"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 250))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_dry_tolerance")).removePower(ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_0"), ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_1"), ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_2"), ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_3"), ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_4"), ShapeShifterCurseFabric.identifier("form_axolotl_2_new_oxygen_health_5")));
    public static final Identifier P_AxolotlTidalPull = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_tidal_pull"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_tidal_pull")).removePower());
    public static final Identifier P_AxolotlMoistureReturn_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_moisture_return_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_moisture_return_1")).removePower());
    public static final Identifier P_AxolotlMoistureReturn_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_moisture_return_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_moisture_return_2")).removePower(ShapeShifterCurseFabric.identifier("perks/axolotl_moisture_return_1")));
    public static final Identifier P_AxolotlCollectWater = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_collect_water"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_collect_water")).removePower());
    public static final Identifier P_AxolotlPropulsionEfficiency = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_propulsion_efficiency"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_propulsion_efficiency")).removePower(ShapeShifterCurseFabric.identifier("form_axolotl_3_sprinting_jump")));
    public static final Identifier P_AxolotlWaveEfficiency = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_wave_efficiency"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_wave_efficiency")).removePower(ShapeShifterCurseFabric.identifier("form_axolotl_3_sprinting_cost_oxygen")));
    public static final Identifier P_AxolotlWaterMagicEfficiency = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("axolotl_water_magic_efficiency"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 500))
            .addPower(ShapeShifterCurseFabric.identifier("perks/axolotl_water_magic_efficiency")).removePower(ShapeShifterCurseFabric.identifier("form_axolotl_3_sprinting_attack"), ShapeShifterCurseFabric.identifier("form_axolotl_3_sprinting_sneaking_water_explode")));
    public static final Identifier P_OcelotArmor_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_armor_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_armor_1")).removePower());
    public static final Identifier P_OcelotArmor_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_armor_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_armor_2")).removePower(ShapeShifterCurseFabric.identifier("perks/ocelot_armor_1")));
    public static final Identifier P_OcelotMetabolism_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_metabolism_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_metabolism_1")).removePower(ShapeShifterCurseFabric.identifier("form_ocelot_3_hunger")));
    public static final Identifier P_OcelotMetabolicOverload = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_metabolic_overload"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_metabolic_overload")).removePower());
    public static final Identifier P_OcelotMetabolism_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_metabolism_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_metabolism_2")).removePower(ShapeShifterCurseFabric.identifier("more_exhaustion")));
    public static final Identifier P_OcelotAmbush = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_ambush"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_ambush")).removePower());
    public static final Identifier P_OcelotHungryPounce = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_hungry_pounce"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_hungry_pounce")).removePower());
    public static final Identifier P_OcelotLongPounce = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("ocelot_long_pounce"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/ocelot_long_pounce")).removePower());
    public static final Identifier P_FamiliarFoxDeflection = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_deflection"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_deflection")).removePower());
    public static final Identifier P_FamiliarFoxReturnShield = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_return_shield"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_return_shield")).removePower());
    public static final Identifier P_FamiliarFoxSiphon_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_siphon_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_siphon_1")).removePower());
    public static final Identifier P_FamiliarFoxSiphon_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_siphon_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 250))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_siphon_2")).removePower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_siphon_1")));
    public static final Identifier P_FamiliarFoxSiphoningRing = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_siphoning_ring"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_siphoning_ring")).removePower());
    public static final Identifier P_FamiliarFoxManaCapacity_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_mana_capacity_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_mana_capacity_1")).removePower());
    public static final Identifier P_FamiliarFoxManaCapacity_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_mana_capacity_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_mana_capacity_2")).removePower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_mana_capacity_1")));
    public static final Identifier P_FamiliarFoxReservoir = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_reservoir"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_reservoir")).removePower());
    public static final Identifier P_FamiliarFoxPermeableField_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_permeable_field_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_permeable_field_1")).removePower(ShapeShifterCurseFabric.identifier("form_familiar_fox_3_no_buff_effect")));
    public static final Identifier P_FamiliarFoxPermeableField_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_permeable_field_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_permeable_field_2")).removePower(ShapeShifterCurseFabric.identifier("form_familiar_fox_3_no_buff_effect"), ShapeShifterCurseFabric.identifier("perks/familiar_fox_permeable_field_1")));
    public static final Identifier P_FamiliarFoxPotionCharms = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("familiar_fox_potion_charms"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/familiar_fox_potion_charms")).removePower());
    public static final Identifier P_SnowFoxRevenge = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_revenge"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_revenge")).removePower());
    public static final Identifier P_SnowFoxElusivePaws = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_elusive_paws"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_elusive_paws")).removePower());
    public static final Identifier P_SnowFoxAirJump = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_air_jump"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_air_jump")).removePower());
    public static final Identifier P_SnowFoxFrostDive = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_frost_dive"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_frost_dive")).removePower());
    public static final Identifier P_SnowFoxColdWhirlwind_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_cold_whirlwind_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_cold_whirlwind_1")).removePower());
    public static final Identifier P_SnowFoxColdWhirlwind_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_cold_whirlwind_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_cold_whirlwind_2")).removePower(ShapeShifterCurseFabric.identifier("perks/snow_fox_cold_whirlwind_1")));
    public static final Identifier P_SnowFoxFireTraining_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_fire_training_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower().removePower(ShapeShifterCurseFabric.identifier("form_snow_fox_3_near_lava_damage"), ShapeShifterCurseFabric.identifier("form_snow_fox_3_near_fire_damage")));
    public static final Identifier P_SnowFoxColdRecovery_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_cold_recovery_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_cold_recovery_1")).removePower());
    public static final Identifier P_SnowFoxColdRecovery_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_cold_recovery_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/snow_fox_cold_recovery_2")).removePower());
    public static final Identifier P_SnowFoxFireTraining_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("snow_fox_fire_training_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower().removePower(ShapeShifterCurseFabric.identifier("form_snow_fox_3_burn_damage_up")));
    public static final Identifier P_AnubisWolfPackResponse_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_pack_response_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_pack_response_1"), ShapeShifterCurseFabric.identifier("perks/anubis_wolf_summoning_1")).removePower(ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_summon_wolf_on_hit"), ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_summon_wolf_when_hit")));
    public static final Identifier P_AnubisWolfSoulExcitation = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_soul_excitation"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_soul_excitation")).removePower());
    public static final Identifier P_AnubisWolfPackResponse_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_pack_response_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_summoning_2")).removePower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_summoning_1"), ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_summon_wolf_on_hit"), ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_summon_wolf_when_hit")));
    public static final Identifier P_AnubisWolfPackAmplification = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_pack_amplification"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_pack_amplification")).removePower());
    public static final Identifier P_AnubisWolfWitherTolerance_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_wither_tolerance_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_wither_tolerance_1")).removePower());
    public static final Identifier P_AnubisWolfWitherTolerance_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_wither_tolerance_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_wither_tolerance_2")).removePower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_wither_tolerance_1")));
    public static final Identifier P_AnubisWolfSoulRecall = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_soul_recall"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/anubis_wolf_soul_recall")).removePower());
    public static final Identifier P_AnubisWolfUndeadDiscernment = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("anubis_wolf_undead_discernment"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower().removePower(ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_undead_damage_down")));
    public static final Identifier P_SpiderFourLeggedAdaptation = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_four_legged_adaptation"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_four_legged_adaptation")).removePower(ShapeShifterCurseFabric.identifier("form_spider_3_speed_down")));
    public static final Identifier P_SpiderCocoonDigestion_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_cocoon_digestion_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_cocoon_digestion_1")).removePower());
    public static final Identifier P_SpiderCocoonDigestion_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_cocoon_digestion_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 100))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_cocoon_digestion_2")).removePower(ShapeShifterCurseFabric.identifier("perks/spider_cocoon_digestion_1")));
    public static final Identifier P_SpiderBridgeWeaver = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_bridge_weaver"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_bridge_weaver")).removePower(ShapeShifterCurseFabric.identifier("form_spider_3_web_bridge")));
    public static final Identifier P_SpiderPerceptualWeb = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_perceptual_web"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_perceptual_web")).removePower());
    public static final Identifier P_SpiderRapidSpinner = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_rapid_spinner"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_rapid_spinner")).removePower(ShapeShifterCurseFabric.identifier("form_spider_3_web_projectile")));
    public static final Identifier P_SpiderStabilizedProjectile = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_stabilized_projectile"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_stabilized_projectile")).removePower());
    public static final Identifier P_SpiderSilkSecretion_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_silk_secretion_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_silk_secretion_1")).removePower(ShapeShifterCurseFabric.identifier("form_spider_3_mana_recover")));
    public static final Identifier P_SpiderSilkSecretion_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_silk_secretion_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 300))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_silk_secretion_2")).removePower(ShapeShifterCurseFabric.identifier("form_spider_3_mana_recover"), ShapeShifterCurseFabric.identifier("perks/spider_silk_secretion_1")));
    public static final Identifier P_SpiderSilkGrapple = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("spider_silk_grapple"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/spider_silk_grapple")).removePower());
    public static final Identifier P_MarbledPolecatDoubleAirJump = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_double_air_jump"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_double_air_jump")).removePower());
    public static final Identifier P_MarbledPolecatPreciseMomentum = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_precise_momentum"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_precise_momentum")).removePower(ShapeShifterCurseFabric.identifier("form_snow_fox_3_enhanced_falling_attack")));
    public static final Identifier P_MarbledPolecatLeapingDash = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_leaping_dash"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 1))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_leaping_dash")).removePower());
    public static final Identifier P_MarbledPolecatHunterMetabolism = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_hunter_metabolism"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_hunter_metabolism")).removePower());
    public static final Identifier P_MarbledPolecatCombatNoodle = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_combat_noodle"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_combat_noodle")).removePower());
    public static final Identifier P_MarbledPolecatKeenScent = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("marbled_polecat_keen_scent"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/marbled_polecat_keen_scent")).removePower());
    public static final Identifier P_AvaliNanoCoating_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_nano_coating_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_nano_coating_1")).removePower());
    public static final Identifier P_AvaliNanoCoating_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_nano_coating_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_nano_coating_2")).removePower());
    public static final Identifier P_AvaliEnvironmentalProtection_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_environmental_protection_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 75))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_environmental_protection_1")).removePower(ShapeShifterCurseFabric.identifier("sub_form_avali_water_slowness")));
    public static final Identifier P_AvaliEnvironmentalProtection_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_environmental_protection_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 150))
            .addPower().removePower(ShapeShifterCurseFabric.identifier("sub_form_avali_hot_health_down")));
    public static final Identifier P_AvaliToolModification_1 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_tool_modification_1"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_tool_modification_1")).removePower());
    public static final Identifier P_AvaliToolModification_2 = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_tool_modification_2"))
            .setIcon(FALLBACK_PERK_ICON).cost(new BaseCost(RegCostType.COST_XP, 200))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_tool_modification_2")).removePower());
    public static final Identifier P_AvaliEmergencyProtocol = registerPerkCommon(new NormalPerk(ShapeShifterCurseFabric.identifier("avali_emergency_protocol"))
            .setIcon(FALLBACK_PERK_ICON).cost(new ItemCost(RegCostType.COST_ITEM, new ItemStack(RegCustomItem.GLINT_PRISM), 2))
            .addPower(ShapeShifterCurseFabric.identifier("perks/avali_emergency_protocol")).removePower());
    
    public static final Identifier T_Bat3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("bat_3_perk_tree"))
            .addNode(P_BatPosture_1, 1, 0)
            .addNode(P_BatPosture_2, 2, 0, P_BatPosture_1)
            .addNode(P_BatEcholocation, 3, 0, P_BatPosture_2)
            .addNode(P_BatWingbeat, 4, 0, P_BatEcholocation)
            .addNode(P_BatArrowThrow, 2, 60, P_BatPosture_1)
            .addNode(P_BatAirBlast, 4, 60, P_BatArrowThrow)
            .addNode(P_BatSunResistance_1, 1, 140)
            .addNode(P_BatSunResistance_2, 2, 140, P_BatSunResistance_1)
            .addNode(P_BatNightVeil, 2, 200, P_BatSunResistance_1)
    );
    public static final Identifier T_Axolotl3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("axolotl_3_perk_tree"))
            .addNode(P_AxolotlVegetation, 1, 0)
            .addNode(P_AxolotlDryTolerance, 2, 0, P_AxolotlVegetation)
            .addNode(P_AxolotlTidalPull, 2, 60, P_AxolotlVegetation)
            .addNode(P_AxolotlMoistureReturn_1, 1, 140)
            .addNode(P_AxolotlMoistureReturn_2, 2, 140, P_AxolotlMoistureReturn_1)
            .addNode(P_AxolotlCollectWater, 4, 140, P_AxolotlMoistureReturn_2)
            .addNode(P_AxolotlPropulsionEfficiency, 1, 240)
            .addNode(P_AxolotlWaveEfficiency, 2, 240, P_AxolotlPropulsionEfficiency)
            .addNode(P_AxolotlWaterMagicEfficiency, 3, 240, P_AxolotlWaveEfficiency)
    );
    public static final Identifier T_Ocelot3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("ocelot_3_perk_tree"))
            .addNode(P_OcelotArmor_1, 1, 0)
            .addNode(P_OcelotArmor_2, 2, 0, P_OcelotArmor_1)
            .addNode(P_OcelotMetabolism_1, 1, 80)
            .addNode(P_OcelotMetabolicOverload, 2, 80, P_OcelotMetabolism_1)
            .addNode(P_OcelotMetabolism_2, 3, 140, P_OcelotMetabolism_1)
            .addNode(P_OcelotAmbush, 2, 220)
            .addNode(P_OcelotHungryPounce, 3, 220, P_OcelotAmbush)
            .addNode(P_OcelotLongPounce, 3, 280, P_OcelotAmbush)
    );
    public static final Identifier T_FamiliarFox3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("familiar_fox_3_perk_tree"))
            .addNode(P_FamiliarFoxDeflection, 2, 0)
            .addNode(P_FamiliarFoxReturnShield, 3, 0, P_FamiliarFoxDeflection)
            .addNode(P_FamiliarFoxSiphon_1, 1, 80)
            .addNode(P_FamiliarFoxSiphon_2, 3, 80, P_FamiliarFoxSiphon_1)
            .addNode(P_FamiliarFoxSiphoningRing, 4, 80, P_FamiliarFoxSiphon_2)
            .addNode(P_FamiliarFoxManaCapacity_1, 1, 160)
            .addNode(P_FamiliarFoxManaCapacity_2, 3, 160, P_FamiliarFoxManaCapacity_1)
            .addNode(P_FamiliarFoxReservoir, 2, 160, P_FamiliarFoxManaCapacity_1)
            .addNode(P_FamiliarFoxPermeableField_1, 2, 240, P_FamiliarFoxManaCapacity_1)
            .addNode(P_FamiliarFoxPermeableField_2, 3, 240, P_FamiliarFoxPermeableField_1)
            .addNode(P_FamiliarFoxPotionCharms, 3, 320)
    );
    public static final Identifier T_SnowFox3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("snow_fox_3_perk_tree"))
            .addNode(P_SnowFoxRevenge, 2, 0)
            .addNode(P_SnowFoxElusivePaws, 1, 80)
            .addNode(P_SnowFoxAirJump, 2, 80, P_SnowFoxElusivePaws)
            .addNode(P_SnowFoxFrostDive, 4, 80, P_SnowFoxAirJump)
            .addNode(P_SnowFoxColdWhirlwind_1, 2, 160, P_SnowFoxElusivePaws)
            .addNode(P_SnowFoxColdWhirlwind_2, 3, 160, P_SnowFoxColdWhirlwind_1)
            .addNode(P_SnowFoxFireTraining_1, 1, 240)
            .addNode(P_SnowFoxColdRecovery_1, 2, 240, P_SnowFoxFireTraining_1)
            .addNode(P_SnowFoxColdRecovery_2, 3, 240, P_SnowFoxColdRecovery_1)
            .addNode(P_SnowFoxFireTraining_2, 2, 320, P_SnowFoxFireTraining_1)
    );
    public static final Identifier T_AnubisWolf3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("anubis_wolf_3_perk_tree"))
            .addNode(P_AnubisWolfPackResponse_1, 1, 0)
            .addNode(P_AnubisWolfSoulExcitation, 2, 0, P_AnubisWolfPackResponse_1)
            .addNode(P_AnubisWolfPackResponse_2, 3, 80, P_AnubisWolfPackResponse_1)
            .addNode(P_AnubisWolfPackAmplification, 4, 80, P_AnubisWolfPackResponse_2)
            .addNode(P_AnubisWolfWitherTolerance_1, 1, 160)
            .addNode(P_AnubisWolfWitherTolerance_2, 3, 160, P_AnubisWolfWitherTolerance_1)
            .addNode(P_AnubisWolfSoulRecall, 2, 240, P_AnubisWolfWitherTolerance_1)
            .addNode(P_AnubisWolfUndeadDiscernment, 1, 320)
    );
    public static final Identifier T_Spider3PerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("spider_3_perk_tree"))
            .addNode(P_SpiderFourLeggedAdaptation, 1, 0)
            .addNode(P_SpiderCocoonDigestion_1, 1, 80)
            .addNode(P_SpiderCocoonDigestion_2, 2, 80, P_SpiderCocoonDigestion_1)
            .addNode(P_SpiderBridgeWeaver, 2, 160)
            .addNode(P_SpiderPerceptualWeb, 3, 160, P_SpiderBridgeWeaver)
            .addNode(P_SpiderRapidSpinner, 3, 240, P_SpiderBridgeWeaver)
            .addNode(P_SpiderStabilizedProjectile, 4, 240, P_SpiderRapidSpinner)
            .addNode(P_SpiderSilkSecretion_1, 1, 320)
            .addNode(P_SpiderSilkSecretion_2, 3, 320, P_SpiderSilkSecretion_1)
            .addNode(P_SpiderSilkGrapple, 2, 400, P_SpiderSilkSecretion_1)
    );
    public static final Identifier T_MarbledPolecatPerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("marbled_polecat_perk_tree"))
            .addNode(P_MarbledPolecatDoubleAirJump, 1, 0)
            .addNode(P_MarbledPolecatPreciseMomentum, 2, 0, P_MarbledPolecatDoubleAirJump)
            .addNode(P_MarbledPolecatLeapingDash, 3, 80, P_MarbledPolecatDoubleAirJump)
            .addNode(P_MarbledPolecatHunterMetabolism, 1, 160)
            .addNode(P_MarbledPolecatCombatNoodle, 2, 160, P_MarbledPolecatHunterMetabolism)
            .addNode(P_MarbledPolecatKeenScent, 2, 240, P_MarbledPolecatHunterMetabolism)
    );
    public static final Identifier T_AvaliPerkTree = registerPerkTree(new PerkTree(ShapeShifterCurseFabric.identifier("avali_perk_tree"))
            .addNode(P_AvaliNanoCoating_1, 1, 0)
            .addNode(P_AvaliNanoCoating_2, 2, 0, P_AvaliNanoCoating_1)
            .addNode(P_AvaliEnvironmentalProtection_1, 1, 80)
            .addNode(P_AvaliEnvironmentalProtection_2, 2, 80, P_AvaliEnvironmentalProtection_1)
            .addNode(P_AvaliToolModification_1, 2, 160)
            .addNode(P_AvaliToolModification_2, 3, 160, P_AvaliToolModification_1)
            .addNode(P_AvaliEmergencyProtocol, 3, 240)
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
    public static final Identifier SSC_T_shape_shifter_curse_f_snowfox_tree = registerPerkTree(new PerkTree(new Identifier("shape-shifter-curse:f_snowfox_tree"))
            .addNode(SSC_P_shape_shifter_curse_snowfox_root, 0, 0)
    );
    // SSC Studio: end perk registrations
}
