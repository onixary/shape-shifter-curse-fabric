package net.onixary.shapeShifterCurseFabric.additional_power;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class EatEntityPower extends Power {
    public boolean mustEmptyHand = true;
    public HashMap<Identifier, FoodComponent> entityFoodMap;

    public EatEntityPower(PowerType<?> type, LivingEntity entity, boolean mustEmptyHand, HashMap<Identifier, FoodComponent> entityFoodMap) {
        super(type, entity);
        this.mustEmptyHand = mustEmptyHand;
        this.entityFoodMap = entityFoodMap;
    }

    public boolean onUseEntity(Entity targetEntity, Hand playerHand, EntityHitResult hitResult) {
        if (targetEntity == null || !targetEntity.isAlive() || !(entity instanceof PlayerEntity player) || !this.isActive()) {
            return false;
        }
        if (this.mustEmptyHand && !player.getStackInHand(playerHand).isEmpty()) {
            return false;
        }
        @Nullable FoodComponent foodComponent = entityFoodMap.get(EntityType.getId(targetEntity.getType()));
        World world = player.getWorld();
        if (foodComponent != null && player.canConsume(foodComponent.isAlwaysEdible())) {
            player.getHungerManager().add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
            for(Pair<StatusEffectInstance, Float> pair : foodComponent.getStatusEffects()) {
                if (!world.isClient && pair.getFirst() != null && world.random.nextFloat() < (Float)pair.getSecond()) {
                    player.addStatusEffect(new StatusEffectInstance((StatusEffectInstance)pair.getFirst()));
                }
            }
            world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            targetEntity.discard();
            return true;
        }
        return false;
    }

    public record FoodPair(Identifier entity, FoodComponent food) {}

    public static final SerializableDataType<FoodPair> ENTITY_FOOD_PAIR = SerializableDataType.compound(
            FoodPair.class,
            new SerializableData()
                    .add("entity", SerializableDataTypes.IDENTIFIER)
                    .add("food", SerializableDataTypes.FOOD_COMPONENT),
            (serializableData) -> new FoodPair(serializableData.get("entity"), serializableData.get("food")),
            (serializableData, pair) -> {
                SerializableData.Instance inst = serializableData.new Instance();
                inst.set("entity", pair.entity());
                inst.set("food", pair.food());
                return inst;
            }
    );

    public static final SerializableDataType<List<FoodPair>> ENTITY_FOOD_PAIR_LIST = SerializableDataType.list(ENTITY_FOOD_PAIR);

    static {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player != null) {
                for (EatEntityPower power : PowerHolderComponent.getPowers(player, EatEntityPower.class)) {
                    if (power.onUseEntity(entity, hand, hitResult)) {
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("eat_entity"),
                new SerializableData()
                        .add("must_empty_hand", SerializableDataTypes.BOOLEAN, true)
                        .add("food_map", ENTITY_FOOD_PAIR_LIST, null),
                data -> (powerType, livingEntity) -> {
                    @Nullable List<FoodPair> foodMap = data.get("food_map");
                    HashMap<Identifier, FoodComponent> entityFoodMap = new HashMap<>();
                    if (foodMap != null) {
                        for (FoodPair pair : foodMap) {
                            entityFoodMap.put(pair.entity(), pair.food());
                        }
                    }
                    return new EatEntityPower(powerType, livingEntity, data.get("must_empty_hand"), entityFoodMap);
                }
        ).allowCondition();
    }
}
