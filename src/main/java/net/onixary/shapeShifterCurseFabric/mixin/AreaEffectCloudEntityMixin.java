package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AreaEffectCloudEntity.class)
public class AreaEffectCloudEntityMixin implements CTPUtils.CTPFormIDHolder {
    @Unique
    private Identifier ctpFormID = null;

    @Final
    @Shadow
    private Map<Entity, Integer> affectedEntities;

    @Override
    public Identifier getCTPFormID() {
        return this.ctpFormID;
    }

    @Override
    public void setCTPFormID(Identifier formID) {
        this.ctpFormID = formID;
    }

    @Inject(method="tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (((AreaEffectCloudEntity)(Object)this).age % 5 == 0) {
            if (this.ctpFormID != null) {
                for (Map.Entry<Entity, Integer> entry : this.affectedEntities.entrySet()) {
                    if (entry.getKey() instanceof PlayerEntity player) {
                        CTPUtils.setTransformativePotionForm(player, this.ctpFormID);
                    }
                }
            }
        }
    }
}
