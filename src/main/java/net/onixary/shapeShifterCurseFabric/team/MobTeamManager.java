package net.onixary.shapeShifterCurseFabric.team;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;

// 为了避免与Team功能冲突已弃用，替换为其他逻辑
// Deprecated to avoid conflicts with Team functionality, replaced with other logic
public class MobTeamManager {
    public static final String SORCERY_TEAM_NAME = "sorcery_team";
    public static Team sorceryTeam;

    public static void registerTeam(ServerWorld world) {
        Scoreboard scoreboard = world.getScoreboard();

        // 创建队伍（如果不存在）
        if (scoreboard.getTeam(SORCERY_TEAM_NAME) == null) {
            sorceryTeam = scoreboard.addTeam(SORCERY_TEAM_NAME);

            // 配置队伍属性
            sorceryTeam.setFriendlyFireAllowed(false);          // 队友之间不会误伤
            sorceryTeam.setShowFriendlyInvisibles(true);       // 可以看到隐形的队友
        } else {
            sorceryTeam = scoreboard.getTeam(SORCERY_TEAM_NAME);
        }
    }
}

