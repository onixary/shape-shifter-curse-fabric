package net.onixary.shapeShifterCurseFabric.perk;

import com.google.common.base.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerkUtils {
    public static HashMap<Identifier, List<Identifier>> getPlayerPerks(PlayerEntity player) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        return component.formPerkMap;
    }

    public static @Nullable List<Identifier> getPlayerPerks(PlayerEntity player, Identifier perkTreeID) {
        return getPlayerPerks(player).get(perkTreeID);
    }

    public static void removeInValidPerk(PlayerEntity player, Identifier perkTreeID) {
        if (!(player instanceof ServerPlayerEntity playerEntity)) return;
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        PerkTree perkTree = RegPerks.getPerkTree(perkTreeID);
        if (perkTree == null) {
            if (component.formPerkMap.containsKey(perkTreeID)) {
                component.formPerkMap.remove(perkTreeID);
                component.sync();
            }
            return;
        }
        List<Identifier> playerPerkList = component.formPerkMap.get(perkTreeID);
        if (playerPerkList == null) return;
        List<Identifier> validPerkList = perkTree.getAllPerks();
        List<Identifier> finalPerks = new ArrayList<>();
        for (Identifier playerPerkID : playerPerkList) {
            if (validPerkList.contains(playerPerkID) && RegPerks.getPerk(playerPerkID) != null) {
                finalPerks.add(playerPerkID);
            }
        }
        component.formPerkMap.put(perkTreeID, finalPerks);
        component.sync();
    }

    public static void __addPerk(PlayerEntity player, Identifier perkTreeID, Identifier perkID) {
        IPerk perkData = RegPerks.getPerk(perkID);
        if (perkData == null) return;
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        List<Identifier> perkList = component.formPerkMap.computeIfAbsent(perkTreeID, k -> new ArrayList<>());
        if (!perkData.canRepeat()) {
            perkList.add(perkID);
        }
        component.sync();
        perkData.onGain(player, component.nowForm);
    }

    public static void addPerk(PlayerEntity player, Identifier perkTreeID, Identifier perkID) {
        if (!(player instanceof ServerPlayerEntity playerEntity)) {
            ModPacketsS2C.sendAddPerk(perkTreeID, perkID);
            return;
        }
        IPerk perkData = RegPerks.getPerk(perkID);
        if (perkData == null) return;
        PerkTree perkTree = RegPerks.getPerkTree(perkTreeID);
        if (perkTree == null) return;
        if (!perkTree.getAllPerks().contains(perkID)) return;
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        if (perkData.canGain(player, component.nowForm)) {
            __addPerk(player, perkTreeID, perkID);
        }
        removeInValidPerk(player, perkTreeID);
    }

    public static void addPerkFromClient(PlayerEntity player, Identifier perkTreeID, Identifier perkID) {
        if (!(player instanceof ServerPlayerEntity playerEntity)) return;
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        if (!Objects.equal(perkTreeID, component.nowPerkTree)) return;
        IPerk perkData = RegPerks.getPerk(perkID);
        if (perkData == null) return;
        PerkTree perkTree = RegPerks.getPerkTree(perkTreeID);
        if (perkTree == null) return;
        if (!perkTree.getAllPerks().contains(perkID)) return;

        PerkTree.PerkNode node = perkTree.getNode(perkID);
        if (node == null) return;
        if (node.dependentPerkID != null) {
            List<Identifier> playerPerkList = getPlayerPerks(player, perkTreeID);
            if (playerPerkList == null || !playerPerkList.contains(node.dependentPerkID)) return;
        }
        int tier = node.tier;
        // TODO tier 判断 需要给升级方块加个玩家UUID表 记录最后一个使用的升级方块等级
        __addPerk(player, perkTreeID, perkID);
        removeInValidPerk(player, perkTreeID);
    }

    public static void loadAllPerk(PlayerEntity player, Identifier perkTreeID) {
        if (!(player instanceof ServerPlayerEntity playerEntity)) return;
        removeInValidPerk(player, perkTreeID);
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        List<Identifier> perkList = component.formPerkMap.get(perkTreeID);
        if (perkList == null) return;
        for (Identifier perkID : perkList) {
            IPerk perkData = RegPerks.getPerk(perkID);
            if (perkData != null) {
                perkData.onLoad(player, component.nowForm);
            }
        }
    }

    public static Identifier getPlayerNowPerkTreeID(PlayerEntity player) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        return component.nowPerkTree;
    }

    public static @Nullable PerkTree getPlayerNowPerkTree(PlayerEntity player) {
        Identifier perkTreeID = getPlayerNowPerkTreeID(player);
        return RegPerks.getPerkTree(perkTreeID);
    }

    public static void setPlayerNowPerkTreeID(PlayerEntity player, Identifier perkTreeID) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.nowPerkTree = perkTreeID;
        component.sync();
    }
}
