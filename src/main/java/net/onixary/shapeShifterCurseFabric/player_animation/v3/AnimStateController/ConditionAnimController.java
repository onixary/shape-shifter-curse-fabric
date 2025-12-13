package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class ConditionAnimController extends AbstractAnimStateController {
    private List<Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimationHolder>> animConditionList;
    private final List<Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimUtils.AnimationHolderData>> animConditionBuilder;
    private @Nullable AnimationHolder defaultAnimation;
    private final @NotNull AnimUtils.AnimationHolderData defaultAnimationData;

    public ConditionAnimController(List<Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimUtils.AnimationHolderData>> animConditionBuilder, @NotNull AnimUtils.AnimationHolderData defaultAnimationData) {
        this.animConditionBuilder = animConditionBuilder;
        this.defaultAnimationData = AnimUtils.ensureAnimHolderDataNotNull(defaultAnimationData);
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        List<Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimationHolder>> animConditionList = new LinkedList<>();
        for (Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimUtils.AnimationHolderData> pair : this.animConditionBuilder) {
            animConditionList.add(new Pair<>(pair.getLeft(), AnimUtils.ensureAnimHolderDataNotNull(pair.getRight()).build()));
        }
        this.animConditionList = animConditionList;
        this.defaultAnimation = this.defaultAnimationData.build();
        super.registerAnim(player, data);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        for (Pair<BiFunction<PlayerEntity, AnimSystem.AnimSystemData, Boolean>, AnimationHolder> pair : this.animConditionList) {
            if (pair.getLeft().apply(player, data)) {
                return pair.getRight();
            }
        }
        return this.defaultAnimation;
    }
}
