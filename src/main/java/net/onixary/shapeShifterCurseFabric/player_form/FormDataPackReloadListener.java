package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.io.IOException;

public class FormDataPackReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(ShapeShifterCurseFabric.MOD_ID, "ssc_form");
    }

    @Override
    public void reload(ResourceManager manager) {
        RegPlayerForms.ClearAllDynamicPlayerForms();
        manager.findResources("ssc_form", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
            // shape-shifter-curse:ssc_form/example.json -> shape-shifter-curse:example
            Identifier formID = new Identifier(identifier.getNamespace(), identifier.getPath().replace(".json", "").substring(9));
            JsonObject formData;
            try {
                formData = JsonParser.parseString(new String(resource.getInputStream().readAllBytes())).getAsJsonObject();
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to load form data for " + formID);
                return;
            }
            RegPlayerForms.registerDynamicPlayerForm(PlayerFormDynamic.of(formID, formData));
            ShapeShifterCurseFabric.LOGGER.info("Loaded form data for " + formID);
        });
    }
}
