// src/main/java/net/onixary/shapeShifterCurseFabric/util/ServerTicker.java
package net.onixary.shapeShifterCurseFabric.util;

public class ServerTicker implements ServerTickable {
    private final Runnable task;
    private int ticksRemaining;
    private final boolean runOnce;

    public ServerTicker(Runnable task, int durationTicks, boolean runOnce) {
        this.task = task;
        this.ticksRemaining = durationTicks;
        this.runOnce = runOnce;
    }

    public ServerTicker(Runnable task, int durationTicks) {
        this(task, durationTicks, false);
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