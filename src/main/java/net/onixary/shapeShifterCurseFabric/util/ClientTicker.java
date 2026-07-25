// src/main/java/net/onixary/shapeShifterCurseFabric/util/ServerTicker.java
package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ClientTicker implements ClientTickable {
    private final MinecraftClient client;
    private final Runnable task;
    private int ticksRemaining;
    private final boolean runOnce;

    public ClientTicker(MinecraftClient client, Runnable task, int durationTicks, boolean runOnce) {
        this.client = client;
        this.task = task;
        this.ticksRemaining = durationTicks;
        this.runOnce = runOnce;
    }

    public ClientTicker(MinecraftClient client, Runnable task, int durationTicks) {
        this(client, task, durationTicks, false);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) {
            if (!runOnce) {
                task.run();
            }
            ticksRemaining--;
            if (runOnce && ticksRemaining == 0) {
                task.run();
                TickManager.removeTickable(this);
            }
        } else {
            TickManager.removeTickable(this);
        }
    }

    public void start() {
        TickManager.addTickable(this);
    }
}