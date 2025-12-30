package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class DiggingBareHandCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof PlayerEntity playerEntity)) {
            return false;
        }

        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (!serverPlayerEntity.interactionManager.mining) {
                return false;
            }

        } else if (playerEntity instanceof ClientPlayerEntity) {
            ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
            if (interactionManager == null || !interactionManager.isBreakingBlock()) {
                return false;
            }
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
                ShapeShifterCurseFabric.identifier("barehand_digging"),
                new SerializableData(),
                DiggingBareHandCondition::condition
        );
    }

}
