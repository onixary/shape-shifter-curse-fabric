package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;

public class FSMUtils {
    public static @Nullable Identifier ProcessUniversalAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (player.isSleeping()) {
            return ANIM_STATE_SLEEP;
        }
        if (player.hasVehicle()) {
            Entity Vehicle = player.getVehicle();
            if (Vehicle instanceof BoatEntity || Vehicle instanceof MinecartEntity)  {
                return ANIM_STATE_RIDE_VEHICLE;
            }
            return ANIM_STATE_RIDE;
        }
        return null;
    }
}
