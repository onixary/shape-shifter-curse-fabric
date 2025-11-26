package net.onixary.shapeShifterCurseFabric.minion.mobs;

/* 弃用 不会做AzureLib模型 原先的模型不兼容 连测试都没法测试

import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class WolfMinionAnimator extends AzEntityAnimator<WolfMinion> {
    private static final Identifier ANIMATIONS = ShapeShifterCurseFabric.identifier(
            "animations/entity/wolf_minion.animation.json"
    );

    @Override
    public void registerControllers(AzAnimationControllerContainer<WolfMinion> animationControllerContainer) {
        animationControllerContainer.add(AzAnimationController.builder(this, "wolf_minion_controller").build());
    }

    @Override
    public @NotNull Identifier getAnimationLocation(WolfMinion minion) {
        return ANIMATIONS;
    }

    public static final AzCommand IDLE_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "idle",
            AzPlayBehaviors.LOOP
    );

    public static final AzCommand WALK_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "walk",
            AzPlayBehaviors.LOOP
    );

    public static final AzCommand JUMP_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "jump",
            AzPlayBehaviors.LOOP
    );

    public static final AzCommand FALL_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "fall",
            AzPlayBehaviors.LOOP
    );

    public static final AzCommand FLOAT_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "float",
            AzPlayBehaviors.LOOP
    );

    public static final AzCommand ATTACK_COMMAND = AzCommand.create(
            "wolf_minion_controller",
            "attack",
            AzPlayBehaviors.PLAY_ONCE
    );
}
 */

public class WolfMinionAnimator {
}