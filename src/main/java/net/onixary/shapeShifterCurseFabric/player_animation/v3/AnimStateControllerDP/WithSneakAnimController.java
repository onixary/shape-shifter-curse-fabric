package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.Nullable;

public class WithSneakAnimController extends AbstractAnimStateControllerDP {
    AnimUtils.AnimationHolderData animationHolderData;
    AnimationHolder animationHolder = null;
    AnimUtils.AnimationHolderData sneakAnimationHolderData;
    AnimationHolder sneakAnimationHolder = null;

    public WithSneakAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public WithSneakAnimController(AnimUtils.AnimationHolderData animationHolderData, AnimUtils.AnimationHolderData sneakAnimationHolderData) {
        super(null);
        this.animationHolderData = animationHolderData;
        this.sneakAnimationHolderData = sneakAnimationHolderData;
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
        if (jsonObject.has("anim") && jsonObject.get("anim").isJsonObject()) {
            this.animationHolderData = AnimUtils.readAnim(jsonObject.get("anim").getAsJsonObject());
        }
        if (jsonObject.has("sneakAnim") && jsonObject.get("sneakAnim").isJsonObject()) {
            this.sneakAnimationHolderData = AnimUtils.readAnim(jsonObject.get("sneakAnim").getAsJsonObject());
        }
        return this;
    }
}
