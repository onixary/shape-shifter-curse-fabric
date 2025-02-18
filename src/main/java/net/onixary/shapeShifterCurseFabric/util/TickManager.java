package net.onixary.shapeShifterCurseFabric.util;

import java.util.ArrayList;
import java.util.List;

public class TickManager {
    private static final List<ServerTickable> serverTickables = new ArrayList<>();
    private static final List<ClientTickable> clientTickables = new ArrayList<>();

    public static void addTickable(ServerTickable tickable) {
        serverTickables.add(tickable);
    }

    public static void removeTickable(ServerTickable tickable) {
        serverTickables.remove(tickable);
    }

    public static void addTickable(ClientTickable tickable) {
        clientTickables.add(tickable);
    }

    public static void removeTickable(ClientTickable tickable) {
        clientTickables.remove(tickable);
    }

    public static void tickServerAll() {
        for (ServerTickable tickable : new ArrayList<>(serverTickables)) {
            tickable.tick();
        }
    }

    public static void tickClientAll() {
        for (ClientTickable tickable : new ArrayList<>(clientTickables)) {
            tickable.tick();
        }
    }

}
