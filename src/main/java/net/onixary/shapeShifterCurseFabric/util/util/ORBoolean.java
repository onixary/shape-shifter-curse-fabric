package net.onixary.shapeShifterCurseFabric.util.util;

import java.util.HashMap;
import java.util.Map;

public class ORBoolean {
    private final boolean NOT;
    private boolean value;
    private final Map<String, Boolean> dataMap = new HashMap<>();

    private int trueCount = 0;

    public ORBoolean(boolean NOT) {
        this.NOT = NOT;
        this.value = NOT;
    }

    public boolean get() {
        return this.value;
    }

    public void set(String key, boolean newValue) {
        Boolean prev = dataMap.get(key);
        if (prev != null && prev == newValue) {
            return;
        }
        dataMap.put(key, newValue);
        if (Boolean.TRUE.equals(prev)) trueCount--;
        if (newValue) trueCount++;
        boolean newResult = NOT ^ (trueCount > 0);
        if (this.value != newResult) {
            this.value = newResult;
        }
    }

    public void clear() {
        if (dataMap.isEmpty() && trueCount == 0 && this.value == NOT) {
            return;
        }
        dataMap.clear();
        trueCount = 0;
        if (this.value != NOT) {
            this.value = NOT;
        }
    }

    public void on(String key) {
        set(key, true);
    }

    public void off(String key) {
        set(key, false);
    }

    public Map<String, Boolean> __getDataMap() {
        return dataMap;
    }
}