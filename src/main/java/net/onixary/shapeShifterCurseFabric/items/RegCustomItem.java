package net.onixary.shapeShifterCurseFabric.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.armors.MorphScaleArmor;
import net.onixary.shapeShifterCurseFabric.items.armors.NetheriteMorphScaleArmor;
import net.onixary.shapeShifterCurseFabric.items.tools.*;
import net.onixary.shapeShifterCurseFabric.items.trinkets.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import static net.minecraft.item.Items.register;
import static net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock.*;

public class RegCustomItem {
    private RegCustomItem(){}

    public static final Item GRAPHENE_BLADE = register("graphene_blade", new SwordItem(ToolMaterials.DIAMOND, 4, -2.4f, new Item.Settings().maxDamage(128)));
    public static final Item COMPOUND_KINETIC_BOW = register("compound_kinetic_bow", new BowItem(new Item.Settings().maxDamage(768)));

    //public static final Item CURSED_BOOK_OF_SHAPE_SHIFTER = register("cursed_book_of_shape_shifter", new StartBook(new StartBook.Settings()));
    public static final Item BOOK_OF_SHAPE_SHIFTER = register("book_of_shape_shifter", new BookOfShapeShifter(new BookOfShapeShifter.Settings()));
    public static final Item UNTREATED_MOONDUST = register("untreated_moondust", new UntreatedMoonDust(new Item.Settings()));
    public static final Item INHIBITOR = register("inhibitor", new Inhibitor(new Item.Settings()));
    public static final Item POWERFUL_INHIBITOR = register("powerful_inhibitor", new PowerfulInhibitor(new Item.Settings()));
    public static final Item CREATIVE_INHIBITOR = register("creative_inhibitor", new CreativeInhibitor(new Item.Settings()));
    public static final Item CATALYST = register("catalyst", new Catalyst(new Item.Settings()));
    public static final Item POWERFUL_CATALYST = register("powerful_catalyst", new PowerfulCatalyst(new Item.Settings()));
    public static final Item MOONDUST_MATRIX = register("moondust_matrix", new MoonDustMatrix(new Item.Settings()));
    // morphscale armor
    public static final Item MORPHSCALE_CORE = register("morphscale_core", new Item(new Item.Settings()));
    public static final Item SUPER_MORPHSCALE_CORE = register("super_morphscale_core", new SuperMorphScaleCore(new Item.Settings().maxDamage(64 * SuperMorphScaleCore.damagePerItem).rarity(Rarity.EPIC)));
    public static final Item MORPHSCALE_HEADRING = register("morphscale_headring", new MorphScaleArmor(ArmorItem.Type.HELMET));
    public static final Item MORPHSCALE_VEST = register("morphscale_vest", new MorphScaleArmor(ArmorItem.Type.CHESTPLATE));
    public static final Item MORPHSCALE_CUISH = register("morphscale_cuish", new MorphScaleArmor(ArmorItem.Type.LEGGINGS));
    public static final Item MORPHSCALE_ANKLET = register("morphscale_anklet", new MorphScaleArmor(ArmorItem.Type.BOOTS));
    // netherite morphscale armor
    public static final Item NETHERITE_MORPHSCALE_HEADRING = register("netherite_morphscale_headring", new NetheriteMorphScaleArmor(ArmorItem.Type.HELMET));
    public static final Item NETHERITE_MORPHSCALE_VEST = register("netherite_morphscale_vest", new NetheriteMorphScaleArmor(ArmorItem.Type.CHESTPLATE));
    public static final Item NETHERITE_MORPHSCALE_CUISH = register("netherite_morphscale_cuish", new NetheriteMorphScaleArmor(ArmorItem.Type.LEGGINGS));
    public static final Item NETHERITE_MORPHSCALE_ANKLET = register("netherite_morphscale_anklet", new NetheriteMorphScaleArmor(ArmorItem.Type.BOOTS));
    // 模组自定义物品
    public static final Item MOONDUST_CRYSTAL_SHARD = register("moondust_crystal_shard", new MoonDustCrystalShard(new MoonDustCrystalShard.Settings()));
    public static final Item ECTOPLASM_RAG = register("ectoplasm_rag", new Item(new Item.Settings()));
    public static final ToolItem BOTTLED_SNOWFALL = register("bottled_snowfall", new BottledSnowfall(BottledSnowfallToolMaterial.INSTANCE, 1, 1, new Item.Settings()));
    public static final ToolItem DIAMOND_MINING_CLAW = register("diamond_mining_claw", new DiamondMiningClaw(DiamondMiningClawToolMaterial.INSTANCE, 1, -2.4f, new Item.Settings()));
    public static final Item FIRE_CHARM_PAPER = register("fire_charm_paper", new FireCharmPaper(new Item.Settings()));
    public static final Item AUXILIARY_SWORD = register("auxiliary_sword", new AuxiliarySword(AuxiliarySwordToolMaterial.INSTANCE, 1, -2.4f, new Item.Settings()));
    public static final ToolItem AUXILIARY_PICKAXE = register("auxiliary_pickaxe", new AuxiliaryPickaxe(AuxiliaryPickaxeToolMaterial.INSTANCE, 1, -2.8f, new Item.Settings()));
    public static final ToolItem AUXILIARY_AXE = register("auxiliary_axe", new AuxiliaryAxe(AuxiliaryAxeToolMaterial.INSTANCE, 1, -3.1f, new Item.Settings()));
    // 模组自定义Trinkets
    public static final Item AMULET_BRACELET = register("amulet_bracelet", new AmuletBraceletTrinket(new AmuletBraceletTrinket.Settings()));
    public static final Item ATTACH_HOOK = register("attach_hook", new AttachHookTrinket(new AttachHookTrinket.Settings()));
    public static final Item CHARM_OF_HOLLOW_FANG = register("charm_of_hollow_fang", new CharmOfHollowFangTrinket(new CharmOfHollowFangTrinket.Settings()));
    public static final Item CHARM_OF_NIGHT_CRYSTAL = register("charm_of_night_crystal", new CharmOfNightCrystalTrinket(new CharmOfNightCrystalTrinket.Settings()));
    public static final Item CHARM_OF_REVERSE_THERMOMETER = register("charm_of_reverse_thermometer", new CharmOfReverseThermometerTrinket(new CharmOfReverseThermometerTrinket.Settings()));
    public static final Item COLLAR_OF_TENSION = register("collar_of_tension", new CollarOfTensionTrinket(new CollarOfTensionTrinket.Settings()));
    public static final Item COLLAR_OF_WHISKERS = register("collar_of_whiskers", new CollarOfWhiskersTrinket(new CollarOfWhiskersTrinket.Settings()));
    public static final Item DIGESTION_FIBER_BALL = register("digestion_fiber_ball", new DigestionFiberBallTrinket(new DigestionFiberBallTrinket.Settings()));
    public static final Item FROST_PAWGLOVE = register("frost_pawglove", new FrostPawgloveTrinket(new FrostPawgloveTrinket.Settings()));
    public static final Item WITHERED_BANDAGE = register("withered_bandage", new WitheredBandageTrinket(new WitheredBandageTrinket.Settings()));
    public static final Item FOUNTAIN_BELT = register("fountain_belt", new FountainBeltTrinket(new FountainBeltTrinket.Settings()));
    public static final Item RESONANT_CORE = register("resonant_core", new ResonantCoreTrinket(new ResonantCoreTrinket.Settings()));
    public static final Item VENOM_SPINDLE = register("venom_spindle", new VenomSpindle(new VenomSpindle.Settings()));

