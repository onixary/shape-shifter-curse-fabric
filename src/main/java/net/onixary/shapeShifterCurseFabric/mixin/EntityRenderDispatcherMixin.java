package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin implements IEntityRenderDispatcherAccessor {
    @Shadow
    private ItemRenderer itemRenderer;

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }
}
