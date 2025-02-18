// src/main/java/net/onixary/shapeShifterCurseFabric/util/ServerTicker.java
package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ClientTicker implements ClientTickable {
    private final MinecraftClient client;
    private final Runnable task;
    private int ticksRemaining;

    public ClientTicker(MinecraftClient client, Runnable task, int durationTicks) {
        this.client = client;
        this.task = task;
        this.ticksRemaining = durationTicks;
    }

    @Override
    public void tick() {
        //ShapeShifterCurseFabric.LOGGER.info("Client ticker tick");
        if (ticksRemaining > 0) {
            task.run();
            ticksRemaining--;
        } else {
            TickManager.removeTickable(this);
        }
    }

    public void start() {
        TickManager.addTickable(this);
    }
}
