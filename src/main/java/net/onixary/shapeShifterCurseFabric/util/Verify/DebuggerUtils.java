package net.onixary.shapeShifterCurseFabric.util.Verify;

// 由于我设计上等级3可以用命令修改其他的赞助者功能 所以需要保护

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

public class DebuggerUtils {
    // 只需要维护调试等级1和2的命令 等级3仅开发者可用 炸了就炸了 行为异常不计入Bug

    public static int getDebuggerLevel(@Nullable CommandContext<ServerCommandSource> commandContext, @Nullable PlayerEntity player) {
        int maxLevel = 0;
        if (commandContext != null && commandContext.getSource().hasPermissionLevel(2)) {
            maxLevel = 1;
        }
        if (ShapeShifterCurseFabric.commonConfig.enableDebugCommand) {
            maxLevel = 2;
        }
        if (player != null) {
            maxLevel = Math.max(maxLevel, DebuggerDataSegment.getLevel(player));
        }
        return maxLevel;
    }

    public static boolean canExecute(@Nullable CommandContext<ServerCommandSource> commandContext, @Nullable PlayerEntity player, int requireLevel) {
        return getDebuggerLevel(commandContext, player) >= requireLevel;
    }
}
