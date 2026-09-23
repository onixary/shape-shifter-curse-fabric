package net.onixary.shapeShifterCurseFabric.util.test;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.util.DataDumper;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetWorkTest {
    public static HashMap<Identifier, AtomicInteger> packetCounter = new HashMap<>();

    public static void init() {
        // ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
        //     dumpData();
        // });
    }

    public static void dumpData() {
        JsonObject json = new JsonObject();
        for (Identifier id : packetCounter.keySet()) {
            json.addProperty(id.toString(), packetCounter.get(id).intValue());
        }
        DataDumper.ENABLE_DUMPER = true;
        DataDumper.dumpJson(json,  FabricLoader.getInstance().getConfigDir().resolve("ssc_dump/network.json"));
    }
}
