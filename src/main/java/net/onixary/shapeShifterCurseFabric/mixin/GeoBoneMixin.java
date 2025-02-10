package net.onixary.shapeShifterCurseFabric.mixin;

import mod.azure.azurelib.cache.object.GeoBone;
import net.onixary.shapeShifterCurseFabric.player_form_render.IGeoBone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GeoBone.class, remap = false)
public class GeoBoneMixin implements IGeoBone {
    @Shadow private boolean hidden;
    @Unique
    boolean orif$dHiddenByDefault = false;
    @Override
    public boolean originfurs$isHiddenByDefault() {
        return IGeoBone.super.originfurs$isHiddenByDefault();
    }
    @Inject(method="<init>", at=@At("TAIL"))
    void initMixin(GeoBone parent, String name, Boolean mirror, Double inflate, Boolean dontRender, Boolean reset, CallbackInfo ci) {
        orif$dHiddenByDefault = this.hidden;
    }

}
