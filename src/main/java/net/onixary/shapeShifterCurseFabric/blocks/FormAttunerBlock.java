package net.onixary.shapeShifterCurseFabric.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.FormAttunerBlockEntity;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.util.util.CachedDataMap;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FormAttunerBlock extends BlockWithEntity implements Stainable {
    public static final CachedDataMap<UUID, PlayerEntity, BlockPos> playerLastAttunerPos = new CachedDataMap<>((uuid -> null), Entity::getUuid);

    public static @Nullable FormAttunerBlockEntity getPlayerLastUsedAttuner(PlayerEntity player) {
        BlockPos pos = playerLastAttunerPos.get(player);
        if (pos == null) {
            return null;
        }
        // 检查是否被加载
        if (!player.getWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return null;
        }
        return player.getWorld().getBlockEntity(pos) instanceof FormAttunerBlockEntity ? (FormAttunerBlockEntity) player.getWorld().getBlockEntity(pos) : null;
    }

    protected FormAttunerBlock(Settings settings) {
        super(settings);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FormAttunerBlockEntity(pos, state);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.PURPLE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, RegCustomBlock.FORM_ATTUNER_BLOCK_ENTITY, FormAttunerBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            this.openScreen(world, pos, (ServerPlayerEntity) player);
            return ActionResult.CONSUME;
        }
    }

    protected void openScreen(World world, BlockPos pos, ServerPlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FormAttunerBlockEntity formAttunerBlockEntity) {
            playerLastAttunerPos.setA(player, pos);
            ModPacketsS2CServer.sendOpenFormUpgradeMenu(player, formAttunerBlockEntity.level);
        }
    }
}
