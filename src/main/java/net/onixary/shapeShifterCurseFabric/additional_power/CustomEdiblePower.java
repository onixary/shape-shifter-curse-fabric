package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;

import java.util.List;

public class CustomEdiblePower extends Power {

    private final FoodComponent foodComponent;
    private final List<Identifier> ItemIdList;


    public CustomEdiblePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.ItemIdList = data.get("item_id_list");
        FoodComponent.Builder foodComponentBuilder = new FoodComponent.Builder()
                .hunger(data.getInt("hunger"))
                .saturationModifier(data.getFloat("saturation_modifier"));
        if (data.getBoolean("meat")) {
            foodComponentBuilder.meat();
        }
        if (data.getBoolean("always_edible")) {
            foodComponentBuilder.alwaysEdible();
        }
        if (data.getBoolean("snack")) {
            foodComponentBuilder.snack();
        }
        List<StatusEffectInstance> effects = data.get("status_effects");
        if (effects != null) {
            for (StatusEffectInstance effect : effects) {
                // 应该使用这个功能的都是需求100%触发效果的 所以这里直接1.0f
                foodComponentBuilder.statusEffect(effect, 1.0f);
            }
        }
        this.foodComponent = foodComponentBuilder.build();
    }

    public List<Identifier> getItemIdList() {
        return this.ItemIdList;
    }

    public FoodComponent getFoodComponent() {
        return this.foodComponent;
    }

    public void AddRegistry() {
        // ShapeShifterCurseFabric.LOGGER.info("Added power custom_edible to {}", this.entity.getName());
        if (this.entity instanceof PlayerEntity playerEntity) {
            CustomEdibleUtils.addCustomEdibleWithList(playerEntity, this.getItemIdList(), this.getFoodComponent());
            if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                ModPacketsS2CServer.sendCustomEdibleList(serverPlayerEntity, this.getItemIdList(), this.getFoodComponent());
            }
        }
    }

    public void ClearRegistry() {
        // ShapeShifterCurseFabric.LOGGER.info("Removed power custom_edible from {}", this.entity.getName());
        if (this.entity instanceof PlayerEntity playerEntity) {
            CustomEdibleUtils.clearCustomEdibleWithList(playerEntity, this.getItemIdList());
            if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                ModPacketsS2CServer.sendClearEdibleList(serverPlayerEntity, this.getItemIdList());
            }
        }
    }

    public void onAdded() {
        super.onAdded();
        // 由于修改能力时先添加能力后删除能力 所以这里延迟1秒 防止删除能力时清空不该清空的数据
        // 如果之后不用Power驱动 改为形态驱动这个1秒可以去掉
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.AddRegistry();
        }).start();
    }

    public void onRemoved() {
        super.onRemoved();
        this.ClearRegistry();
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("custom_edible"),
                new SerializableData()
                        .add("item_id_list", SerializableDataTypes.IDENTIFIERS, null)
                        .add("hunger", SerializableDataTypes.INT, 0)
                        .add("saturation_modifier", SerializableDataTypes.FLOAT, 0.0f)
                        .add("meat", SerializableDataTypes.BOOLEAN, false)
                        .add("always_edible", SerializableDataTypes.BOOLEAN, false)
                        .add("snack", SerializableDataTypes.BOOLEAN, false)
                        .add("status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
                data -> (type, entity) -> new CustomEdiblePower(type, entity, data)
        );
    }
}