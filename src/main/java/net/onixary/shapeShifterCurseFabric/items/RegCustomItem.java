package net.onixary.shapeShifterCurseFabric.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.armors.MorphScaleArmor;
import net.onixary.shapeShifterCurseFabric.items.armors.NetheriteMorphScaleArmor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.item.Items.register;

public class RegCustomItem {
    private RegCustomItem(){}

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
    public static final Item MORPHSCALE_HEADRING = register("morphscale_headring", new MorphScaleArmor(ArmorItem.Type.HELMET));
    public static final Item MORPHSCALE_VEST = register("morphscale_vest", new MorphScaleArmor(ArmorItem.Type.CHESTPLATE));
    public static final Item MORPHSCALE_CUISH = register("morphscale_cuish", new MorphScaleArmor(ArmorItem.Type.LEGGINGS));
    public static final Item MORPHSCALE_ANKLET = register("morphscale_anklet", new MorphScaleArmor(ArmorItem.Type.BOOTS));
    // netherite morphscale armor
    public static final Item NETHERITE_MORPHSCALE_HEADRING = register("netherite_morphscale_headring", new NetheriteMorphScaleArmor(ArmorItem.Type.HELMET));
    public static final Item NETHERITE_MORPHSCALE_VEST = register("netherite_morphscale_vest", new NetheriteMorphScaleArmor(ArmorItem.Type.CHESTPLATE));
    public static final Item NETHERITE_MORPHSCALE_CUISH = register("netherite_morphscale_cuish", new NetheriteMorphScaleArmor(ArmorItem.Type.LEGGINGS));
    public static final Item NETHERITE_MORPHSCALE_ANKLET = register("netherite_morphscale_anklet", new NetheriteMorphScaleArmor(ArmorItem.Type.BOOTS));

    // 用于成就图标的占位物品
    public static final Item ICON_CURSED_MOON = register("icon_cursed_moon", new Item(new Item.Settings()));

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
                entries.addAll(buildAllPotions(
                        RegCustomPotions.MOONDUST_POTION,
                        RegCustomPotions.BAT_FORM_POTION,
                        RegCustomPotions.AXOLOTL_FORM_POTION,
                        RegCustomPotions.OCELOT_FORM_POTION,
                        RegCustomPotions.FAMILIAR_FOX_FORM_POTION,
                        RegCustomPotions.SNOW_FOX_FORM_POTION,
                        RegCustomPotions.ANUBIS_WOLF_FORM_POTION,
                        RegCustomPotions.ALLEY_FORM_POTION,
                        RegCustomPotions.FERAL_CAT_FORM_POTION,
                        RegCustomPotions.CUSTOM_STATUE_FORM_POTION
                ));
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
}
