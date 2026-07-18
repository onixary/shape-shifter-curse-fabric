package net.onixary.shapeShifterCurseFabric.util.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class CachedDataMap <KEY, ARG, VALUE> implements ICachedDataMap <KEY, ARG, VALUE> {
    public final HashMap<KEY, CachedData<ARG, VALUE>> map = new HashMap<>();
    public @NotNull Function<ARG, VALUE> supplier;
    public @Nullable Function<ARG, KEY> keySupplier = null;

    public CachedDataMap(@NotNull Function<ARG, VALUE> supplier) {
        this.supplier = supplier;
    }

    public CachedDataMap(@NotNull Function<ARG, VALUE> supplier, @Nullable Function<ARG, KEY> keySupplier) {
        this.supplier = supplier;
        this.keySupplier = keySupplier;
    }

    public void _assertNotNullKeySupplier() {
        if (keySupplier == null) {
            throw new IllegalStateException("keySupplier is null!");
        }
    }

    public VALUE get(KEY key, ARG arg) {
        CachedData<ARG, VALUE> cachedData = map.get(key);
        if (cachedData == null) {
            cachedData = new CachedData<>(supplier);
            map.put(key, cachedData);
        }
        return cachedData.get(arg);
    }

    public VALUE get(ARG arg) {
        _assertNotNullKeySupplier();
        return this.get(keySupplier.apply(arg), arg);
    }

    public CachedData<ARG, VALUE> getCacheK(KEY key) {
        CachedData<ARG, VALUE> cachedData = map.get(key);
        if (cachedData == null) {
            cachedData = new CachedData<>(supplier);
            map.put(key, cachedData);
        }
        return cachedData;
    }

    public CachedData<ARG, VALUE> getCacheA(ARG key) {
        _assertNotNullKeySupplier();
        return this.getCacheK(keySupplier.apply(key));
    }

    public void updateK(KEY key, ARG arg) {
        CachedData<ARG, VALUE> cachedData = map.get(key);
        if (cachedData != null) {
            cachedData.update(arg);
        }
    }

    public void updateA(ARG arg) {
        _assertNotNullKeySupplier();
        this.updateK(keySupplier.apply(arg), arg);
    }

    public void markDirtyK(KEY key) {
        CachedData<ARG, VALUE> cachedData = map.get(key);
        if (cachedData != null) {
            cachedData.markDirty();
        }
    }

    public void setDirtyA(ARG key) {
        _assertNotNullKeySupplier();
        this.markDirtyK(keySupplier.apply(key));
    }

    public void setK(KEY key, VALUE value) {
        CachedData<ARG, VALUE> cachedData = this.getCacheK(key);
        if (cachedData != null) {
            cachedData.set(value);
        }
    }

    public void setA(ARG key, VALUE value) {
        _assertNotNullKeySupplier();
        this.setK(keySupplier.apply(key), value);
    }

    public void clear() {
        map.clear();
    }

    public void setSupplier(@NotNull Function<ARG, VALUE> supplier) {
        this.supplier = supplier;
        for (CachedData<ARG, VALUE> cachedData : map.values()) {
            cachedData.setSupplier(supplier);
        }
    }

    public void setKeySupplier(@Nullable Function<ARG, KEY> keySupplier) {
        this.keySupplier = keySupplier;
    }
}
