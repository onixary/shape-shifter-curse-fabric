package net.onixary.shapeShifterCurseFabric.player_animation.v2;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

public abstract class AbstractFormAnimationPlayer {

    public abstract KeyframeAnimation getFormAnimToPlay(PlayerAnimState currentState);
    public abstract void registerAnims();





}
