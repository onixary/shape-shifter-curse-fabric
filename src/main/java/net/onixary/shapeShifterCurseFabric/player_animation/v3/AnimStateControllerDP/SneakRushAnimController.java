package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SneakRushAnimController extends AbstractAnimStateControllerDP {

    private @NotNull AnimUtils.AnimationHolderData animationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder animationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData sneakAnimationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder sneakAnimationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData sneakRushAnimationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder sneakRushAnimationHolder = null;

    public SneakRushAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public SneakRushAnimController(@Nullable AnimUtils.AnimationHolderData animationHolderData, @Nullable AnimUtils.AnimationHolderData sneakAnimationHolderData, AnimUtils.AnimationHolderData sneakRushAnimationHolderData) {
        super(null);
        this.animationHolderData = AnimUtils.ensureAnimHolderDataNotNull(animationHolderData);
        this.sneakAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(sneakAnimationHolderData);
        this.sneakRushAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(sneakRushAnimationHolderData);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.isSneaking()) {
            if (player.getHungerManager().getFoodLevel() >= 6) {
                return sneakRushAnimationHolder;
            } else {
                return sneakAnimationHolder;
            }
        } else {
            return animationHolder;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        this.sneakAnimationHolder = this.sneakAnimationHolderData.build();
        this.sneakRushAnimationHolder = this.sneakRushAnimationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.sneakAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "sneakAnim", null);
        this.sneakRushAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "sneakRushAnim", null);
        return this;
    }
}
