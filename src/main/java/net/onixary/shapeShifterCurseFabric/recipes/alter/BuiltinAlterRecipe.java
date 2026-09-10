package net.onixary.shapeShifterCurseFabric.recipes.alter;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.function.*;

public class BuiltinAlterRecipe extends AlterRecipe {
    public static final HashMap<Identifier, BARecipeConfig> BARecipeConfigMap = new HashMap<>();

    public static record BARecipeConfig (
            BiPredicate<AlterBlockEntity, World> match,
            BiFunction<AlterBlockEntity, DynamicRegistryManager, ItemStack> craft,
            Function<DynamicRegistryManager, ItemStack> virtualOutput,
            Predicate<PlayerEntity> canCraft,
            int recipeTime,
            int fuelUsage,
            Predicate<AlterBlockEntity> isInputsCountEnough,
            Consumer<AlterBlockEntity> consumeInputs,
            Function<AlterBlockEntity, List<ItemStack>> extraOutput
    ) {
        public void register(Identifier id) {
            BARecipeConfigMap.put(id, this);
        }

        public static @Nullable BARecipeConfig get(Identifier id) {
            return BARecipeConfigMap.get(id);
        }
    }

    public static class BARecipeConfigBuilder {
        BiPredicate<AlterBlockEntity, World> match = (alterBlockEntity, world) -> false;
        BiFunction<AlterBlockEntity, DynamicRegistryManager, ItemStack> craft = (alterBlockEntity, registryManager) -> ItemStack.EMPTY;
        Function<DynamicRegistryManager, ItemStack> virtualOutput = registryManager -> ItemStack.EMPTY;
        Predicate<PlayerEntity> canCraft = player -> true;
        int recipeTime = 200;
        int fuelUsage = 1;
        Predicate<AlterBlockEntity> isInputsCountEnough = alterBlockEntity -> true;
        Consumer<AlterBlockEntity> consumeInputs = alterBlockEntity -> {};
        Function<AlterBlockEntity, List<ItemStack>> extraOutput = alterBlockEntity -> List.of();

        public BARecipeConfigBuilder() { }

        public BARecipeConfigBuilder match(BiPredicate<AlterBlockEntity, World> match) { this.match = match; return this; }
        public BARecipeConfigBuilder craft(BiFunction<AlterBlockEntity, DynamicRegistryManager, ItemStack> craft) { this.craft = craft; return this; }
        public BARecipeConfigBuilder virtualOutput(Function<DynamicRegistryManager, ItemStack> virtualOutput) { this.virtualOutput = virtualOutput; return this; }
        public BARecipeConfigBuilder canCraft(Predicate<PlayerEntity> canCraft) { this.canCraft = canCraft; return this; }
        public BARecipeConfigBuilder recipeTime(int recipeTime) { this.recipeTime = recipeTime; return this; }
        public BARecipeConfigBuilder fuelUsage(int fuelUsage) { this.fuelUsage = fuelUsage; return this; }
        public BARecipeConfigBuilder isInputsCountEnough(Predicate<AlterBlockEntity> isInputsCountEnough) { this.isInputsCountEnough = isInputsCountEnough; return this; }
        public BARecipeConfigBuilder consumeInputs(Consumer<AlterBlockEntity> consumeInputs) { this.consumeInputs = consumeInputs; return this; }
        public BARecipeConfigBuilder extraOutput(Function<AlterBlockEntity, List<ItemStack>> extraOutput) { this.extraOutput = extraOutput; return this; }
        public BARecipeConfig build() { return new BARecipeConfig(match, craft, virtualOutput, canCraft, recipeTime, fuelUsage, isInputsCountEnough, consumeInputs, extraOutput); }

        // TODO 还差几个预设生成器 比如match函数 让它支持Shape和Shapeless

