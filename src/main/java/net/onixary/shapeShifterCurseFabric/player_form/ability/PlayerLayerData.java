package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerLayerData {
    public PlayerEntity player;

    public HashMap<IFormLayerGroup, IFormLayer> layers = new HashMap<>();

    // 无需存储为NBT 修改形态时先修改targetLayers 之后调用函数来应用
    public HashMap<IFormLayerGroup, IFormLayer> targetLayers = new HashMap<>();

    public PlayerLayerData(PlayerEntity player) {
        this.player = player;
    }

    public void toNBT(NbtCompound nbt) {

    }

    public void fromNBT(NbtCompound nbt) {

    }

    public void setLayerGroup(List<IFormLayerGroup> layerGroups) {
        for (IFormLayerGroup layerGroup : targetLayers.keySet()) {
            if (!layerGroups.contains(layerGroup)) {
                targetLayers.remove(layerGroup);
            }
        }
        for (IFormLayerGroup layerGroup : layerGroups) {
            if (!targetLayers.containsKey(layerGroup)) {
                IFormLayer layer = RegFormLayer.getLayer(layerGroup.transformLayerID(this.player, null));
                if (layer == null) {
                    throw new RuntimeException("Layer not found");
                }
                targetLayers.put(layerGroup, layer);
            }
        }
    }

    public void setLayer(IFormLayerGroup group, IFormLayer layer) {
        if (!targetLayers.containsKey(group)) {
            throw new RuntimeException("Layer group not exists");
        }
        targetLayers.put(group, layer);
    }

    private List<IFormLayerGroup> getMissingGroup() {
        List<IFormLayerGroup> result = new ArrayList<>();
        for (IFormLayerGroup group : layers.keySet()) {
            if (!targetLayers.containsKey(group)) {
                result.add(group);
            }
        }
        return result;
    }

    private List<IFormLayerGroup> getExtraGroup() {
        List<IFormLayerGroup> result = new ArrayList<>();
        for (IFormLayerGroup group : targetLayers.keySet()) {
            if (!layers.containsKey(group)) {
                result.add(group);
            }
        }
        return result;
    }

    public void apply() {
        List<IFormLayerGroup> missingGroup = getMissingGroup();
        missingGroup.forEach(group -> {
            group.onRemoveGroup(player, layers.get(group).getID());
        });
        List<IFormLayerGroup> extraGroup = getExtraGroup();
        extraGroup.forEach(group -> {
            group.onAddGroup(player, targetLayers.get(group).getID());
        });
        // TODO
        // 移除缺失的layer Power
        // 添加新增的layer Power
    }
}
