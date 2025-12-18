package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

public class UseOtherStateAnimController extends AbstractAnimStateControllerDP {
    public @Nullable Identifier otherStateId;

    public UseOtherStateAnimController(@Nullable Identifier otherStateId) {
        super();
        this.otherStateId = otherStateId;
    }

    public UseOtherStateAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    private @Nullable AbstractAnimStateController getOtherStateController(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (this.otherStateId == null) {
            return null;
        }
        return data.playerForm.getAnimStateController(player, data, this.otherStateId);
    }

    @Override
    public boolean isRegistered(PlayerEntity player, AnimSystem.AnimSystemData data) {
        AbstractAnimStateController otherStateController = this.getOtherStateController(player, data);
        if (otherStateController != null) {
            return otherStateController.isRegistered(player, data);
        }
        return true;
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        AbstractAnimStateController otherStateController = this.getOtherStateController(player, data);
        if (otherStateController != null) {
            otherStateController.registerAnim(player, data);
        }
        super.registerAnim(player, data);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        AbstractAnimStateController otherStateController = this.getOtherStateController(player, data);
        if (otherStateController != null) {
            if (!otherStateController.isRegistered(player, data)) {
                otherStateController.registerAnim(player, data);
            }
            return otherStateController.getAnimation(player, data);
        }
        if (ShapeShifterCurseFabric.IsDevelopmentEnvironment()) {  // 由于otherStateId nullable但是逻辑上不推荐为null 所以在开发环境下提示
            ShapeShifterCurseFabric.LOGGER.warn("UseOtherStateAnimController State Not Found: {}", this.otherStateId);
        }
        return null;
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonData) {
        if (jsonData != null && jsonData.has("StateControllerId") && jsonData.get("StateControllerId").isJsonPrimitive())  {
            this.otherStateId = Identifier.tryParse(jsonData.get("StateControllerId").getAsString());
        } else {
            this.otherStateId = null;
        }
        return this;
    }
}
