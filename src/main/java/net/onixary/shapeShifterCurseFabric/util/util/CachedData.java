package net.onixary.shapeShifterCurseFabric.util.util;

// 此目录仅放置一些数据类

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CachedData <T> {
    public boolean isDirty = false;
    public @Nullable T data;
    public @NotNull Supplier<T> supplier;

    public CachedData(@Nullable T data, @NotNull Supplier<T> supplier) {
        this.data = data;
        this.supplier = supplier;
    }

    public CachedData(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
        this.data = supplier.get();
    }

    public @Nullable T get() {
        if (isDirty) {
            this.update();
        }
        return data;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void update() {
        data = supplier.get();
        isDirty = false;
    }

    public void set(@Nullable T data) {
        this.data = data;
        isDirty = false;
    }

    public void setSupplier(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
        isDirty = true;
    }
}
