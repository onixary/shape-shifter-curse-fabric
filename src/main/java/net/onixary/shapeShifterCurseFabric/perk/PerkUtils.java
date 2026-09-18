package net.onixary.shapeShifterCurseFabric.perk;

import com.google.common.base.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.FormAttunerBlock;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.FormAttunerBlockEntity;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
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

    public static @Nullable List<IPerk> getPlayerPerksObject(PlayerEntity player, Identifier perkTreeID) {
        // 仅服务器端 客户端不保证数据能完整拿到
        List<Identifier> perkList = getPlayerPerks(player, perkTreeID);
        if (perkList == null) return null;
        List<IPerk> perkDataList = new ArrayList<>();
        for (Identifier perkID : perkList) {
            IPerk perkData = RegPerks.getPerk(perkID);
            if (perkData != null) {
                perkDataList.add(perkData);
            }
        }
        return perkDataList;
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

        int xpCost = player.getAbilities().creativeMode ? 0 : perkData.getXpCost();
        if (player.totalExperience < xpCost) {
            return;
        }

        PerkTree.PerkNode node = perkTree.getNode(perkID);
        if (node == null) return;
        if (!node.dependentPerkIDs.isEmpty()) {
            List<Identifier> playerPerkList = getPlayerPerks(player, perkTreeID);
            if (playerPerkList == null) return;
            for (Identifier dependentPerkID : node.dependentPerkIDs) {
                if (!playerPerkList.contains(dependentPerkID)) return;
            }
        }
        int tier = node.tier;
        // 感觉Tier0在无诅咒之月可以点可以作为特性使用 可以在tier0设置一些特殊的Perk
        if (tier > 0 && !isCanGainPerk(player)) {
            return;
        }
        @Nullable FormAttunerBlockEntity lastUsedAttuner = FormAttunerBlock.getPlayerLastUsedAttuner(player);
        if (lastUsedAttuner == null || lastUsedAttuner.level < tier) {
            return;
        }

        if (perkData.canGain(player, component.nowForm)) {
            player.addExperience(-xpCost);
            __addPerk(player, perkTreeID, perkID);
        }
        removeInValidPerk(player, perkTreeID);
    }

    public static boolean isCanGainPerk(PlayerEntity player) {
        // 仅检测从客户端提交的加点请求 服务器端的加点请求直接过 所以这里只能加环境检测
        World world = player.getWorld();
        if (world.getRegistryKey() != World.OVERWORLD) {
            return false;
        }
        if (!CursedMoon.isInCursedMoon(world)) {
            return false;
        }
        return true;
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

    public static HashMap<Identifier, Boolean> getPlayerPerkAvailability(PlayerEntity player) {
        PerkTree perkTree = getPlayerNowPerkTree(player);
        if (perkTree == null) return new HashMap<>();
        HashMap<Identifier, Boolean> perkAvailability = new HashMap<>();
        for (Identifier perkID : perkTree.getAllPerks()) {
            IPerk perkData = RegPerks.getPerk(perkID);
            if (perkData != null) {
                perkAvailability.put(perkID, perkData.canGain(player, PlayerFormComponent.COMPONENT.get(player).nowForm));
            }
        }
        return perkAvailability;
    }
}
