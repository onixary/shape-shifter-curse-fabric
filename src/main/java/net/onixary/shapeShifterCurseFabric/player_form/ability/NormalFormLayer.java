package net.onixary.shapeShifterCurseFabric.player_form.ability;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NormalFormLayer implements IFormLayer {
    private Identifier layerID = null;
    private List<Identifier> powerID = new ArrayList<>();

    @Override
    public @NotNull Identifier getID() {
        return layerID;
    }

    public NormalFormLayer setID(@NotNull Identifier new_id) {
        this.layerID = new_id;
        return this;
    }

    @Override
    public void __setID(@NotNull Identifier id) {
        this.setID(id);
    }

    @Override
    public @NotNull List<Identifier> getPowerID(@Nullable PlayerEntity player) {
        return powerID;
    }

    public NormalFormLayer setPower(@NotNull List<Identifier> powerIDList) {
        this.powerID = powerIDList;
        return this;
    }

    public NormalFormLayer setPower(@NotNull Identifier... powerID) {
        this.powerID.clear();
        Collections.addAll(this.powerID, powerID);
        return this;
    }

    @Override
    public void __setPowerID(@NotNull List<Identifier> powerIDList) {
        this.setPower(powerID);
    }

    /*
    {
        "load_priority": 0,      // 加载数据包时读取 最后仅会执行一次fromJson
        "id": "ssc:layer_id",    // 用于覆盖由文件名自动获取的ID 没什么用
        "power": [
            "ssc:power_1",
            "ssc:power_2",
            ...
        ]
    }
    */

    // 数据包用 后续可能会把Layers给迁移到硬编码(差不多和形态系统一样 主硬编码 副数据包)
    public static NormalFormLayer fromJson(@NotNull Identifier id, @NotNull JsonObject json) {
        NormalFormLayer layer = new NormalFormLayer();
        if (json.has("id")) {
            layer.setID(new Identifier(json.get("id").getAsString()));
        } else {
            layer.setID(id);
        }
        JsonObject powerJson = json.getAsJsonObject("power");
        List<Identifier> powerID = new ArrayList<>();
        for (String key : powerJson.keySet()) {
            powerID.add(new Identifier(key));
        }
        layer.setPower(powerID);
        return layer;
    }
}
