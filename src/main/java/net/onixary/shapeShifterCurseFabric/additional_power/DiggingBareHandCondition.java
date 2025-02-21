package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.mixin.ClientPlayerInteractionManagerAccessor;
import io.github.apace100.apoli.mixin.ServerPlayerInteractionManagerAccessor;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class DiggingBareHandCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof PlayerEntity playerEntity)) {
            return false;
        }

        BlockState miningBlockState;
        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {

            ServerPlayerInteractionManagerAccessor interactionManager = (ServerPlayerInteractionManagerAccessor) serverPlayerEntity.interactionManager;
            if (!interactionManager.getMining()) {
                return false;
            }

            miningBlockState = entity.getWorld().getBlockState(interactionManager.getMiningPos());

        } else if (playerEntity instanceof ClientPlayerEntity) {

            ClientPlayerInteractionManagerAccessor interactionManager = (ClientPlayerInteractionManagerAccessor) MinecraftClient.getInstance().interactionManager;
            if (interactionManager == null || !interactionManager.getBreakingBlock()) {
                return false;
            }

            miningBlockState = entity.getWorld().getBlockState(interactionManager.getCurrentBreakingPos());

        } else {
            return false;
        }

        if(playerEntity.getInventory().getMainHandStack().isEmpty()){
            return true;
        }
        else if(playerEntity.getInventory().getMainHandStack().getItem() instanceof ToolItem toolItem){
            return toolItem.getMaterial().getMiningLevel() <= 0;
        }

        return true;

    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                Apoli.identifier("barehand_digging"),
                new SerializableData(),
                DiggingBareHandCondition::condition
        );
    }

}
