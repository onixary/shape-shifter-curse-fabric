package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.CopyOnWriteArrayList;

public class PendingClientTaskManager {
    private static final CopyOnWriteArrayList<PendingTask> PENDING_TASKS = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<RepeatingTask> REPEATING_TASKS = new CopyOnWriteArrayList<>();
    private static boolean registered = false;
    private static boolean repeatingRegistered = false;
    private static MinecraftClient cachedClient = null;
    private static int clientTickCounter = 0;

    public static void setClient(MinecraftClient client) {
        cachedClient = client;
    }

    public static void queue(int delayTicks, TaskExecutor executor) {
        if (!registered) {
            registered = true;
            ClientTickEvents.END_CLIENT_TICK.register(PendingClientTaskManager::tick);
        }
        if (cachedClient != null) {
            PENDING_TASKS.add(new PendingTask(clientTickCounter + delayTicks, executor));
        }
    }

    public static void queueRepeating(int intervalTicks, int maxRetries, TaskExecutor executor) {
        queueRepeating(intervalTicks, maxRetries, executor, (current, max) -> current < max);
    }

    public static void queueRepeating(int intervalTicks, int maxRetries, TaskExecutor executor, RetryCondition retryCondition) {
        if (!repeatingRegistered) {
            repeatingRegistered = true;
            ClientTickEvents.END_CLIENT_TICK.register(PendingClientTaskManager::tickRepeating);
        }
        if (cachedClient != null) {
            REPEATING_TASKS.add(new RepeatingTask(clientTickCounter + intervalTicks, intervalTicks, maxRetries, executor, retryCondition));
        }
    }

    private static void tick(MinecraftClient client) {
        cachedClient = client;
        clientTickCounter++;
        int currentTick = clientTickCounter;
        PENDING_TASKS.removeIf(task -> {
            if (task.isExpired(currentTick)) {
                return true;
            }
            if (task.shouldExecute(currentTick)) {
                task.execute(client);
                return true;
            }
            return false;
        });

        if (PENDING_TASKS.isEmpty()) {
            registered = false;
        }
    }

    private static void tickRepeating(MinecraftClient client) {
        cachedClient = client;
        clientTickCounter++;
        int currentTick = clientTickCounter;
        REPEATING_TASKS.removeIf(task -> {
            if (task.isExpired(currentTick)) {
                return true;
            }
            if (task.shouldExecute(currentTick)) {
                task.execute(client);
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
        void execute(MinecraftClient client);
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

        public void execute(MinecraftClient client) {
            if (executed) return;
            executed = true;
            executor.execute(client);
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
        private final int expireAfterTicks;
        private int nextExecuteTick;
        private int currentRetry = 0;

        public RepeatingTask(int startTick, int intervalTicks, int maxRetries, TaskExecutor executor, RetryCondition retryCondition) {
            this(startTick, intervalTicks, maxRetries, executor, retryCondition, 600);
        }

        public RepeatingTask(int startTick, int intervalTicks, int maxRetries, TaskExecutor executor, RetryCondition retryCondition, int expireAfterTicks) {
            this.nextExecuteTick = startTick;
            this.intervalTicks = intervalTicks;
            this.maxRetries = maxRetries;
            this.executor = executor;
            this.retryCondition = retryCondition;
            this.expireAfterTicks = expireAfterTicks;
        }

        public boolean shouldExecute(int currentTick) {
            return currentTick >= nextExecuteTick && currentRetry < maxRetries;
        }

        public void execute(MinecraftClient client) {
            executor.execute(client);
            currentRetry++;
            nextExecuteTick = clientTickCounter + intervalTicks;
        }

        public boolean shouldContinue() {
            return retryCondition.shouldRetry(currentRetry, maxRetries);
        }

        public boolean isExpired(int currentTick) {
            return currentTick > nextExecuteTick + expireAfterTicks || currentRetry >= maxRetries;
        }

        public int getCurrentRetry() {
            return currentRetry;
        }
    }
}
