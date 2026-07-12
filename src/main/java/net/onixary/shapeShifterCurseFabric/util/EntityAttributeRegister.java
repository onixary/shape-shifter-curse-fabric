package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class EntityAttributeRegister {
    private static final Map<EntityType<? extends LivingEntity>, Supplier<DefaultAttributeContainer.Builder>> extraAttributes = new java.util.HashMap<>();
    private static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> extraAttributesFinal = new java.util.HashMap<>();

    public static Optional<DefaultAttributeContainer> getAttributes(EntityType<? extends LivingEntity> entityType) {
        if (extraAttributesFinal.containsKey(entityType)) {
            return Optional.ofNullable(extraAttributesFinal.get(entityType));
        } else if (extraAttributes.containsKey(entityType)) {
            extraAttributesFinal.put(entityType, extraAttributes.get(entityType).get().build());
            return Optional.ofNullable(extraAttributesFinal.get(entityType));
        }
        return Optional.empty();
    }

    public static boolean ShouldUseThisSystem() {
        return FabricLoader.getInstance().isModLoaded("changed");
    }

    public static void register(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> builder) {
        if (ShouldUseThisSystem()) {
            extraAttributes.put(entityType, builder);
        }
        else {
            FabricDefaultAttributeRegistry.register(entityType, builder.get());
        }
    }
}
