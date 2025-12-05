package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimStateControllerDP extends AbstractAnimStateController {
    public AbstractAnimStateControllerDP(@Nullable JsonObject jsonData) {
        super();
        if (jsonData != null) {
            loadFormJson(jsonData);
        }
    }

    public abstract AbstractAnimStateController loadFormJson(JsonObject jsonObject);  // 返回自身
}
