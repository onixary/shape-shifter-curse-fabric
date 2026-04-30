package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Spider1 extends PlayerFormBase {
    
    public Form_Spider1(Identifier formID) {
        super(formID);
    }


    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(
            PlayerEntity player, 
            AnimSystem.AnimSystemData animSystemData, 
            @NotNull Identifier animStateID) {
        
        AnimStateEnum state = AnimStateEnum.getStateEnum(animStateID);
        if (state != null) {
            switch (state) {
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
