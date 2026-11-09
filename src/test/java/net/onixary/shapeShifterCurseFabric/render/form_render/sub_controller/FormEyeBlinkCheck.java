package net.onixary.shapeShifterCurseFabric.render.form_render.sub_controller;

import com.google.gson.JsonParser;
import java.util.List;

/** ./gradlew checkEyeBlink; no running Minecraft client required. */
public final class FormEyeBlinkCheck {
    public static void main(String[] args) {
        check("{}", List.of());
        check("{\"eye\":\"eyeRoot\"}", List.of("eyeRoot"));
        check("{\"eye\":[\"eyeRoot\"]}", List.of("eyeRoot"));
        check("{\"eye\":[\"upperEyes\",\"lowerEyes\",\"sideEyes\"]}",
                List.of("upperEyes", "lowerEyes", "sideEyes"));
        check("{\"eye\":[]}", List.of());
        var custom = parse("{\"eye\":[\"a\",\"b\"],\"open_scale\":0.8,\"close_scale\":0.02}");
        if (custom.OPEN_SCALE != 0.8f || custom.CLOSED_SCALE != 0.02f) {
            throw new AssertionError("Custom scales must be preserved");
        }
        for (String invalid : List.of("[null]", "[{}]", "[[\"eyeRoot\"]]", "[1]")) {
            try {
                parse("{\"eye\":" + invalid + "}");
                throw new AssertionError("Invalid eye array accepted: " + invalid);
            } catch (IllegalArgumentException expected) {
                // The animation-system loader logs this error and disables the controller.
            }
        }
        System.out.println("Eye blink configuration checks passed.");
    }

    private static FormEyeBlinkController parse(String json) {
        return new FormEyeBlinkController(JsonParser.parseString(json).getAsJsonObject());
    }

    private static void check(String json, List<String> expected) {
        var controller = parse(json);
        if (!controller.EYE_ROOT_BONES.equals(expected)) throw new AssertionError(json);
        if (controller.OPEN_SCALE != 1.0f || controller.CLOSED_SCALE != 0.01f) {
            throw new AssertionError("Default scales changed");
        }
        if (expected.isEmpty()) controller.update(null, null, 0.0f);
    }
}