        // 坏了 还得整TriPredicate TriFunction 顺带在整个TriConsumer吧 函数还得传配方自身
        public static BiPredicate<AlterBlockEntity, World> createMatch_Shapeless(DefaultedList<Ingredient> input, Ingredient catalyst) {
            return (alterBlockEntity, world) -> {
                if (catalyst != null) {
                    ItemStack itemStack = alterBlockEntity.getStack(9);
                    if (!catalyst.test(itemStack)) {
                        return false;
                    }
                }

                RecipeMatcher recipeMatcher = new RecipeMatcher();
                int i = 0;
                for(int j = 0; j < 9; ++j) {
                    ItemStack itemStack = alterBlockEntity.getStack(j);
                    if (!itemStack.isEmpty()) {
                        ++i;
                        recipeMatcher.addInput(itemStack, 1);
                    }
                }

                // return i == input.size() && recipeMatcher.match(this, (IntList)null);
                return false;
            };
        }
    }

    public final Identifier id;
    public final BARecipeConfig recipeConfig;

    public BuiltinAlterRecipe(Identifier id, BARecipeConfig recipeConfig) {
        this.id = id;
        this.recipeConfig = recipeConfig;
    }

    @Override
    public int recipeTime() {
        return this.recipeConfig.recipeTime;
    }

    @Override
    public boolean matches(SidedInventory inventory, World world) {
        if (inventory instanceof AlterBlockEntity alterBlockEntity) {
            return this.recipeConfig.match.test(alterBlockEntity, world);
        }
        return false;
    }

    @Override
    public ItemStack craft(SidedInventory inventory, DynamicRegistryManager registryManager) {
        if (inventory instanceof AlterBlockEntity alterBlockEntity) {
            return this.recipeConfig.craft.apply(alterBlockEntity, registryManager);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.recipeConfig.virtualOutput.apply(registryManager);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean canCraft(@Nullable PlayerEntity player) {
        return this.recipeConfig.canCraft.test(player);
    }

    @Override
    public boolean InputsCountEnough(SidedInventory inventory) {
        if (inventory instanceof AlterBlockEntity alterBlockEntity) {
            return this.recipeConfig.isInputsCountEnough.test(alterBlockEntity);
        }
        return false;
    }

    @Override
    public void consumeInputs(SidedInventory inventory) {
        if (inventory instanceof AlterBlockEntity alterBlockEntity) {
            this.recipeConfig.consumeInputs.accept(alterBlockEntity);
        }
    }

    @Override
    public List<ItemStack> getExtraOutput(SidedInventory inventory) {
        if (inventory instanceof AlterBlockEntity alterBlockEntity) {
            return this.recipeConfig.extraOutput.apply(alterBlockEntity);
        }
        return List.of();
    }

    @Override
    public int fuelUsage() {
        return this.recipeConfig.fuelUsage;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegister.BUILTIN_ALTER_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<BuiltinAlterRecipe> {
        public BuiltinAlterRecipe read(Identifier identifier, JsonObject jsonObject) {
            String configRaw = JsonHelper.getString(jsonObject, "recipe_config_id", "");
            if (configRaw.isEmpty()) {
                throw new JsonSyntaxException("recipe_config_id is required");
            }
            Identifier configId = Identifier.tryParse(configRaw);
            if (configId == null) {
                throw new JsonSyntaxException("recipe_config_id must be a valid identifier");
            }
            BARecipeConfig recipeConfig = BuiltinAlterRecipe.BARecipeConfig.get(configId);
            return new BuiltinAlterRecipe(identifier, recipeConfig);
        }

        public BuiltinAlterRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Identifier configId = packetByteBuf.readIdentifier();
            if (configId == null) {
                throw new RuntimeException("recipe_config_id must be a valid identifier");
            }
            BARecipeConfig recipeConfig = BuiltinAlterRecipe.BARecipeConfig.get(configId);
            if (recipeConfig == null) {
                throw new RuntimeException("recipe_config_id must be a valid identifier");
            }
            return new BuiltinAlterRecipe(identifier, recipeConfig);
        }

        public void write(PacketByteBuf packetByteBuf, BuiltinAlterRecipe alterRecipe) {
            packetByteBuf.writeIdentifier(alterRecipe.getId());
        }
    }
}
