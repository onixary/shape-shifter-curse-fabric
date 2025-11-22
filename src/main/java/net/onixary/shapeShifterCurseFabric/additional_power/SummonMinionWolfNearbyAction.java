package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.minion.mobs.WolfMinion;
import org.jetbrains.annotations.Nullable;


public class SummonMinionWolfNearbyAction {
    public static int RandomInt(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static boolean IsEmptySpace(World world, BlockPos blockPos) {
        return world.isAir(blockPos) || world.isWater(blockPos);
    }

    public static @Nullable BlockPos FindCanSpawn(World world, BlockPos blockPos) {
        BlockPos nextPos = blockPos;
        for (int i = 0; i < 4; i++) {
            if (IsEmptySpace(world, nextPos)) {
                if (!IsEmptySpace(world, nextPos.down())) {
                    return nextPos;
                }
                nextPos = nextPos.down();
            }
        }
        return null;
    }

    public static void action(SerializableData.Instance data, Pair<Entity, Entity> entities) {
        Entity Owner = entities.getLeft();
        Entity SpawnNearbyTarget = entities.getRight();
        int MinionLevel = data.getInt("minion_level");
        int MinionCount = data.getInt("count");
        int MaxMinionCount = data.getInt("max_minion_count");
        if (Owner instanceof ServerPlayerEntity player) {
            for (int i = 0; i < MinionCount; i++) {
                if (player instanceof IPlayerEntityMinion playerEntityMinion) {
                    if (playerEntityMinion.shape_shifter_curse$getMinionsCount(WolfMinion.MinionID) > MaxMinionCount) {
                        return;
                    }
                }
                else {
                    ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, player is not IPlayerEntityMinion");
                    return;
                }
                BlockPos targetPos = null;
                for (int nowTry = 0; nowTry < 4; nowTry++) {
                    int j = RandomInt(player.getRandom(), -3, 3);
                    int k = RandomInt(player.getRandom(), -1, 1);
                    int l = RandomInt(player.getRandom(), -3, 3);
                    targetPos = SpawnNearbyTarget.getBlockPos().add(j, k, l);
                    targetPos = FindCanSpawn(SpawnNearbyTarget.getWorld(), targetPos);
                    if (targetPos == null) {
                        continue;
                    }
                }
                if (targetPos == null) {
                    targetPos = SpawnNearbyTarget.getBlockPos();
                }
                if (SpawnNearbyTarget.getWorld() instanceof ServerWorld world) {
                    WolfMinion wolfMinion = MinionRegister.SpawnMinion(MinionRegister.WOLF_MINION, world, targetPos, player);
                    if (wolfMinion != null) {
                        wolfMinion.setMinionLevel(MinionLevel);
                    } else {
                        ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, wolfMinion is null");
                    }
                } else {
                    ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, world is not ServerWorld");
                }
            }
        }
    }

    public static ActionFactory<Pair<Entity, Entity>> createBIFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("bi_summon_wolf_minion"),
                new SerializableData()
                        .add("minion_level", SerializableDataTypes.INT, 1)
                        .add("count", SerializableDataTypes.INT, 1)
                        .add("max_minion_count", SerializableDataTypes.INT, Integer.MAX_VALUE),
                SummonMinionWolfNearbyAction::action
        );
    }

    public static ActionFactory<Entity> createFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("summon_wolf_minion"),
                new SerializableData()
                        .add("minion_level", SerializableDataTypes.INT, 1)
                        .add("count", SerializableDataTypes.INT, 1)
                        .add("max_minion_count", SerializableDataTypes.INT, Integer.MAX_VALUE),
                (data, entity) -> {action(data, new Pair<>(entity, entity));}
        );
    }
}
