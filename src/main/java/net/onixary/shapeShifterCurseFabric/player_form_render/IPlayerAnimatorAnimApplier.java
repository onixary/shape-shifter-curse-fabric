package net.onixary.shapeShifterCurseFabric.player_form_render;


import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface IPlayerAnimatorAnimApplier {
    default @Nullable IAnimation getTop() {return null;}
    default float getDelta() {return 0f;}
    default Vec3d getPositionForBone(String name) {return Vec3d.ZERO;}
}
