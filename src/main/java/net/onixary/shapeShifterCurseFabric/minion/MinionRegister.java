package net.onixary.shapeShifterCurseFabric.minion;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.mobs.WolfMinion;
import net.onixary.shapeShifterCurseFabric.minion.mobs.WolfMinionRenderer;
import org.jetbrains.annotations.Nullable;

public class MinionRegister {
    public static final EntityType<WolfMinion> WOLF_MINION = Registry.register(
            Registries.ENTITY_TYPE,
            ShapeShifterCurseFabric.identifier("minion_wolf"),
            FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, WolfMinion::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(WOLF_MINION, WolfMinion.createWolfMinionAttributes());
    }

    public static void registerClient() {
        EntityRendererRegistry.register(WOLF_MINION, WolfMinionRenderer::new);
    }

    public static @Nullable <T extends LivingEntity> T SpawnMinion(EntityType<T> minion, ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
        T entity = minion.spawn(world, pos, SpawnReason.NATURAL);
        if (entity instanceof IMinion<?> minionEntity) {
            minionEntity.InitMinion(player);
            return entity;
        }
        return null;
    }
}
