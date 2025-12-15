package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimStateControllerDP extends AbstractAnimStateController {
    public AbstractAnimStateControllerDP(@Nullable JsonObject jsonData) {
        super();
        if (jsonData == null) {
            jsonData = new JsonObject();
            ShapeShifterCurseFabric.LOGGER.warn("jsonData is null");
        }
        loadFormJson(jsonData);
    }

    public AbstractAnimStateControllerDP() {
        super();
    }

    public abstract AbstractAnimStateController loadFormJson(JsonObject jsonObject);  // 返回自身
}
