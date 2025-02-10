package net.onixary.shapeShifterCurseFabric.player_form_render;

import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Vec3d;

public interface IMojModelPart {
    default Vec3d originfurs$getPosition() {return Vec3d.ZERO;}
    default Vec3d originfurs$getRotation() {return Vec3d.ZERO;}
    default Vec3d originfurs$getScale() {return Vec3d.ZERO;}
    default Vec3f originfurs$getPositionF() {return Vec3f.ZERO;}
    default Vec3f originfurs$getRotationF() {return Vec3f.ZERO;}
    default Vec3f originfurs$getScaleF() {return Vec3f.ZERO;}
    default ModelPart originfurs$getHolderPart() {return null;}
}
