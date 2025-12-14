package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RushJumpAnimController extends AbstractAnimStateControllerDP {
    private @NotNull AnimUtils.AnimationHolderData animationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder animationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData sneakAnimationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder sneakAnimationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData rushJumpAnimationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder rushJumpAnimationHolder = null;
    private @NotNull AnimUtils.AnimationHolderData sneakRushJumpAnimationHolderData = AnimUtils.EMPTY_ANIM;
    private @Nullable AnimationHolder sneakRushJumpAnimationHolder = null;

    public RushJumpAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public RushJumpAnimController(@Nullable AnimUtils.AnimationHolderData animationHolderData, @Nullable AnimUtils.AnimationHolderData sneakAnimationHolderData, AnimUtils.AnimationHolderData rushJumpAnimationHolderData, AnimUtils.AnimationHolderData sneakRushJumpAnimationHolder) {
        super(null);
        this.animationHolderData = AnimUtils.ensureAnimHolderDataNotNull(animationHolderData);
        this.sneakAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(sneakAnimationHolderData);
        this.rushJumpAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(rushJumpAnimationHolderData);
        this.sneakRushJumpAnimationHolderData = AnimUtils.ensureAnimHolderDataNotNull(sneakRushJumpAnimationHolderData);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (Math.abs(player.getVelocity().z) > 0.15 || Math.abs(player.getVelocity().x) > 0.15) {
            if (player.isSneaking()) {
                return sneakRushJumpAnimationHolder;
            } else {
                return rushJumpAnimationHolder;
            }
        } else {
            if (player.isSneaking()) {
                return sneakAnimationHolder;
            } else {
                return animationHolder;
            }
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        this.sneakAnimationHolder = this.sneakAnimationHolderData.build();
        this.rushJumpAnimationHolder = this.rushJumpAnimationHolderData.build();
        this.sneakRushJumpAnimationHolder = this.sneakRushJumpAnimationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.sneakAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "sneakAnim", null);
        this.rushJumpAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "rushJumpAnim", null);
        this.sneakRushJumpAnimationHolderData = AnimUtils.readAnimInJson(jsonObject, "sneakRushJumpAnim", null);
        return this;
    }
}
