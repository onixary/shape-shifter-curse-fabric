package net.onixary.shapeShifterCurseFabric.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.io.IOException;

public class TrinketDataPackReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(ShapeShifterCurseFabric.MOD_ID, "accessory_power");
    }

    @Override
    public void reload(ResourceManager manager) {
        TrinketUtils.clearAccessoryPower();
        manager.findResources("accessory_power", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
            JsonObject accessoryData;
            try {
                accessoryData = JsonParser.parseString(new String(resource.getInputStream().readAllBytes())).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            TrinketUtils.loadAccessoryPowerData(accessoryData);
        });
    }
}
