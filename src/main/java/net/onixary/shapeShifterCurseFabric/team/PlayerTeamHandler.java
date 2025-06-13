package net.onixary.shapeShifterCurseFabric.team;

import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

public class PlayerTeamHandler {
    private static PlayerForms currentForm;
    public static void updatePlayerTeam(ServerPlayerEntity player) {
        currentForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        updateSorceryTeam(player);
    }


    private static void updateSorceryTeam(ServerPlayerEntity player) {
        if(MobTeamManager.sorceryTeam == null) {
            // 确保队伍已注册
            MobTeamManager.registerTeam(player.getServerWorld());
        }

        if (currentForm == PlayerForms.FAMILIAR_FOX_3 || currentForm == PlayerForms.FAMILIAR_FOX_2 || currentForm == PlayerForms.FAMILIAR_FOX_1) {
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

