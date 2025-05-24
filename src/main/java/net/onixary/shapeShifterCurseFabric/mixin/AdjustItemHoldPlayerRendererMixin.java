package net.onixary.shapeShifterCurseFabric.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.onixary.shapeShifterCurseFabric.features.ExtraItemFeatureRenderer;
import net.onixary.shapeShifterCurseFabric.features.MouthItemFeature;
import net.onixary.shapeShifterCurseFabric.accessors.IEntityRenderDispatcherAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Environment(EnvType.CLIENT)
@Mixin(
        value = PlayerEntityRenderer.class,
        priority = 1000
)
public abstract class AdjustItemHoldPlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public AdjustItemHoldPlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.addFeature(new MouthItemFeature<>(this, this.dispatcher.getHeldItemRenderer()));
        ItemRenderer itemRenderer = ((IEntityRenderDispatcherAccessor) this.dispatcher).getItemRenderer();
        //this.addFeature(new ExtraItemFeatureRenderer<>(this, this.dispatcher, itemRenderer));
    }
}
