package net.onixary.shapeShifterCurseFabric.util.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface ICachedDataMap <KEY, ARG, VALUE> {
    VALUE get(KEY key, ARG arg);

    VALUE get(ARG arg);

    CachedData<ARG, VALUE> getCacheK(KEY key);

    CachedData<ARG, VALUE> getCacheA(ARG key);

    void updateK(KEY key, ARG arg);

    void updateA(ARG arg);

    void markDirtyK(KEY key);

    void setDirtyA(ARG key);

    void setK(KEY key, VALUE value);

    void setA(ARG key, VALUE value);

    void clear();

    void setSupplier(@NotNull Function<ARG, VALUE> supplier);

    void setKeySupplier(@Nullable Function<ARG, KEY> keySupplier);
}
