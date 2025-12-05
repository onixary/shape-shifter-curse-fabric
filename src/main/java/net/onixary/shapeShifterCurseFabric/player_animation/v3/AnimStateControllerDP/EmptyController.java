package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateControllerDP;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

public class EmptyController extends AbstractAnimStateControllerDP {
    public EmptyController() {
        this(null);
    }

    public EmptyController(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    @Override
    public boolean isRegistered() {
        return true;
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return null;
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonData) {
        return this;
    }
}