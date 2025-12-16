package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.Nullable;

public class ClimbAnimController extends AbstractAnimStateControllerDP {
    private AnimUtils.AnimationHolderData animationHolderData;
    private @Nullable AnimationHolder animationHolder = null;
    private AnimUtils.AnimationHolderData climbAnimationHolderData;
    private @Nullable AnimationHolder climbAnimationHolder = null;

    public ClimbAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public ClimbAnimController(@Nullable AnimUtils.AnimationHolderData normalAnimationHolderData, @Nullable AnimUtils.AnimationHolderData climbAnimationHolderData) {
        super();
        this.animationHolderData = AnimUtils.ensureAnimHolderDataNotNull(normalAnimationHolderData);
        this.climbAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(climbAnimationHolderData);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.getVelocity().getY() > 0.0d) {
            return climbAnimationHolder;
        } else {
            return animationHolder;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        this.climbAnimationHolder = this.climbAnimationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.climbAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "climbAnim", null);
        return this;
    }
}
