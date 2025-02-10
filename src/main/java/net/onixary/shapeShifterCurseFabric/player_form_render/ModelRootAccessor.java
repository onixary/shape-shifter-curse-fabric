package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.minecraft.client.model.ModelPart;

public interface ModelRootAccessor {
    public default void originalFur$setProcessedSlim(boolean state){}
    public default boolean originalFur$hasProcessedSlim(){return true;}
    public ModelPart originalFur$getRoot();
    public default boolean originalFur$isSlim() {
        return false;
    };
    public default boolean originalFur$justUsedElytra(){return false;}
    public default void originalFur$setJustUsedElytra(boolean b){}
    public default float originalFur$elytraPitch(){return 0;}
    public default void originalFur$setElytraPitch(float f){}
}
