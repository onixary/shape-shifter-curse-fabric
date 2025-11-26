package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
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

    // 每Tick仅会运行一次 无论多少个Power
    public static void OnClientTick(PlayerEntity player) {
        // 5s 更新1次 会导致悦灵变形到其他形态时还可以吃一个紫水晶碎片(在特定时机)
        if (player.age % 100 == 0) {
            CustomEdibleUtils.ReloadPlayerCustomEdible(player);
            // 如果启用这个和服务器端的Logger后发现Server和Client的Logger同时输出 不用想肯定是Bug
            // ShapeShifterCurseFabric.LOGGER.info("Reload Player Custom Edible For {} In Client", player.getName().getString());
        }
    }

    // 每Tick仅会运行玩家数量次 无论多少个Power
    public static void OnServerTick(ServerPlayerEntity player) {
        // 防止在单人游戏里运行两次
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        // 5s 更新1次 会导致悦灵变形到其他形态时还可以吃一个紫水晶碎片(在特定时机)
        if (player.age % 100 == 0) {
            CustomEdibleUtils.ReloadPlayerCustomEdible(player);
            // 如果启用这个和客户端的Logger后发现Server和Client的Logger同时输出 不用想肯定是Bug
            // ShapeShifterCurseFabric.LOGGER.info("Reload Player Custom Edible For {} In Server", player.getName().getString());
        }
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