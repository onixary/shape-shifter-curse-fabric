package net.onixary.shapeShifterCurseFabric.recipes.altar;

import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AltarBlockEntity;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import io.github.apace100.apoli.power.PowerTypeRegistry;

/** Runs inside Fabric's existing GameTest server: ./gradlew checkAltarRecipes */
public final class AltarRecipeCheck {
    private static final Identifier ID = new Identifier("test", "altar");

    private static final class Inventory extends SimpleInventory implements SidedInventory {
        Inventory() { super(12); }
        public int[] getAvailableSlots(Direction side) { return new int[]{0, 1, 9}; }
        public boolean canInsert(int slot, ItemStack stack, Direction side) { return true; }
        public boolean canExtract(int slot, ItemStack stack, Direction side) { return true; }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static AltarShapelessRecipe recipe(int seconds, int moons) {
        return new AltarShapelessRecipe.Serializer().read(ID, JsonParser.parseString("""
                {"ingredients":[{"item":"minecraft:stick"}],
                 "catalyst":{"item":"minecraft:diamond"},
                 "result":{"item":"minecraft:emerald"},"time":%d,"moondust_cost":%d}
                """.formatted(seconds * 20, moons)).getAsJsonObject());
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void recipes(TestContext context) {
        for (int[] example : new int[][]{{5, 5}, {10, 10}, {30, 16}, {60, 9}, {5, 0}}) {
            AltarShapelessRecipe recipe = recipe(example[0], example[1]);
            int total = 0;
            for (int tick = 0; tick < recipe.recipeTime(); tick++) {
                int cost = recipe.fuelUsage(tick);
                check(cost >= 0, "Fuel must never be created");
                total += cost;
            }
            check(total == example[1] * 800, "Exact moondust cost over full duration");
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            new AltarShapelessRecipe.Serializer().write(packet, recipe);
            AltarShapelessRecipe copy = new AltarShapelessRecipe.Serializer().read(ID, packet);
            check(copy.totalFuelUsage() == total && copy.recipeTime() == recipe.recipeTime(), "Shapeless network roundtrip");
            packet.release();
        }

        AltarShapelessRecipe recipe = recipe(30, 16);
        Inventory inventory = new Inventory();
        inventory.setStack(0, new ItemStack(Items.STICK));
        check(!recipe.matches(inventory, null), "A catalyst is required");
        inventory.setStack(9, new ItemStack(Items.DIAMOND));
        check(recipe.matches(inventory, null), "Correct ingredients and catalyst match");
        inventory.setStack(0, new ItemStack(Items.DIRT));
        check(!recipe.matches(inventory, null), "Same occupied slot count must not match wrong ingredients");
        inventory.setStack(0, new ItemStack(Items.STICK));
        recipe.consumeInputs(inventory);
        check(inventory.getStack(0).isEmpty(), "Input is consumed");
        check(inventory.getStack(9).isOf(Items.DIAMOND), "Catalyst is never consumed");
        inventory.setStack(0, new ItemStack(Items.POWDER_SNOW_BUCKET));
        recipe.consumeInputs(inventory);
        check(inventory.getStack(0).isOf(Items.BUCKET), "Frost core returns its bucket");
        inventory.setStack(0, new ItemStack(Items.HONEY_BOTTLE, 2));
        check(recipe.getExtraOutput(inventory).get(0).isOf(Items.GLASS_BOTTLE), "Stacked container inputs return extra containers");

        AltarShapedRecipe shaped = new AltarShapedRecipe.Serializer().read(ID, JsonParser.parseString("""
                {"pattern":["SS"],"key":{"S":{"item":"minecraft:stick"}},
                 "result":{"item":"minecraft:emerald"},"time":600,"moondust_cost":16}
                """).getAsJsonObject());
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        new AltarShapedRecipe.Serializer().write(packet, shaped);
        AltarShapedRecipe copy = new AltarShapedRecipe.Serializer().read(ID, packet);
        check(copy.totalFuelUsage() == 12800 && copy.recipeTime() == 600, "Shaped network roundtrip");
        packet.release();
        Inventory shapedInventory = new Inventory();
        shapedInventory.setStack(0, new ItemStack(Items.STICK));
        check(!copy.matches(shapedInventory, null), "Repeated ingredients require both slots");
        shapedInventory.setStack(1, new ItemStack(Items.STICK));
        check(copy.matches(shapedInventory, null), "Shaped ingredients match");

        AltarShapelessRecipe legacy = new AltarShapelessRecipe.Serializer().read(ID, JsonParser.parseString("""
                {"ingredients":[{"item":"minecraft:stick"}],"result":{"item":"minecraft:emerald"},
                 "time":100,"fuel_cost":3}
                """).getAsJsonObject());
        check(legacy.fuelUsage(0) == 3 && legacy.totalFuelUsage() == 300, "Legacy per-tick fuel remains compatible");

        // Check the actual loaded data, not only the vanilla-item fixtures above.
        int upgradeMappings = 0;
        for (var entry : TrinketUtils.accessoryPowerRegistry.entrySet()) {
            if (!entry.getKey().getPath().endsWith("_plus")) continue;
            upgradeMappings++;
            for (var group : entry.getValue().layerPowerAddMap.values()) {
                for (var powers : group.values()) {
                    for (Identifier id : powers) {
                        check(PowerTypeRegistry.get(id) != null, "Missing upgraded power: " + id);
                    }
                }
            }
        }
        check(upgradeMappings == 10, "All data-driven accessory mappings loaded (spindle uses its projectile hook)");
        check(PowerTypeRegistry.get(new Identifier("shape-shifter-curse", "form_familiar_fox_explosive_charm_paper")) != null,
                "Explosive charm action loaded");
        check(PowerTypeRegistry.get(new Identifier("shape-shifter-curse", "form_snow_fox_bottled_snowfall_plus_tool")) != null,
                "Upgraded snowfall action loaded");

        AltarBlockEntity altar = new AltarBlockEntity(BlockPos.ORIGIN, RegCustomBlock.ALTAR_BLOCK.getDefaultState());
        altar.setWorld(context.getWorld());
        ItemStack oldHook = new ItemStack(RegCustomItem.ATTACH_HOOK);
        oldHook.setCustomName(Text.literal("Must not be inherited"));
        altar.setStack(0, oldHook);
        altar.setStack(9, new ItemStack(RegCustomItem.NIGHT_CATALYST_CORE));
        altar.tick(context.getWorld(), BlockPos.ORIGIN, altar.getCachedState(), altar);
        check(altar.progress == 0, "No progress without fuel");
        altar.setStack(10, new ItemStack(RegCustomItem.UNTREATED_MOONDUST, 16));
        for (int tick = 0; tick < 200; tick++) altar.tick(context.getWorld(), BlockPos.ORIGIN, altar.getCachedState(), altar);
        var saved = altar.createNbt();
        altar = new AltarBlockEntity(BlockPos.ORIGIN, RegCustomBlock.ALTAR_BLOCK.getDefaultState());
        altar.readNbt(saved);
        altar.setWorld(context.getWorld());
        for (int tick = 200; tick < 599; tick++) altar.tick(context.getWorld(), BlockPos.ORIGIN, altar.getCachedState(), altar);
        check(altar.getStack(11).isEmpty(), "Upgrade takes the full 30 seconds");
        altar.tick(context.getWorld(), BlockPos.ORIGIN, altar.getCachedState(), altar);
        check(altar.getStack(11).isOf(RegCustomItem.ATTACH_HOOK_PLUS), "Correct standalone upgrade output");
        check(!altar.getStack(11).hasCustomName(), "No input NBT inheritance");
        check(altar.fuelTime == 0 && altar.getStack(10).isEmpty(), "Exactly 16 moondust consumed");
        check(altar.getStack(9).isOf(RegCustomItem.NIGHT_CATALYST_CORE), "Actual catalyst retained");
        altar.setStack(0, new ItemStack(RegCustomItem.ATTACH_HOOK));
        check(altar.nowRecipe == null, "Do not overstack an upgraded accessory in the output slot");
        System.out.println("Altar recipe checks passed.");
        context.complete();
    }
}
