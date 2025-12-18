package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RideAnimController extends AbstractAnimStateControllerDP {
    private AnimUtils.AnimationHolderData animationHolderData;
    private @Nullable AnimationHolder animationHolder = null;
    private AnimUtils.AnimationHolderData RideVehicleAnimationHolderData;
    private @Nullable AnimationHolder RideVehicleAnimationHolder = null;

    public RideAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public RideAnimController(@Nullable AnimUtils.AnimationHolderData animationHolderData, @Nullable AnimUtils.AnimationHolderData RideVehicleAnimationHolderData) {
        super();
        this.animationHolderData = AnimUtils.ensureAnimHolderDataNotNull(animationHolderData);
        this.RideVehicleAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(RideVehicleAnimationHolderData);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.getVehicle() instanceof BoatEntity || player.getVehicle() instanceof MinecartEntity) {
            return RideVehicleAnimationHolder;
        } else {
            return animationHolder;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        this.RideVehicleAnimationHolder = this.RideVehicleAnimationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.RideVehicleAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "rideVehicleAnim", null);
        return this;
    }
}
