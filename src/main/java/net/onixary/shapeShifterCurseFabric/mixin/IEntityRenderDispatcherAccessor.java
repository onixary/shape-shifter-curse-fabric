package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderDispatcher.class) // 指定目标类
public interface IEntityRenderDispatcherAccessor {
    @Accessor("itemRenderer") // 访问目标类中的字段
    ItemRenderer getItemRenderer();
}
