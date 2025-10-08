package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FormDataPackReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(ShapeShifterCurseFabric.MOD_ID, "ssc_form");
    }

    @Override
    public void reload(ResourceManager manager) {
        // ssc_form
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
        // origins_power_extra
        FormAbilityManager.OriginExtraPowerRegistry.clear();
        manager.findResources("origins_power_extra", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
            Identifier ID = new Identifier(identifier.getNamespace(), identifier.getPath().replace(".json", "").substring(20));
            JsonObject ExtraPowerData;
            Identifier OriginID;
            List<Identifier> ExtraPowerIDs = new LinkedList<>();
            try {
                ExtraPowerData = JsonParser.parseString(new String(resource.getInputStream().readAllBytes())).getAsJsonObject();
                OriginID = Identifier.tryParse(ExtraPowerData.get("TargetOriginsID").getAsString());
                JsonArray PowerIDs = ExtraPowerData.get("ExtraPowers").getAsJsonArray();
                for (int i = 0; i < PowerIDs.size(); i++) {
                    ExtraPowerIDs.add(Identifier.tryParse(PowerIDs.get(i).getAsString()));
                }
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to load extra power data for " + ID);
                return;
            }
            if (!ExtraPowerIDs.isEmpty()) {
                FormAbilityManager.RegisterOriginExtraPower(ID, OriginID, ExtraPowerIDs);
            }
            ShapeShifterCurseFabric.LOGGER.info("Loaded extra power data for " + ID);
        });
    }
}
