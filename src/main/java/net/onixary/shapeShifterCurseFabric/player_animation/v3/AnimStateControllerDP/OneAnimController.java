package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.Nullable;

public class OneAnimController extends AbstractAnimStateControllerDP {
    AnimUtils.AnimationHolderData animationHolderData;
    AnimationHolder animationHolder = null;

    public OneAnimController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public OneAnimController(AnimUtils.AnimationHolderData animationHolderData) {
        super(null);
        this.animationHolderData = animationHolderData;
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return this.animationHolder;
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder = this.animationHolderData.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        if (jsonObject.has("anim") && jsonObject.get("anim").isJsonObject()) {
            this.animationHolderData = AnimUtils.readAnim(jsonObject.get("anim").getAsJsonObject());
        }
        return this;
    }
}
