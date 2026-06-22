package net.onixary.shapeShifterCurseFabric.util.util;

// 此目录仅放置一些数据类

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CachedData <ARG, VALUE> {
    public boolean isDirty = false;
    public @Nullable VALUE data;
    public @NotNull Function<ARG, VALUE> supplier;

    public CachedData(@NotNull Function<ARG, VALUE> supplier) {
        this.supplier = supplier;
        this.data = null;
        this.markDirty();
    }

    public @Nullable VALUE get(ARG arg) {
        if (isDirty) {
            this.update(arg);
        }
        return data;
    }

    public CachedData<ARG, VALUE> markDirty() {
        isDirty = true;
        return this;
    }

    public CachedData<ARG, VALUE> update(ARG arg) {
        data = supplier.apply(arg);
        isDirty = false;
        return this;
    }

    public CachedData<ARG, VALUE> set(@Nullable VALUE data) {
        this.data = data;
        isDirty = false;
        return this;
    }

    public CachedData<ARG, VALUE> setSupplier(@NotNull Function<ARG, VALUE> supplier) {
        this.supplier = supplier;
        isDirty = true;
        return this;
    }
}
