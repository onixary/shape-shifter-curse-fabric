package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithSneakAnimController extends AbstractAnimStateControllerDP {
    private @NotNull AnimUtils.AnimationHolderData animationHolderData;
    private @Nullable AnimationHolder animationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData sneakAnimationHolderData;
    private @Nullable AnimationHolder sneakAnimationHolder = null;

    public WithSneakAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public WithSneakAnimController(@Nullable AnimUtils.AnimationHolderData normalAnimationHolderData, @Nullable AnimUtils.AnimationHolderData sneakAnimationHolderData) {
        super(null);
        this.animationHolderData = AnimUtils.ensureAnimHolderDataNotNull(normalAnimationHolderData);
        this.sneakAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(sneakAnimationHolderData);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.isSneaking()) {
            return sneakAnimationHolder;
        } else {
            return animationHolder;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        this.sneakAnimationHolder = this.sneakAnimationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.sneakAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "sneakAnim", null);
        return this;
    }
}
