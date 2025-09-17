package net.onixary.shapeShifterCurseFabric.team;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.PillagerFriendlyPower;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

// 为了避免与Team功能冲突已弃用，替换为其他逻辑
// Deprecated to avoid conflicts with Team functionality, replaced with other logic

public class PlayerTeamHandler {
    private static PlayerFormBase currentForm;
    public static void updatePlayerTeam(ServerPlayerEntity player) {
        currentForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        updateSorceryTeam(player);
    }


    private static void updateSorceryTeam(ServerPlayerEntity player) {
        if(MobTeamManager.sorceryTeam == null) {
            // 确保队伍已注册
            MobTeamManager.registerTeam(player.getServerWorld());
        }
        if (PowerHolderComponent.hasPower(player, PillagerFriendlyPower.class)) {
            player.getScoreboard().addPlayerToTeam(player.getEntityName(), MobTeamManager.sorceryTeam);
        } else {
            // 从队伍中移除（如果是成员）
            Team team = (Team) player.getScoreboardTeam();
            if (team != null && team.getName().equals(MobTeamManager.SORCERY_TEAM_NAME)) {
                player.getScoreboard().removePlayerFromTeam(player.getEntityName(), team);
            }
        }
    }
}

