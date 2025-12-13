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

public class SwimAnimController extends AbstractAnimStateControllerDP {
    private @NotNull AnimUtils.AnimationHolderData animationHolderData1 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder1 = null;
    private @NotNull AnimUtils.AnimationHolderData animationHolderData2 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder2 = null;

    public SwimAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public SwimAnimController(@Nullable AnimUtils.AnimationHolderData animationHolderDataFloat, @Nullable AnimUtils.AnimationHolderData animationHolderDataSwimming) {
        super(null);
        this.animationHolderData1 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataFloat);
        this.animationHolderData2 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataSwimming);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.isSwimming()) {
            return this.animationHolder2;
        } else {
            return this.animationHolder1;
        }
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder1 = this.animationHolderData1.build();
        this.animationHolder2 = this.animationHolderData2.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData1 = AnimUtils.readAnimInJson(jsonObject, "anim", null);
        this.animationHolderData2 = AnimUtils.readAnimInJson(jsonObject, "swimAnim", null);
        return this;
    }
}