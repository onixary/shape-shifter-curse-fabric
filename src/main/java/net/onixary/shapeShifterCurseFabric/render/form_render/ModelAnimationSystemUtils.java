package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public class ModelAnimationSystemUtils {
    public static final HashMap<Identifier, Supplier<IModelAnimationSystem>> modelAnimationSystemRegistry = new HashMap<>();

    public static void register(Identifier id, Supplier<IModelAnimationSystem> supplier) {
        modelAnimationSystemRegistry.put(id, supplier);
    }

    public static @Nullable IModelAnimationSystem get(Identifier id, @Nullable JsonObject json) {
        @Nullable Supplier<IModelAnimationSystem> supplier = modelAnimationSystemRegistry.get(id);
        if (supplier != null) {
            IModelAnimationSystem system = supplier.get();
            system.loadConfig(json);
            return system;
        }
        return null;
    }
}
