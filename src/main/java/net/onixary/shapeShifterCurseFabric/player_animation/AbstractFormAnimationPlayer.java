package net.onixary.shapeShifterCurseFabric.player_animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Set;

public abstract class AbstractFormAnimationPlayer {

    public abstract KeyframeAnimation getFormAnimToPlay(PlayerAnimState currentState);
    public abstract void registerAnims();





}