    public static final Item CHARM_OF_NIGHT_CRYSTAL_PLUS = register("charm_of_night_crystal_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item CHARM_OF_HOLLOW_FANG_PLUS = register("charm_of_hollow_fang_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item ATTACH_HOOK_PLUS = register("attach_hook_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item CHARM_OF_REVERSE_THERMOMETER_PLUS = register("charm_of_reverse_thermometer_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item FROST_PAWGLOVE_PLUS = register("frost_pawglove_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item VENOM_SPINDLE_PLUS = register("venom_spindle_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item COLLAR_OF_WHISKERS_PLUS = register("collar_of_whiskers_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item DIGESTION_FIBER_BALL_PLUS = register("digestion_fiber_ball_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item WITHERED_BANDAGE_PLUS = register("withered_bandage_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item FOUNTAIN_BELT_PLUS = register("fountain_belt_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item AMULET_BRACELET_PLUS = register("amulet_bracelet_plus", new FormTrinket(new Item.Settings().rarity(Rarity.RARE)));
    public static final ToolItem BOTTLED_SNOWFALL_PLUS = register("bottled_snowfall_plus", new BottledSnowfall(BottledSnowfallToolMaterial.INSTANCE, 1, 1, new Item.Settings().rarity(Rarity.RARE)));
    public static final Item EXPLOSIVE_CHARM_PAPER = register("explosive_charm_paper", new FireCharmPaper(new Item.Settings()));
    public static final Item GLINT_PRISM = register("glint_prism", new Item(new Item.Settings()));
    public static final Item JUNGLE_CATALYST_CORE = register("jungle_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item WEB_CATALYST_CORE = register("web_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item FROST_CATALYST_CORE = register("frost_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item FOXFIRE_CATALYST_CORE = register("foxfire_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item SURGE_CATALYST_CORE = register("surge_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item DUNE_CATALYST_CORE = register("dune_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item NIGHT_CATALYST_CORE = register("night_catalyst_core", new CatalystCoreItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    public static final Item TRANSFORMATIVE_AXOLOTL_BUCKET = register("transformative_axolotl_bucket", new EntityBucketItem(ShapeShifterCurseFabric.T_AXOLOTL, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_AXOLOTL, (new Item.Settings()).maxCount(1)));
    // 减少非蜘蛛玩家食用的中毒量，做到实在没东西吃的时候也能硬着头皮吃的感觉
    public static final Item SPIDER_FLUID_COCOON = register("spider_fluid_cocoon", new SpiderFluidCocoon(new SpiderFluidCocoon.Settings()));

    public static final Item PATRON_FORM_ITEM = register("patron_form_item", new PatronFormItem(new Item.Settings()));
    public static final Item SELECT_FORM_ITEM = register("select_form_item", new SelectFormItem(new Item.Settings()));

    public static final Item CUSTOM_TRINKET = register("custom_trinket", new CustomTrinket(new CustomTrinket.Settings()));
    // 用于成就图标的占位物品
    public static final Item ICON_CURSED_MOON = register("icon_cursed_moon", new Item(new Item.Settings()));
    // 蛛丝弹占位物品
    public static final Item WEB_PROJECTILE = register("web_projectile", new WebProjectileItem(new Item.Settings()));
    public static final Item SILK_DEW = register("silk_dew", new SilkDew(new Item.Settings()));

    public static final Item RIPPLE_MIRROR = register("ripple_mirror", new RippleMirror(new Item.Settings()));

    public static final Item HEALING_CHARM = register("healing_charm_paper", new PotionCharmItem(Potions.STRONG_HEALING));
    public static final Item HARMING_CHARM = register("harming_charm_paper", new PotionCharmItem(Potions.STRONG_HARMING));
    public static final Item POISON_CHARM = register("poison_charm_paper", new PotionCharmItem(Potions.STRONG_POISON));


    public static ItemStack buildPotion(Item PotionItem, Potion potion) {
        ItemStack potionStack = new ItemStack(PotionItem);
        PotionUtil.setPotion(potionStack, potion);
        return potionStack;
    }

    public static Collection<ItemStack> buildAllPotions(Potion... potions) {
        List<ItemStack> potionStacks = new ArrayList<>();
        for (Potion potion : potions) {
            potionStacks.add(buildPotion(Items.POTION, potion));
        }
        for (Potion potion : potions) {
            potionStacks.add(buildPotion(Items.SPLASH_POTION, potion));
        }
        for (Potion potion : potions) {
            potionStacks.add(buildPotion(Items.LINGERING_POTION, potion));
        }
        for (Potion potion : potions) {
            potionStacks.add(buildPotion(Items.TIPPED_ARROW, potion));
        }
        return potionStacks;
    }

    public static final ItemGroup SSC_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ICON_CURSED_MOON))
            .displayName(Text.translatable("itemGroup.shape_shifter_curse.sscitems"))
            .entries((context, entries) -> {
                entries.add(BOOK_OF_SHAPE_SHIFTER);
                entries.add(UNTREATED_MOONDUST);
                entries.add(MOONDUST_MATRIX);
                entries.add(MORPHSCALE_CORE);
                entries.add(SUPER_MORPHSCALE_CORE);
                entries.add(INHIBITOR);
                entries.add(POWERFUL_INHIBITOR);
                entries.add(CREATIVE_INHIBITOR);
                entries.add(CATALYST);
                entries.add(POWERFUL_CATALYST);
                entries.add(MORPHSCALE_HEADRING);
                entries.add(MORPHSCALE_VEST);
                entries.add(MORPHSCALE_CUISH);
                entries.add(MORPHSCALE_ANKLET);
                entries.add(NETHERITE_MORPHSCALE_HEADRING);
                entries.add(NETHERITE_MORPHSCALE_VEST);
                entries.add(NETHERITE_MORPHSCALE_CUISH);
                entries.add(NETHERITE_MORPHSCALE_ANKLET);
                entries.add(DIAMOND_MINING_CLAW);
                entries.add(MOONDUST_CRYSTAL_SHARD);
                entries.add(ECTOPLASM_RAG);
                entries.add(AMULET_BRACELET);
                entries.add(ATTACH_HOOK);
                entries.add(BOTTLED_SNOWFALL);
                entries.add(CHARM_OF_HOLLOW_FANG);
                entries.add(CHARM_OF_NIGHT_CRYSTAL);
                entries.add(CHARM_OF_REVERSE_THERMOMETER);
                entries.add(COLLAR_OF_TENSION);
                entries.add(COLLAR_OF_WHISKERS);
                entries.add(DIGESTION_FIBER_BALL);
                entries.add(FROST_PAWGLOVE);
                entries.add(WITHERED_BANDAGE);
                entries.add(FOUNTAIN_BELT);
                entries.add(RESONANT_CORE);
                entries.add(VENOM_SPINDLE);
                entries.add(CHARM_OF_NIGHT_CRYSTAL_PLUS);
                entries.add(CHARM_OF_HOLLOW_FANG_PLUS);
                entries.add(ATTACH_HOOK_PLUS);
                entries.add(CHARM_OF_REVERSE_THERMOMETER_PLUS);
                entries.add(FROST_PAWGLOVE_PLUS);
                entries.add(VENOM_SPINDLE_PLUS);
                entries.add(COLLAR_OF_WHISKERS_PLUS);
                entries.add(DIGESTION_FIBER_BALL_PLUS);
                entries.add(WITHERED_BANDAGE_PLUS);
                entries.add(FOUNTAIN_BELT_PLUS);
                entries.add(AMULET_BRACELET_PLUS);
                entries.add(BOTTLED_SNOWFALL_PLUS);
                entries.add(EXPLOSIVE_CHARM_PAPER);
                entries.add(GLINT_PRISM);
                entries.add(JUNGLE_CATALYST_CORE);
                entries.add(WEB_CATALYST_CORE);
                entries.add(FROST_CATALYST_CORE);
                entries.add(FOXFIRE_CATALYST_CORE);
                entries.add(SURGE_CATALYST_CORE);
                entries.add(DUNE_CATALYST_CORE);
                entries.add(NIGHT_CATALYST_CORE);
                entries.add(CUSTOM_TRINKET);
                entries.add(FIRE_CHARM_PAPER);
                entries.add(TRANSFORMATIVE_AXOLOTL_BUCKET);
                entries.add(SPIDER_FLUID_COCOON);
                entries.add(AUXILIARY_SWORD);
                entries.add(GRAPHENE_BLADE);
                entries.add(COMPOUND_KINETIC_BOW);
                entries.add(AUXILIARY_PICKAXE);
                entries.add(AUXILIARY_AXE);
                entries.add(SELECT_FORM_ITEM);
                entries.add(SILK_DEW);
                entries.add(RIPPLE_MIRROR);
                entries.add(HEALING_CHARM);
                entries.add(HARMING_CHARM);
                entries.add(POISON_CHARM);
                // 方块物品注册
                entries.add(MOONDUST_CRYSTAL_GRIT);
                entries.add(WEB_COMPOSTER);
                entries.add(DEW_COVERED_COBWEB);
                entries.add(ALTAR_BLOCK);
                entries.addAll(buildAllPotions(
                        RegCustomPotions.MOONDUST_POTION,
                        RegCustomPotions.BAT_FORM_POTION,
                        RegCustomPotions.AXOLOTL_FORM_POTION,
                        RegCustomPotions.OCELOT_FORM_POTION,
                        RegCustomPotions.FAMILIAR_FOX_FORM_POTION,
                        RegCustomPotions.SNOW_FOX_FORM_POTION,
                        RegCustomPotions.ANUBIS_WOLF_FORM_POTION,
                        RegCustomPotions.SPIDER_FORM_POTION,
                        RegCustomPotions.ALLEY_FORM_POTION,
                        RegCustomPotions.FERAL_CAT_FORM_POTION,
                        RegCustomPotions.CUSTOM_STATUE_FORM_POTION,
                        RegCustomPotions.FEED_POTION
                ));
                if (PatronUtils.EnablePatronFeature) {
                    entries.add(PATRON_FORM_ITEM);
                }
            })
            .build();

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), item);
    }

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, ShapeShifterCurseFabric.identifier("ssc_item"), SSC_GROUP);
        /*
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(BOOK_OF_SHAPE_SHIFTER);
            if (PatronUtils.EnablePatronFeature) {
                entries.add(PATRON_FORM_ITEM);
            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(UNTREATED_MOONDUST);
            entries.add(MOONDUST_MATRIX);
            entries.add(MORPHSCALE_CORE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(INHIBITOR);
            entries.add(POWERFUL_INHIBITOR);
            entries.add(CREATIVE_INHIBITOR);
            entries.add(CATALYST);
            entries.add(POWERFUL_CATALYST);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(MORPHSCALE_HEADRING);
            entries.add(MORPHSCALE_VEST);
            entries.add(MORPHSCALE_CUISH);
            entries.add(MORPHSCALE_ANKLET);
            entries.add(NETHERITE_MORPHSCALE_HEADRING);
            entries.add(NETHERITE_MORPHSCALE_VEST);
            entries.add(NETHERITE_MORPHSCALE_CUISH);
            entries.add(NETHERITE_MORPHSCALE_ANKLET);
        });
         */
    }

    @Environment(EnvType.CLIENT)
    public static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(
                COMPOUND_KINETIC_BOW, new Identifier("pull"), (stack, world, entity, seed) ->
                        entity != null && entity.getActiveItem() == stack ? (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f : 0.0f);
        FabricModelPredicateProviderRegistry.register(
                COMPOUND_KINETIC_BOW, new Identifier("pulling"), (stack, world, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
