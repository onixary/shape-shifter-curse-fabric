package net.onixary.shapeShifterCurseFabric.integration.tan;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.TANTemperatureModifierPower;
import net.onixary.shapeShifterCurseFabric.util.util.CachedDataMap;

import java.util.List;
import java.util.UUID;

// 不加载 TAN 时仍可调用里面的函数 但是不会生效
public class TANHelper {
    public static final boolean isLoaded = FabricLoader.getInstance().isModLoaded("toughasnails");

    public static final CachedDataMap<UUID, PlayerEntity, Integer> HotResistance = new CachedDataMap<>(TANHelper::getPlayerHotResistance, Entity::getUuid);
    public static final CachedDataMap<UUID, PlayerEntity, Integer> ColdResistance = new CachedDataMap<>(TANHelper::getPlayerColdResistance, Entity::getUuid);
    public static final CachedDataMap<UUID, PlayerEntity, Integer> TemperatureResistance = new CachedDataMap<>(TANHelper::getPlayerTemperatureResistance, Entity::getUuid);
    public static final CachedDataMap<UUID, PlayerEntity, Integer> TemperatureOffset = new CachedDataMap<>(TANHelper::getPlayerTemperatureOffset, Entity::getUuid);

    static {
        if (isLoaded) {
            TANUtils.init();
        }
    }

    public static int getPlayerHotResistance(PlayerEntity player) {
        List<TANTemperatureModifierPower> powers = PowerHolderComponent.getPowers(player, TANTemperatureModifierPower.class);
        return powers.stream().mapToInt(power -> power.hotResistance).sum();
    }

    public static int getPlayerColdResistance(PlayerEntity player) {
        List<TANTemperatureModifierPower> powers = PowerHolderComponent.getPowers(player, TANTemperatureModifierPower.class);
        return powers.stream().mapToInt(power -> power.coldResistance).sum();
    }

    public static int getPlayerTemperatureResistance(PlayerEntity player) {
        List<TANTemperatureModifierPower> powers = PowerHolderComponent.getPowers(player, TANTemperatureModifierPower.class);
        return powers.stream().mapToInt(power -> power.temperatureResistance).sum();
    }

    public static int getPlayerTemperatureOffset(PlayerEntity player) {
        List<TANTemperatureModifierPower> powers = PowerHolderComponent.getPowers(player, TANTemperatureModifierPower.class);
        return powers.stream().mapToInt(power -> power.temperatureOffset).sum();
    }

    public static void markDataDirty(PlayerEntity player) {
        HotResistance.setDirtyA(player);
        ColdResistance.setDirtyA(player);
        TemperatureResistance.setDirtyA(player);
        TemperatureOffset.setDirtyA(player);
    }

    public static void onServerInit() {
        HotResistance.clear();
        ColdResistance.clear();
        TemperatureResistance.clear();
        TemperatureOffset.clear();
    }
}
