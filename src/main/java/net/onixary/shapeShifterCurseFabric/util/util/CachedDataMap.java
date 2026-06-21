package net.onixary.shapeShifterCurseFabric.util.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class CachedDataMap <KEY, ARG, VALUE> {
    public final HashMap<KEY, CachedData<VALUE>> map = new HashMap<>();
    public Function<ARG, VALUE> supplier;
    public @Nullable Function<ARG, KEY> keySupplier = null;

    public CachedDataMap(Function<ARG, VALUE> supplier) {
        this.supplier = supplier;
    }

    public CachedDataMap(Function<ARG, VALUE> supplier, Function<ARG, KEY> keySupplier) {
        this.supplier = supplier;
        this.keySupplier = keySupplier;
    }

    public VALUE get(KEY key, ARG arg) {
        CachedData<VALUE> cachedData = map.get(key);
        if (cachedData == null) {
            cachedData = new CachedData<>(() -> supplier.apply(arg));
            map.put(key, cachedData);
        }
        return cachedData.get();
    }

    public VALUE get(ARG key) {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
        return this.get(keySupplier.apply(key), key);
    }

    public CachedData<VALUE> getCache(KEY key, ARG arg) {
        CachedData<VALUE> cachedData = map.get(key);
        if (cachedData == null) {
            cachedData = new CachedData<>(() -> supplier.apply(arg));
            map.put(key, cachedData);
        }
        return cachedData;
    }

    public CachedData<VALUE> getCache(ARG key) {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
        return this.getCache(keySupplier.apply(key), key);
    }

    public void updateK(KEY key) {
        CachedData<VALUE> cachedData = map.get(key);
        if (cachedData != null) {
            cachedData.update();
        }
    }

    public void updateA(ARG key) {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
        this.updateK(keySupplier.apply(key));
    }

    public void setDirtyK(KEY key) {
        CachedData<VALUE> cachedData = map.get(key);
        if (cachedData != null) {
            cachedData.setDirty();
        }
    }

    public void setDirtyA(ARG key) {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
        this.setDirtyK(keySupplier.apply(key));
    }

    public void set(KEY key, ARG arg, VALUE value) {
        CachedData<VALUE> cachedData = this.getCache(key, arg);
        if (cachedData != null) {
            cachedData.set(value);
        }
    }

    public void set(ARG key, VALUE value) {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
        this.set(keySupplier.apply(key), key, value);
    }

    public void clear() {
        map.clear();
    }
}
