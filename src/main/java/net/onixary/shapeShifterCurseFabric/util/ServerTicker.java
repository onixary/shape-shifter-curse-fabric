// src/main/java/net/onixary/shapeShifterCurseFabric/util/ServerTicker.java
package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.server.MinecraftServer;

public class ServerTicker implements ServerTickable {
    private final MinecraftServer server;
    private final Runnable task;
    private int ticksRemaining;

    public ServerTicker(MinecraftServer server, Runnable task, int durationTicks) {
        this.server = server;
        this.task = task;
        this.ticksRemaining = durationTicks;
    }

    @Override
    public void tick() {
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
