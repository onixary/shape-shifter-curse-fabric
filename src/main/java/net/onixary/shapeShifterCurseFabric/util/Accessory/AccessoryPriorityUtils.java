package net.onixary.shapeShifterCurseFabric.util.Accessory;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AccessoryPriorityUtils {
    public static String highestPriorityPlugin = null;
    public static @Nullable String getHighestPriorityPlugin() {
        HashMap<String, Integer> priorityMap = new HashMap<>();
        if (highestPriorityPlugin == null) {
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                ModMetadata metadata = mod.getMetadata();
                if (metadata.containsCustomValue("ssc-accessory-priority")) {
                    try {
                        for (Map.Entry<String, CustomValue> value : metadata.getCustomValue("ssc-accessory-priority").getAsObject()) {
                            String PluginName = value.getKey();
                            boolean canLoaded = true;
                            int priority = 1000;
                            CustomValue.CvObject priorityData = value.getValue().getAsObject();
                            if (priorityData.containsKey("priority")) {
                                priority = priorityData.get("priority").getAsNumber().intValue();
                            }
                            if (priorityData.containsKey("depends")) {
                                for (CustomValue depend : priorityData.get("depends").getAsArray()) {
                                    if (!FabricLoader.getInstance().isModLoaded(depend.getAsString())) {
                                        canLoaded = false;
                                    }
                                }
                            }
                            if (priorityData.containsKey("breaks")) {
                                for (CustomValue depend : priorityData.get("breaks").getAsArray()) {
                                    if (FabricLoader.getInstance().isModLoaded(depend.getAsString())) {
                                        canLoaded = false;
                                    }
                                }
                            }
                            if (canLoaded) {
                                priorityMap.put(PluginName, priority);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            int highestPriority = Integer.MIN_VALUE;
            List<String> highestPriorityPlugins = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : priorityMap.entrySet()) {
                if (entry.getValue() > highestPriority) {
                    highestPriority = entry.getValue();
                    highestPriorityPlugins.clear();
                }
                if (entry.getValue() == highestPriority) {
                    highestPriorityPlugins.add(entry.getKey());
                }
            }
            if (!highestPriorityPlugins.isEmpty()) {
                Collections.sort(highestPriorityPlugins);
                highestPriorityPlugin = highestPriorityPlugins.get(0);
            }
        }
        return highestPriorityPlugin;
    }
}
