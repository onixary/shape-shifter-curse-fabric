package net.onixary.shapeShifterCurseFabric.minion;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity;
import org.jetbrains.annotations.Nullable;

public class MinionRegister {
    public static final EntityType<AnubisWolfMinionEntity> ANUBIS_WOLF_MINION = Registry.register(
            Registries.ENTITY_TYPE,
            AnubisWolfMinionEntity.MinionID,
            FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, AnubisWolfMinionEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(ANUBIS_WOLF_MINION, AnubisWolfMinionEntity.createWolfMinionAttributes());
    }


    public static void DisSpawnAllMinion(PlayerEntity player) {
        if (player instanceof IPlayerEntityMinion minionPlayer) {
            minionPlayer.shape_shifter_curse$clearAllMinions();
        }
    }

    public static @Nullable <T extends LivingEntity> T SpawnMinion(EntityType<T> minion, ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
        T entity = minion.spawn(world, pos, SpawnReason.NATURAL);
        if (entity instanceof IMinion<?> minionEntity) {
            minionEntity.InitMinion(player);
            return entity;
        }
        return null;
    }

    public static void SetCoolDown(Identifier MinionID, PlayerEntity player) {
        if (player instanceof IPlayerEntityMinion minionPlayer) {
            minionPlayer.shape_shifter_curse$applyCooldown(MinionID, player.age);
        }
    }

    public static boolean IsInCoolDown(Identifier MinionID, PlayerEntity player, int Cooldown) {
        if (Cooldown <= 0) {
            return false;
        }
        if (player instanceof IPlayerEntityMinion minionPlayer) {
            long LastCooldown = minionPlayer.shape_shifter_curse$getCooldownTime(MinionID);
            if (LastCooldown == 0) {  // 没召唤过
                return false;
            }
            if (LastCooldown > player.age) {
                minionPlayer.shape_shifter_curse$applyCooldown(MinionID, 0);  // player.age会刷新
                return false;
            }
            return LastCooldown + Cooldown >= player.age;
        }
        return true;
    }

    public static void ResetPlayerCoolDown(PlayerEntity player) {
        if (player instanceof IPlayerEntityMinion minionPlayer) {
            minionPlayer.shape_shifter_curse$resetAllCooldown();
        }
    }

    private static boolean IsSpaceEmpty(World world, BlockPos pos) {
        return world.isAir(pos) || world.isWater(pos);
    }

    private static boolean IsSpaceEmpty(World world, BlockPos pos, int height) {
        for (int i = 0; i < height; i++) {
            if (!IsSpaceEmpty(world, pos)) {
                return false;
            }
            pos = pos.up();
        }
        return true;
    }

    private static int RandomInt(Random randomSource, int min, int max) {
        return randomSource.nextInt(max - min + 1) + min;
    }

    public static @Nullable BlockPos getNearbyEmptySpace(World world, Random randomSource, BlockPos startPos, int XZRange, int YRange, int SpaceHeight, int MaxTry) {
        for (int i = 0; i < MaxTry; i++) {
            int x = RandomInt(randomSource, -XZRange, XZRange);
            int z = RandomInt(randomSource, -XZRange, XZRange);
            int y = RandomInt(randomSource, -YRange, YRange);
            BlockPos pos = startPos.add(x, y, z);
            for (int j = 0; j < YRange; j++) {
                if (!IsSpaceEmpty(world, pos)) {
                    pos = pos.up();
                }
                else if (IsSpaceEmpty(world, pos.down())) {
                    pos = pos.down();
                }
                else {
                    break;
                }
            }
            if (IsSpaceEmpty(world, pos, SpaceHeight)) {
                return pos;
            }
            continue;
        }
        return null;
    }
}
