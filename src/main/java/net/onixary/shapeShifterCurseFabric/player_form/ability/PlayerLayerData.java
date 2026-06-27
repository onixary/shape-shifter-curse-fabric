package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerLayerData {
    public PlayerEntity player;

    public HashMap<Identifier, Identifier> layers = new HashMap<>();

    // 无需存储为NBT 修改形态时先修改targetLayers 之后调用函数来应用
    public HashMap<Identifier, Identifier> targetLayers = new HashMap<>();

    public PlayerLayerData(PlayerEntity player) {
        this.player = player;
    }

    public void toNBT(NbtCompound nbt) {

    }

    public void fromNBT(NbtCompound nbt) {

    }

    public void setLayerGroup(List<IFormLayerGroup> layerGroups) {
        List<Identifier> layerGroupsID = new ArrayList<>();
        for (IFormLayerGroup layerGroup : layerGroups) {
            layerGroupsID.add(layerGroup.getGroupID());
        }
        for (Identifier layerGroup : targetLayers.keySet()) {
            if (!layerGroupsID.contains(layerGroup)) {
                targetLayers.remove(layerGroup);
            }
        }
        for (IFormLayerGroup layerGroup : layerGroups) {
            if (!targetLayers.containsKey(layerGroup.getGroupID())) {
                IFormLayer layer = RegFormLayer.getLayer(layerGroup.transformLayerID(this.player, null));
                if (layer == null) {
                    throw new RuntimeException("Layer not found");
                }
                targetLayers.put(layerGroup.getGroupID(), layer.getID());
            }
        }
    }

    public void setLayer(Identifier groupID, Identifier layerID) {
        IFormLayerGroup layerGroup = RegFormLayer.getLayerGroup(groupID);
        if (layerGroup == null) {
            throw new RuntimeException("Layer group not found");
        }
        Identifier newLayerID = layerGroup.transformLayerID(this.player, layerID);
        IFormLayer layer = RegFormLayer.getLayer(newLayerID);
        if (layer == null) {
            throw new RuntimeException("Layer not found");
        }
        targetLayers.put(groupID, newLayerID);
    }

    public void __setLayer(Identifier groupID, Identifier layerID) {
        IFormLayerGroup layerGroup = RegFormLayer.getLayerGroup(groupID);
        if (layerGroup == null) {
            throw new RuntimeException("Layer group not found");
        }
        IFormLayer layer = RegFormLayer.getLayer(layerID);
        if (layer == null) {
            throw new RuntimeException("Layer not found");
        }
        targetLayers.put(groupID, layerID);
    }

    private List<Identifier> getMissingGroup() {
        List<Identifier> result = new ArrayList<>();
        for (Identifier group : layers.keySet()) {
            if (!targetLayers.containsKey(group)) {
                result.add(group);
            }
        }
        return result;
    }

    private List<Identifier> getExtraGroup() {
        List<Identifier> result = new ArrayList<>();
        for (Identifier group : targetLayers.keySet()) {
            if (!layers.containsKey(group)) {
                result.add(group);
            }
        }
        return result;
    }

    private void checkTargetGroupDataValid() {
        for (Identifier groupID : targetLayers.keySet()) {
            IFormLayerGroup group = RegFormLayer.getLayerGroup(groupID);
            if (group == null) {
                throw new RuntimeException("Layer group not found");
            }
            Identifier layerID = targetLayers.get(groupID);
            if (!group.getLayers().contains(layerID)) {
                targetLayers.put(groupID, group.transformLayerID(player, layerID));
            }
        }
    }

    public void apply() {
        checkTargetGroupDataValid();
        List<Identifier> missingGroup = getMissingGroup();
        missingGroup.forEach(group -> {
            IFormLayerGroup layerGroup = RegFormLayer.getLayerGroup(group);
            if (layerGroup != null) {
                layerGroup.onRemoveGroup(player, layers.get(group));
            }
        });
        List<Identifier> extraGroup = getExtraGroup();
        extraGroup.forEach(group -> {
            IFormLayerGroup layerGroup = RegFormLayer.getLayerGroup(group);
            // 新增部分如果还没有就肯定有问题了
            if (layerGroup != null) {
                layerGroup.onAddGroup(player, targetLayers.get(group));
            } else {
                throw new RuntimeException("Layer group not found");
            }
        });
        // TODO
        // 移除缺失的layer Power
        // 添加新增的layer Power
    }
}
