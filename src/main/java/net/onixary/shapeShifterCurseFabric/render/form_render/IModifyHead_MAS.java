package net.onixary.shapeShifterCurseFabric.render.form_render;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;

public interface IModifyHead_MAS {
    void modifyHeadPart(PlayerEntity player, BipedEntityModel<?> model, FormModel formModel);
}
