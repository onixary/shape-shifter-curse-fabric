package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CopyOnWriteArrayList;

public class PendingTaskManager {
    private static final CopyOnWriteArrayList<PendingTask> PENDING_TASKS = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<RepeatingTask> REPEATING_TASKS = new CopyOnWriteArrayList<>();
    private static boolean registered = false;
    private static boolean repeatingRegistered = false;
    private static MinecraftServer cachedServer = null;

    public static void setServer(MinecraftServer server) {
        cachedServer = server;
    }

    public static void queue(int delayTicks, TaskExecutor executor) {
        if (!registered) {
            registered = true;
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(PendingTaskManager::tick);
        }
        if (cachedServer != null) {
            int currentTick = cachedServer.getTicks();
            PENDING_TASKS.add(new PendingTask(currentTick + delayTicks, executor));
        }
    }

    public static void queueRepeating(int intervalTicks, int maxRetries, TaskExecutor executor) {
        queueRepeating(intervalTicks, maxRetries, executor, (current, max) -> current < max);
    }

    public static void queueRepeating(int intervalTicks, int maxRetries, TaskExecutor executor, RetryCondition retryCondition) {
        if (!repeatingRegistered) {
            repeatingRegistered = true;
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(PendingTaskManager::tickRepeating);
        }
        if (cachedServer != null) {
            int currentTick = cachedServer.getTicks();
            REPEATING_TASKS.add(new RepeatingTask(currentTick + intervalTicks, intervalTicks, maxRetries, executor, retryCondition));
        }
    }

    public static void queueRepeatingForPlayer(int intervalTicks, int maxRetries, ServerPlayerEntity player, TaskExecutor executor) {
        queueRepeatingForPlayer(intervalTicks, maxRetries, player, executor, (current, max) -> current < max);
    }

    public static void queueRepeatingForPlayer(int intervalTicks, int maxRetries, ServerPlayerEntity finalPlayer, TaskExecutor executor, RetryCondition retryCondition) {
        if (!repeatingRegistered) {
            repeatingRegistered = true;
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(PendingTaskManager::tickRepeating);
        }
        MinecraftServer server = finalPlayer.getServer();
        if (server != null) {
            int currentTick = server.getTicks();
            TaskExecutor wrappedExecutor = s -> {
                if (finalPlayer.isDisconnected()) return;
                executor.execute(s);
            };
            REPEATING_TASKS.add(new RepeatingTask(currentTick + intervalTicks, intervalTicks, maxRetries, wrappedExecutor, retryCondition));
        }
    }

    public static void queueForPlayer(int delayTicks, ServerPlayerEntity finalPlayer, TaskExecutor executor) {
        if (!registered) {
            registered = true;
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(PendingTaskManager::tick);
        }
        MinecraftServer server = finalPlayer.getServer();
        if (server != null) {
            int currentTick = server.getTicks();
            int targetTick = currentTick + delayTicks;
            PENDING_TASKS.add(new PendingTask(targetTick, s -> {
                if (finalPlayer.isDisconnected()) return;
                executor.execute(s);
            }));
        }
    }

    private static void tick(MinecraftServer server) {
        cachedServer = server;
        int currentTick = server.getTicks();
        PENDING_TASKS.removeIf(task -> {
            if (task.isExpired(currentTick)) {
                return true;
            }
            if (task.shouldExecute(currentTick)) {
                task.execute(server);
                return true;
            }
            return false;
        });

        if (PENDING_TASKS.isEmpty()) {
            registered = false;
        }
    }

    private static void tickRepeating(MinecraftServer server) {
        cachedServer = server;
        int currentTick = server.getTicks();
        REPEATING_TASKS.removeIf(task -> {
            if (task.isExpired(currentTick)) {
                return true;
            }
            if (task.shouldExecute(currentTick)) {
                task.execute(server);
                return !task.shouldContinue();
            }
            return false;
        });

        if (REPEATING_TASKS.isEmpty()) {
            repeatingRegistered = false;
        }
    }

    public static int getPendingCount() {
        return PENDING_TASKS.size() + REPEATING_TASKS.size();
    }

    public interface TaskExecutor {
        void execute(MinecraftServer server);
    }

    public interface RetryCondition {
        boolean shouldRetry(int currentRetry, int maxRetries);
    }

    public static class PendingTask {
        private final int targetTick;
        private final TaskExecutor executor;
        private boolean executed = false;

        public PendingTask(int targetTick, TaskExecutor executor) {
            this.targetTick = targetTick;
            this.executor = executor;
        }

        public boolean shouldExecute(int currentTick) {
            return currentTick >= targetTick && !executed;
        }

        public void execute(MinecraftServer server) {
            if (executed) return;
            executed = true;
            executor.execute(server);
        }

        public boolean isExpired(int currentTick) {
            return currentTick > targetTick + 20;
        }
    }

    public static class RepeatingTask {
        private final int intervalTicks;
        private final int maxRetries;
        private final TaskExecutor executor;
        private final RetryCondition retryCondition;
        private int nextExecuteTick;
        private int currentRetry = 0;
        private boolean completed = false;

        public RepeatingTask(int startTick, int intervalTicks, int maxRetries, TaskExecutor executor, RetryCondition retryCondition) {
            this.nextExecuteTick = startTick;
            this.intervalTicks = intervalTicks;
            this.maxRetries = maxRetries;
            this.executor = executor;
            this.retryCondition = retryCondition;
        }

        public boolean shouldExecute(int currentTick) {
            if (completed) return false;
            return currentTick >= nextExecuteTick && currentRetry < maxRetries;
        }

        public void execute(MinecraftServer server) {
            if (completed) return;
            executor.execute(server);
            currentRetry++;
            nextExecuteTick = server.getTicks() + intervalTicks;
        }

        public boolean shouldContinue() {
            if (completed) return false;
            boolean shouldContinue = retryCondition.shouldRetry(currentRetry, maxRetries);
            if (!shouldContinue) {
                completed = true;
            }
            return shouldContinue;
        }

        public boolean isExpired(int currentTick) {
            return currentTick > nextExecuteTick + 600 || currentRetry >= maxRetries;
        }

        public int getCurrentRetry() {
            return currentRetry;
        }
    }
}
