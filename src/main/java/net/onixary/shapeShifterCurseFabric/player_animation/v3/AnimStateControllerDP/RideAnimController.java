package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.Nullable;

public class RideAnimController extends AbstractAnimStateControllerDP {
    private AnimUtils.AnimationHolderData animationHolderData;
    private AnimationHolder animationHolder = null;
    private AnimUtils.AnimationHolderData RideVehicleAnimationHolderData;
    private AnimationHolder RideVehicleAnimationHolder = null;

    public RideAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public RideAnimController(AnimUtils.AnimationHolderData animationHolderData, AnimUtils.AnimationHolderData RideVehicleAnimationHolderData) {
        super(null);
        this.animationHolderData = animationHolderData;
        this.RideVehicleAnimationHolderData = RideVehicleAnimationHolderData;
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
        if (jsonObject.has("anim") && jsonObject.get("anim").isJsonObject()) {
            this.animationHolderData = AnimUtils.readAnim(jsonObject.get("anim").getAsJsonObject());
        }
        if (jsonObject.has("rideVehicleAnim") && jsonObject.get("rideVehicleAnim").isJsonObject()) {
            this.RideVehicleAnimationHolderData = AnimUtils.readAnim(jsonObject.get("rideVehicleAnim").getAsJsonObject());
        }
        return this;
    }
}
