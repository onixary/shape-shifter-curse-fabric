package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Optional;

public class SSCCustomDataReader {
    public static final String Channel;
    public static final String ChannelVersion;

    /*
  "custom": {
    "ssc-data": {
      "Channel": "${CHANNEL}",
      "ChannelVersion": "${CHANNEL_VERSION}"
    }
  }
     */

    static {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("shape-shifter-curse");
        if (container.isEmpty()) {
            ShapeShifterCurseFabric.LOGGER.error("ShapeShifterCurseFabric ModContainer is not found!");  // 除非哪个小天才直接复制代码连改都没改 否则不应该到这个分支
        }
        ModContainer modContainer = container.get();
        ModMetadata metadata = modContainer.getMetadata();
        if (metadata.containsCustomValue("ssc-data")) {
            CustomValue.CvObject customValue = metadata.getCustomValue("ssc-data").getAsObject();
            if (customValue.containsKey("Channel")) {
                Channel = customValue.get("Channel").getAsString();
            } else {
                Channel = "ERROR";
            }
            if (customValue.containsKey("ChannelVersion")) {
                ChannelVersion = customValue.get("ChannelVersion").getAsString();
            } else {
                ChannelVersion = "ERROR";
            }
        } else {
            Channel = "ERROR";
            ChannelVersion = "ERROR";
        }
    }
}
