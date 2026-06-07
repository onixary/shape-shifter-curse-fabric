package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface ModifyCapeRender {
    public Vec3d getCapeIdleLoc(AbstractClientPlayerEntity player);

    public float getCapeBaseRotateAngle(AbstractClientPlayerEntity player);

    public boolean NeedModifyXRotationAngle();
}
