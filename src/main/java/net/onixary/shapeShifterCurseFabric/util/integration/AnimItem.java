package net.onixary.shapeShifterCurseFabric.util.integration;

import net.minecraft.item.Item;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class AnimItem {
    public static class AnimItemTag {
        public final String Name;
        public Predicate<@NotNull Item> Predicate;
        public AnimItemTag(String name, Predicate<@NotNull Item> predicate) {
            Name = name;
            Predicate = predicate;
        }

        public boolean test(Item item) {
            return Predicate.test(item);
        }
    };

    public static final List<AnimItemTag> TAGS = new ArrayList<>();
    public static final HashMap<Item, List<AnimItemTag>> ANIM_ITEMS = new HashMap<>();

    public static final String SlashBladeClassPath = "mods.flammpfeil.slashblade.item.ItemSlashBlade";
    public static final @Nullable Class<?> SlashBladeClass;
    static {
        @Nullable Class<?> TempClass;
        try {
            TempClass = Class.forName(SlashBladeClassPath);
            ShapeShifterCurseFabric.LOGGER.info("SlashBlade Anim Integration Enabled");
        } catch (ClassNotFoundException e) {
            TempClass = null;
        }
        SlashBladeClass = TempClass;
    }

    public static final AnimItemTag NoAnimItemTag = AnimItem.registerAnimItemTag("no_anim_item", item -> {
        if (SlashBladeClass != null) {
            return SlashBladeClass.isInstance(item);
        }
        return false;
    });

    public static List<AnimItemTag> _calcAnimItemTags(Item item) {
        List<AnimItemTag> tags = new ArrayList<>();
        for (AnimItemTag tag : TAGS) {
            if (tag.test(item)) {
                tags.add(tag);
            }
        }
        ANIM_ITEMS.put(item, tags);
        return tags;
    }

    public static List<AnimItemTag> getAnimItemTags(Item item) {
        if (ANIM_ITEMS.containsKey(item)) {
            return ANIM_ITEMS.get(item);
        }
        return _calcAnimItemTags(item);
    }

    public static AnimItemTag registerAnimItemTag(String name, Predicate<@NotNull Item> predicate) {
        AnimItemTag tag = new AnimItemTag(name, predicate);
        TAGS.add(tag);
        return tag;
    }
}
